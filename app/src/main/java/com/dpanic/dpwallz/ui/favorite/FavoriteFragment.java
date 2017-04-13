package com.dpanic.dpwallz.ui.favorite;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import com.dpanic.dpwallz.R;
import com.dpanic.dpwallz.application.DPWallz;
import com.dpanic.dpwallz.injection.component.DaggerMainComponent;
import com.dpanic.dpwallz.injection.component.MainComponent;
import com.dpanic.dpwallz.injection.module.ActivityModule;
import com.dpanic.dpwallz.data.StorIODBManager;
import com.dpanic.dpwallz.data.model.Image;
import com.dpanic.dpwallz.ui.base.BaseFragment;
import com.dpanic.dpwallz.ui.common.ImageAdapter;
import com.dpanic.dpwallz.utils.Constants;
import com.dpanic.dpwallz.utils.FileUtil;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by dpanic on 10/14/2016.
 * Project: DPWallz
 */

public class FavoriteFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<Image> favList;
    private ArrayList<Image> displayList;

    @BindView(R.id.image_list_recycler_view)
    RecyclerView rvFavorite;

    @BindView(R.id.image_list_container)
    SwipeRefreshLayout refreshLayout;

    //    @BindView(R.id.no_download_layout)
    //    LinearLayout noDownloadLayout;
    //
    //    @BindView(R.id.no_fav_layout)
    //    LinearLayout noFavLayout;

    @BindView(R.id.no_download_view_stub)
    ViewStub noDownloadViewStub;

    @BindView(R.id.no_fav_view_stub)
    ViewStub noFavViewStub;

    View noDownloadView;
    View noFavView;

    @Inject
    ImageAdapter iaFavorite;
    private int totalItemCount;
    private int firstVisibleItem;
    private int visibleItemCount;
    private boolean isLoading = false;
    private int visibleThreshold = 5;
    private int setCount = 0;

    private static final int batchSize = 1000;
    private boolean isFavorite;
    @Inject
    StorIODBManager mDataManager;
    //    DataManager mDataManager;
    private int orientation = Configuration.ORIENTATION_PORTRAIT;
    private GridLayoutManager layoutManager;

    public static FavoriteFragment getInstance(boolean isFavorite) {
        FavoriteFragment favoriteFragment = new FavoriteFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.IS_FAVORITE_FRAGMENT, isFavorite);

        favoriteFragment.setArguments(bundle);

        return favoriteFragment;
    }

    @Override
    protected void injectDependencies() {
        getComponent(MainComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_image_list_layout, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        isFavorite = bundle.getBoolean(Constants.IS_FAVORITE_FRAGMENT);

        Timber.w("mDataManager = " + mDataManager);

        if (noDownloadView == null) {
            noDownloadView = noDownloadViewStub.inflate();
        }

        if (noFavView == null) {
            noFavView = noFavViewStub.inflate();
        }

        favList = new ArrayList<>();
        displayList = new ArrayList<>();
//        iaFavorite = new ImageAdapter(getActivity(), displayList);
        iaFavorite.setData(displayList);
        orientation = getActivity().getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            layoutManager = new GridLayoutManager(getActivity(), 4);
        }
        rvFavorite.setAdapter(iaFavorite);
        rvFavorite.setLayoutManager(layoutManager);
        Timber.e("imageListAdapter iaFavorite = " + iaFavorite);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        refreshLayout.setRefreshing(true);

        rvFavorite.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    totalItemCount = layoutManager.getItemCount();
                    //                    pastVisibleItems = layoutManager.findLastVisibleItemPosition();
                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    visibleItemCount = layoutManager.getChildCount();

                    if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        isLoading = true;
                        onLoadMore();
                    }
                }
            }
        });


        initData();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.setSpanCount(2);
        } else {
            layoutManager.setSpanCount(4);
        }
    }

    private void onLoadMore() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                favList.add(null);
                iaFavorite.notifyItemInserted(favList.size() - 1);
            }
        };
        handler.post(runnable);

        addDataBatch(setCount);
    }

    private void addDataBatch(int setCount) {
        int nextSetCount = setCount + 1;
        int startIndex = setCount * batchSize;
        int endIndex = nextSetCount * batchSize;
        int favListSize = favList.size();

        for (int i = startIndex; i < endIndex && i < favListSize; i++) {
            displayList.add(favList.get(i));
        }

        rvFavorite.post(new Runnable() {
            @Override
            public void run() {
                iaFavorite.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
        if (isFavorite) {
            addSubscription(mDataManager.getFavoriteImageList().subscribeOn(Schedulers.io())
                                              .observeOn(AndroidSchedulers.mainThread())
                                              .subscribe(new Action1<List<Image>>() {
                                                  @Override
                                                  public void call(List<Image> images) {
                                                      if (noFavView != null) {
                                                          if (images.size() == 0) {
                                                              noFavView.setVisibility(View.VISIBLE);
                                                          } else {
                                                              noFavView.setVisibility(View.GONE);
                                                          }
                                                      }
                                                      Timber.tag("thanh.dao").d("Fav list size = " + images.size());

                                                      processInitData(images);
                                                  }
                                              }));
        } else {
            addSubscription(mDataManager.getDownloadedImageList().subscribeOn(Schedulers.io())
                                              .observeOn(AndroidSchedulers.mainThread())
                                              .subscribe(new Action1<List<Image>>() {
                                                  @Override
                                                  public void call(List<Image> images) {
                                                      if (noDownloadView != null) {
                                                          if (images.size() == 0) {
                                                              noDownloadView.setVisibility(View.VISIBLE);
                                                          } else {
                                                              noDownloadView.setVisibility(View.GONE);
                                                          }
                                                      }

                                                      processInitData(images);
                                                  }
                                              }));
        }
    }

    private void processInitData(List<Image> images) {
        setCount = 0;
        favList.clear();
        displayList.clear();
        refreshLayout.setRefreshing(false);

        if (!isFavorite) {
            for (Image image : images) {
                if (FileUtil.isFileExists(image.getLocalLink())) {
                    favList.add(image);
                } else {
                    addSubscription(mDataManager.deleteImage(image).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<DeleteResult>() {
                                @Override
                                public void call(DeleteResult deleteResult) {
                                    Timber.e("call: delete " + deleteResult.numberOfRowsDeleted() + " image(s)");
                                }
                            }));
                }
            }
        } else {
            favList.addAll(images);
        }

        addDataBatch(setCount);
        setCount++;
    }

    @Override
    public void onRefresh() {
        setCount = 0;
        favList.clear();
        displayList.clear();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    protected void injectDependencies(DPWallz application, AppComponent component) {
//        component.inject(this);
//    }
}
