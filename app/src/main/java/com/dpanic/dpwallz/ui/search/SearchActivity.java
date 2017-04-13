package com.dpanic.dpwallz.ui.search;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.dpanic.dpwallz.R;
import com.dpanic.dpwallz.busevent.NoResultFoundEvent;
import com.dpanic.dpwallz.busevent.OpenImageEvent;
import com.dpanic.dpwallz.data.model.Category;
import com.dpanic.dpwallz.data.model.Image;
import com.dpanic.dpwallz.ui.base.BaseActivity;
import com.dpanic.dpwallz.ui.detail.DetailActivity;
import com.dpanic.dpwallz.ui.imagelist.ImageListFragment;
import com.dpanic.dpwallz.utils.Constants;
import com.dpanic.dpwallz.utils.TextUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity {

    private static final String FRAGMENT_CATEGORY_IMAGE = "frag_category_image";
    private FragmentManager mFragmentManager;
    private Category category;

    @BindView(R.id.search_toolbar)
    Toolbar toolbar;

    @BindView(R.id.search_color_indicator)
    ImageView colorIndicator;

    @BindView(R.id.search_no_result_layout)
    LinearLayout noResultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void injectDependencies() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessage(OpenImageEvent event) {
        launchDetailActivity(event.getImage());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessage(NoResultFoundEvent event) {
        noResultLayout.setVisibility(View.VISIBLE);
    }

    public void launchDetailActivity(Image img) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Constants.IMAGE_INSTANCE, img);

        startActivity(intent);
        overridePendingTransition(R.anim.above_activity_enter, R.anim.below_activity_exit);
    }

    private void initView() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        category = bundle.getParcelable(Constants.CATEGORY_INSTANCE);
        boolean isColorSearch = bundle.getBoolean(Constants.IS_COLOR_SEARCH, false);

        if (isColorSearch) {
            colorIndicator.setColorFilter(Color.parseColor(TextUtil.getColorFromSearchText(category.getName())),
                                          PorterDuff.Mode.SRC_ATOP);
        } else {
            colorIndicator.setVisibility(View.GONE);
        }

        mFragmentManager = getSupportFragmentManager();

        setTitle(category.getName());

        launchImageFragment();
    }

    public void launchImageFragment() {
        if (mFragmentManager.findFragmentByTag(FRAGMENT_CATEGORY_IMAGE) == null) {
            ImageListFragment mImageFragment =
                    ImageListFragment.newInstance(Constants.FRAG_TYPE_SEARCH, category.getLink());
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.search_image_container, mImageFragment, FRAGMENT_CATEGORY_IMAGE).commit();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.below_activity_enter, R.anim.above_activity_exit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
