package dpanic.freestock.pexels.wallpaper.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import com.dpanic.wallz.pexels.R;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by dpanic on 11/2/2016.
 * Project: Pexels
 */

public class DownloadUtil {

    private static OkHttpClient httpClient = new OkHttpClient();

    @SuppressWarnings("unused")
    @NonNull
    public static Observable<Boolean> isNetworkConnecting() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    InetAddress ipAddr = InetAddress.getByName("google.com");
                    subscriber.onNext(!ipAddr.toString().equals(""));
                    subscriber.onCompleted();
                } catch (UnknownHostException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @NonNull
    public static Observable<Integer> downloadImageFromUrl(Context context, final String url,
                                                           boolean isForNotification) {
        final InputStream[] input = { null };
        final OutputStream[] output = { null };
        final Response[] response = new Response[1];
        final String outputPath;
        final long[] length = new long[1];
        final Call[] downloadCall = new Call[1];

        if (isForNotification) {
            if (context != null) {
                outputPath = FileUtil.getInternalPath(context, url);
            } else {
                outputPath = "";
            }
        } else {
            outputPath = FileUtil.getLocalPath(url);
        }

        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {

                try {
                    Request downloadRequest = new Request.Builder().url(url).build();
                    downloadCall[0] = httpClient.newCall(downloadRequest);
                    response[0] = downloadCall[0].execute();

                    if (response[0].isSuccessful()) {
                        input[0] = response[0].body().byteStream();
                        length[0] = response[0].body().contentLength();
                        Timber.e("call: length = " + length[0]);

                        output[0] = new FileOutputStream(outputPath);

                        byte data[] = new byte[1024];
                        subscriber.onNext(0);

                        long total = 0;
                        int count;

                        while ((count = input[0].read(data)) != -1) {
                            total += count;

                            subscriber.onNext((int) (total * 100 / length[0]));

                            Timber.e("progress:  = " + (total * 100 / length[0]));
                            output[0].write(data, 0, count);
                        }

                        output[0].flush();
                        output[0].close();
                        input[0].close();
                        response[0].close();
                    }

                } catch (IOException e) {
                    subscriber.onError(e);
                } finally {
                    if (input[0] != null) {
                        try {
                            input[0].close();
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }
                    }

                    if (output[0] != null) {
                        try {
                            output[0].close();
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }
                    }
                    if (response[0] != null) {
                        response[0].close();
                    }
                }
                subscriber.onCompleted();
            }
        }).sample(100, TimeUnit.MILLISECONDS).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                Timber.e("doOnUnsubscribe");
                File file = new File(outputPath);
                if (file.exists()) {
                    long currentLength = file.length();
                    Timber.e("currentLength = " + currentLength);
                    if (currentLength != length[0]) {
                        if (downloadCall[0] != null) {
                            downloadCall[0].cancel();
                        }

                        if (file.delete()) {
                            Timber.e("file  " + outputPath + " deleted!");
                        }
                    }
                }

                try {
                    if (output[0] != null) {
                        Timber.e("doOnUnsubscribe: output flush");
                        output[0].flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Timber.e("doOnUnsubscribe: output flush exception");
                }
                try {
                    if (output[0] != null) {
                        Timber.e("doOnUnsubscribe: output close");
                        output[0].close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Timber.e("doOnUnsubscribe: output close exception");
                }
                try {
                    Timber.e("doOnUnsubscribe: input close");
                    if (input[0] != null) {
                        input[0].close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Timber.e("doOnUnsubscribe: input exception");
                }
                if (response[0] != null) {
                    Timber.e("doOnUnsubscribe: response close");
                    response[0].close();
                }
            }
        });
    }

    public static boolean checkNetworkStatus(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null;
    }

    public static long enqueueDownload(Context context, String url, boolean isForNotification) {
        long downloadReference;
        Uri uri = Uri.parse(url);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle(context.getString(R.string.app_name));
        request.setDescription(context.getString(R.string.string_downloading_image));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        if (isForNotification) {
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS,
                                                     FileUtil.getFileName(url));
        } else {
            request.setDestinationInExternalPublicDir(context.getString(R.string.app_name), FileUtil.getFileName(url));
        }
        request.allowScanningByMediaScanner();

        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    public static Observable<Integer> getDownloadProgressFromId(final Context context, final long id) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    boolean downloading = true;
                    int recent_byte = 0;
                    long recent_time = System.currentTimeMillis();
                    while (downloading) {

                        DownloadManager.Query q = new DownloadManager.Query();
                        DownloadManager downloadManager =
                                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        q.setFilterById(id);

                        Cursor cursor = downloadManager.query(q);
                        cursor.moveToFirst();

                        int bytes_downloaded =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));


                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) ==
                                DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;
                        } else if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) ==
                                DownloadManager.STATUS_FAILED) {
                            downloadManager.remove(id);
                            subscriber.onError(new SocketTimeoutException());

                        }
                        cursor.close();

                        subscriber.onNext((int) ((bytes_downloaded * 100L) / bytes_total));

                        // keep track of downloaded bytes and time to cancel download
                        if (recent_byte != bytes_downloaded) {
                            recent_byte = bytes_downloaded;
                            recent_time = System.currentTimeMillis();
                        } else {
                            if (System.currentTimeMillis() - recent_time > 10000) {
                                downloadManager.remove(id);
                                subscriber.onError(new SocketTimeoutException());
                            }
                        }

                        if (!downloading) {
                            subscriber.onCompleted();
                        }
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }

        }).sample(500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static void dequeueDownload(Context context, long id) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.remove(id);
    }
}
