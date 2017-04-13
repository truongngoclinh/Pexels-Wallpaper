package com.dpanic.dpwallz.ui.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.dpanic.dpwallz.R;
import com.dpanic.dpwallz.application.DPWallz;
import com.dpanic.dpwallz.injection.component.DaggerMainComponent;
import com.dpanic.dpwallz.injection.component.MainComponent;
import com.dpanic.dpwallz.injection.module.ActivityModule;
import com.dpanic.dpwallz.data.StorIODBManager;
import com.dpanic.dpwallz.data.model.Category;
import com.dpanic.dpwallz.ui.base.BaseFragment;
import com.dpanic.dpwallz.utils.HTMLParsingUtil;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by dpanic on 29/09/2016.
 * Project: DPWallz
 */

public class CategoryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.category_container)
    SwipeRefreshLayout categoryContainer;

    @BindView(R.id.rv_category)
    RecyclerView rvCategory;

    @BindView(R.id.category_error_container)
    LinearLayout layoutError;

    ArrayList<Category> categoryList;
    @Inject
    CategoryAdapter categoryAdapter;
    //    @Inject
    //    DataManager mDataManager;
    @Inject
    StorIODBManager mDataManager;
    private Subscription subscription;
    private int orientation = Configuration.ORIENTATION_PORTRAIT;
    private GridLayoutManager layoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void injectDependencies() {
        getComponent(MainComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_category_layout, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initFragment();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.setSpanCount(2);
        } else {
            layoutManager.setSpanCount(3);
        }
    }

    private void initFragment() {
        categoryList = new ArrayList<>();

        categoryAdapter.setData(categoryList);
        orientation = getActivity().getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            layoutManager = new GridLayoutManager(getActivity(), 3);
        }

        rvCategory.setAdapter(categoryAdapter);
        rvCategory.setLayoutManager(layoutManager);

        categoryContainer.setOnRefreshListener(this);
        categoryContainer.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        categoryContainer.setRefreshing(true);

        loadCategoryDataFromDB();
    }

    private void loadCategoryDataFromDB() {
        subscription = mDataManager.getCategoryList().subscribe(new Action1<List<Category>>() {
            @Override
            public void call(List<Category> categories) {
                if (subscription != null) {
                    subscription.unsubscribe();
                }
                if (categories.size() > 0) {
                    categoryList.clear();
                    categoryList.addAll(categories);
                    sortCategory(categoryList);
                    categoryAdapter.notifyDataSetChanged();
                    categoryContainer.setRefreshing(false);
                } else {
                    loadCategoryFromWebsite(false);
                }
            }
        });
        addSubscription(subscription);
    }

    private void loadCategoryFromWebsite(final boolean isRefreshing) {

        addSubscription(HTMLParsingUtil.getCategory().subscribe(new Observer<List<Category>>() {
            @Override
            public void onCompleted() {
                categoryContainer.setRefreshing(false);
            }

            @Override
            public void onError(Throwable throwable) {
                Timber.e("onError: " + throwable.getMessage());
                throwable.printStackTrace();
                categoryContainer.setRefreshing(false);
                if (categoryList.size() == 0) {
                    layoutError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNext(final List<Category> list) {
                if (list.size() > 0) {

                    if (isRefreshing) {
                        addSubscription(mDataManager.deleteCategoryData().subscribe(new Observer<DeleteResult>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable throwable) {
                                throwable.printStackTrace();
                            }

                            @Override
                            public void onNext(DeleteResult integer) {
                                addCategoriesToDB(list);
                            }
                        }));
                    } else {
                        addCategoriesToDB(list);
                    }

                    categoryList.clear();

                    categoryList.addAll(list);

                    categoryAdapter.notifyDataSetChanged();
                }
            }
        }));
    }

    private static void sortCategory(List<Category> list) {
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                return cat1.getName().compareTo(cat2.getName());
            }
        });
    }

    private void addCategoriesToDB(List<Category> list) {
        mDataManager.addCategories(list).subscribe(new Action1<PutResults<Category>>() {
            @Override
            public void call(PutResults<Category> categoryPutResults) {
                // TODO log here
            }
        });
    }

    @Override
    public void onRefresh() {
        layoutError.setVisibility(View.GONE);
        loadCategoryFromWebsite(true);
    }

    @OnClick(R.id.category_btn_retry)
    void onClick() {
        categoryContainer.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDataManager = null;
    }
}
