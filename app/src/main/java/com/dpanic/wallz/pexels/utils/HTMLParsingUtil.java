package com.dpanic.wallz.pexels.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.dpanic.wallz.pexels.data.model.Category;
import com.dpanic.wallz.pexels.data.model.Image;
import com.dpanic.wallz.pexels.data.model.ImageDetail;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by dpanic on 10/6/2016.
 * Project: Pexels
 */

public class HTMLParsingUtil {
    private static final int CONNECT_TIMEOUT = 5000;

    private static Observable<ArrayList<Image>> getImageListFromUrl(final String url) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Image>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Image>> subscriber) {
                try {
                    if (isNetworkConnecting()) {
                        subscriber.onNext(getImages(url));
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new SocketTimeoutException());
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static ArrayList<Image> getImages(String url) throws Exception {
        ArrayList<Image> list = new ArrayList<>();
        Document doc = Jsoup.connect(url).timeout(CONNECT_TIMEOUT).get();

        Elements elements = doc.select("article.photo-item a");
        for (Element element : elements) {
            String link = element.attr("href");
            String pexelId = link.substring(link.lastIndexOf("-") + 1, link.length() - 1);
            String detailLink = Constants.ROOT_LINK + link;

            Element imgElement = element.select("img").get(0);
            String name = imgElement.attr("alt");
            String mediumLink = imgElement.attr("src");
            String largeImgLink = mediumLink.replace("350", "650");
            String originalImgLink = mediumLink.substring(0, mediumLink.indexOf("?"));

            Timber.d("url = " + url);

            list.add(new Image(pexelId, name, originalImgLink, largeImgLink, detailLink, "", false));
        }

        return list;
    }

    public static Observable<Image> getImageFromDetailLink(final String detailLink) {
        return Observable.create(new Observable.OnSubscribe<Image>() {
            @Override
            public void call(Subscriber<? super Image> subscriber) {
                try {
                    Document doc = Jsoup.connect(detailLink).timeout(CONNECT_TIMEOUT).get();

                    String pexelId = detailLink.substring(detailLink.lastIndexOf("-") + 1, detailLink.length() - 1);
                    Elements titleEle = doc.select("section.photo-details div.l-content div.box h1.box__title");
                    String name;
                    if (titleEle.size() > 0) {
                        name = titleEle.get(0).text();
                    } else {
                        name = doc.select("head title").get(0).text();
                    }
                    String originalLink = doc.select("div.photo-modal__container.js-insert-featured-badge a").get(0)
                            .attr("href");
                    String largeLink = doc.select("a.js-download picture img.image-section__image").get(0).attr("src");

                    Image image = new Image(pexelId, name, originalLink, largeLink, detailLink, "", false);
                    subscriber.onNext(image);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<ArrayList<Image>> getImageListStartAt(String mainLink, int page) {
        Observable<ArrayList<Image>> obs1 = getImageListFromUrl(mainLink + "?page=" + page);
        obs1.subscribeOn(Schedulers.io());
        Observable<ArrayList<Image>> obs2 = getImageListFromUrl(mainLink + "?page=" + (page + 1));
        obs2.subscribeOn(Schedulers.io());
        Observable<ArrayList<Image>> obs3 = getImageListFromUrl(mainLink + "?page=" + (page + 2));
        obs3.subscribeOn(Schedulers.io());

        return Observable.zip(obs1, obs2, obs3,
                              new Func3<ArrayList<Image>, ArrayList<Image>, ArrayList<Image>, ArrayList<Image>>() {

                                  @Override
                                  public ArrayList<Image> call(ArrayList<Image> o, ArrayList<Image> o2,
                                                               ArrayList<Image> o3) {
                                      o.addAll(o2);
                                      o.addAll(o3);
                                      return o;
                                  }
                              }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    // trick to check url exists or not but need to find another way
    @SuppressWarnings("unused")
    private static boolean isURLExists(String urlName) throws Exception {
        //        SSLContext sslcontext = SSLContext.getInstance("TLSv1");
        //
        //        sslcontext.init(null,
        //                        null,
        //                        null);
        //        SSLSocketFactory noSSLv3Factory = new NoSSLv3Factory(sslcontext.getSocketFactory());
        //
        //        HttpsURLConnection.setDefaultSSLSocketFactory(noSSLv3Factory);
        //
        //        HttpsURLConnection con = (HttpsURLConnection) new URL(urlName).openConnection();
        //
        //        HttpsURLConnection.setFollowRedirects(false);
        //        con.setConnectTimeout(1000);
        //        con.setReadTimeout(1000);
        //        con.setRequestMethod("HEAD");
        //
        //        return con.getResponseCode() == HttpsURLConnection.HTTP_OK;

        //        Runtime runtime = Runtime.getRuntime();
        //        Process proc = runtime.exec("ping " + urlName); //<- Try ping -c 1 www.serverURL.com
        //        int mPingResult = proc.waitFor();
        //        if (mPingResult == 0) {
        //            Log.e("thanh.dao", "isURLExists: " + true);
        //            return true;
        //        } else {
        //            Log.e("thanh.dao", "isURLExists: " + false);
        //            return false;
        //        }

        Jsoup.connect(urlName).timeout(CONNECT_TIMEOUT).get();
        return true;
    }

    public static Observable<ImageDetail> getImageDetailFromUrl(final String url) {
        return Observable.create(new Observable.OnSubscribe<ImageDetail>() {
            @Override
            public void call(Subscriber<? super ImageDetail> subscriber) {
                try {
                    subscriber.onNext(parseImageDetail(url));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private static ImageDetail parseImageDetail(String url) throws Exception {
        Document doc = Jsoup.connect(url).timeout(CONNECT_TIMEOUT).get();

        Elements titleEle = doc.select("section.photo-details div.l-content div.box h1.box__title");
        String title;
        if (titleEle.size() > 0) {
            title = titleEle.get(0).text();
        } else {
            title = doc.select("head title").get(0).text();
        }

        Element avatarEle = doc.select("div.mini-profile.box a.pull-left img.mini-profile__img").get(0);
        String avatarUrl = avatarEle.attr("src");

        String author;
        Elements authorEle = doc.select("div.mini-profile.box div h3.mini-profile__name a");
        author = authorEle.get(0).text();

        String dimen = doc.select(" div.icon-list__content div.icon-list__title").get(0).text();
        dimen = dimen.substring(0, dimen.lastIndexOf(" "));
        Elements sizeEles = doc.select("div.icon-list__infos span");
        String size = sizeEles.get(0).text();

        ArrayList<String> tags = new ArrayList<>();
        ArrayList<String> colors = new ArrayList<>();

        Elements tagEles = doc.select("a.btn-light");
        for (Element tagEle : tagEles) {
            if (tags.size() < 9) {
                tags.add(tagEle.text());
            } else {
                break;
            }
        }

        Elements colorElements = doc.select("a.photo-colors__color");
        for (Element colorEle : colorElements) {
            colors.add(colorEle.attr("title"));
        }

        return new ImageDetail(title, author, avatarUrl, dimen, size, colors, tags);
    }

    @SuppressWarnings("unchecked")
    public static Observable<List<Category>> getCategory() {
        return getMaxPage().flatMap(new Func1<Integer, Observable<List<Category>>>() {
            @Override
            public Observable<List<Category>> call(Integer integer) {
                List<Observable<List<Category>>> listObs = new ArrayList<>();
                for (int i = 0; i < integer; i++) {
                    String pageUrl = Constants.CATEGORY_LINK + "?page=" + (i + 1);
                    listObs.add(getListCategoryFromPage(pageUrl));
                }

                return Observable.zip(listObs, new FuncN<List<Category>>() {
                    @Override
                    public List<Category> call(Object... args) {
                        List<Category> result = new ArrayList<>();
                        Set<Category> orderList = new HashSet<>();
                        for (Object arg : args) {
                            if (arg instanceof List) {
                                orderList.addAll(((List<Category>) arg));
                            }
                        }
                        result.addAll(orderList);
                        sortCategory(result);
                        return result;
                    }
                });
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    private static void sortCategory(List<Category> list) {
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                return cat1.getName().compareTo(cat2.getName());
            }
        });
    }

    private static Observable<Integer> getMaxPage() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    subscriber.onNext(getMaxPage(Constants.CATEGORY_LINK));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static Observable<List<Category>> getListCategoryFromPage(final String url) {
        return Observable.create(new Observable.OnSubscribe<List<Category>>() {
            @Override
            public void call(Subscriber<? super List<Category>> subscriber) {
                try {
                    List<Category> categories = new ArrayList<>();
                    Timber.e("category link : " + url);
                    Document doc;
                    doc = Jsoup.connect(url).timeout(CONNECT_TIMEOUT).get();
                    Elements elements = doc.select("a.search-medium__link");

                    for (Element element : elements) {
                        String name = element.select("h4.search-medium__title").get(0).text();

                        String capName = name.substring(0, 1).toUpperCase() + name.substring(1);
                        String link = Constants.ROOT_LINK + element.attr("href");
                        String thumbLink = element.select("img").attr("src");

                        categories.add(new Category(capName, link, thumbLink));
                    }

                    subscriber.onNext(categories);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    private static int getMaxPage(String url) throws IOException {
        int maxPage = 0;

        Document doc = Jsoup.connect(url).timeout(CONNECT_TIMEOUT).get();
        Elements pages = doc.select("div.js-pagination div.pagination a");

        for (Element page : pages) {
            int pageNo;
            try {
                pageNo = Integer.parseInt(page.text());
            } catch (NumberFormatException e) {
                continue;
            }
            if (pageNo > maxPage) {
                Timber.e("getMaxPage: " + page.text());
                maxPage = Integer.parseInt(page.text());
            }
        }

        return maxPage;
    }

    private static boolean isNetworkConnecting() throws UnknownHostException {
        InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
        return !ipAddr.toString().equals("");
    }
}
