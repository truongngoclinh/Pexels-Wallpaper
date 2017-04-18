package com.dpanic.wallz.pexels.ui.main;

import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.dpanic.wallz.pexels.BuildConfig;
import com.dpanic.wallz.pexels.R;
import com.dpanic.wallz.pexels.busevent.OpenCategoryEvent;
import com.dpanic.wallz.pexels.busevent.OpenImageEvent;
import com.dpanic.wallz.pexels.data.model.Category;
import com.dpanic.wallz.pexels.data.model.Image;
import com.dpanic.wallz.pexels.injection.HasComponent;
import com.dpanic.wallz.pexels.injection.component.MainComponent;
import com.dpanic.wallz.pexels.ui.base.BaseActivity;
import com.dpanic.wallz.pexels.ui.detail.DetailActivity;
import com.dpanic.wallz.pexels.ui.explore.ExploreFragment;
import com.dpanic.wallz.pexels.ui.favorite.FavoriteFragment;
import com.dpanic.wallz.pexels.ui.search.SearchActivity;
import com.dpanic.wallz.pexels.utils.Constants;
import com.dpanic.wallz.pexels.utils.TextUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends BaseActivity
        implements HasComponent<MainComponent>, NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener {

    private static final String FRAGMENT_EXPLORE = "fragment_explore";
    private static final String FRAGMENT_FAVORITE = "fragment_favorite";
    private static final String FRAGMENT_HISTORY = "fragment_history";

    @BindView(R.id.main_tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ExploreFragment mExploreFragment;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private FavoriteFragment mFavoriteFragment;
    private FavoriteFragment mHistoryFragment;

    @Inject
    FirebaseAnalytics mFirebaseAnalytics;
    @Inject
    FirebaseMessaging mFirebaseMessaging;
    @Inject
    EventBus eventBus;

    private int count = 0;
    private MainComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (workaroundForMarketIssue()) {
            return;
        }

        setupActionBar();

        setupNavigationView();

        mFragmentManager = getSupportFragmentManager();

        initSharedPreferenceValues();

        subscribeTopic();

        launchExploreFragment();
    }

    @Override
    protected void injectDependencies() {
        component = MainComponent.Initializer.init(getAppComponent(), this);
        component.inject(this);
    }

    private boolean workaroundForMarketIssue() {
        // Possible work around for market launches. See http://code.google.com/p/android/issues/detail?id=2373
        // for more details. Essentially, the market launches the main activity on top of other activities.
        // we never want this to happen. Instead, we check if we are the root and if not, we finish.
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Timber.w("thanh.dao", "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent();
        super.onNewIntent(intent);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Image image = bundle.getParcelable(Constants.IMAGE_INSTANCE);
            if (image != null) {
                launchDetailActivity(image);
            }
        }
    }

    private void subscribeTopic() {
        if (BuildConfig.DEBUG) {
            mFirebaseMessaging.subscribeToTopic(getString(R.string.app_name_debug));
        } else {
            mFirebaseMessaging.subscribeToTopic(getResources().getString(R.string.app_name));
        }
    }

    private void setupNavigationView() {
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                                          R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initSharedPreferenceValues() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.IS_SHOW_ADS, true);
        editor.apply();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        eventBus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (count == 1) {
                count = 0;
                super.onBackPressed();
            } else {
                Toast.makeText(this, "Press Back again to exit app", Toast.LENGTH_SHORT).show();
                count++;
                new AppRate(this)
                        .setShowIfAppHasCrashed(false)
                        .setMinDaysUntilPrompt(2)
                        .setMinLaunchesUntilPrompt(5)
                        .init();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        initSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.main_menu_share:
            shareApp();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        Intent i = new Intent(Intent.ACTION_SEND);
        String playStoreLink = "https://play.google.com/store/apps/details?id=" +
                getPackageName();
        String shareText = "I found wonderful photo collections from this app  " + playStoreLink;

        // Log Event
        logAnalytic(FirebaseAnalytics.Event.SHARE, null, null, null);

        i.putExtra(Intent.EXTRA_TEXT, shareText);
        i.setType("text/plain");
        startActivity(Intent.createChooser(i, getResources().getText(R.string.share_app)));
    }


    public void logAnalytic(String action, String itemId, String itemName, String contentType) {
        Bundle bundle = new Bundle();
        if (itemId != null) {
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
        }
        if (itemName != null) {
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
        }
        if (contentType != null) {
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        }
        mFirebaseAnalytics.logEvent(action, bundle);
    }


    private void initSearchView(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.main_menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
        case R.id.nav_explore:
            launchExploreFragment();
            break;
        case R.id.nav_favorites:
            launchFavoriteFragment();
            break;
        case R.id.nav_history:
            launchHistoryFragment();
            break;
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void launchDetailActivity(Image img) {
        Intent intent = new Intent(this, DetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.IMAGE_INSTANCE, img);

        intent.putExtras(bundle);

        startActivity(intent);
        overridePendingTransition(R.anim.above_activity_enter, R.anim.below_activity_exit);
    }

    public void launchExploreFragment() {
        mTabLayout.setVisibility(View.VISIBLE);
        setTitle(getResources().getString(R.string.app_name));
        if (mExploreFragment == null) {
            mExploreFragment = new ExploreFragment();
        }

        FragmentTransaction ft = mFragmentManager.beginTransaction();

        if (mExploreFragment.isAdded()) {
            ft.show(mExploreFragment);
        } else {
            ft.add(R.id.fragment_container, mExploreFragment, FRAGMENT_EXPLORE);
        }

        if (mFavoriteFragment != null && mFavoriteFragment.isAdded()) {
            ft.hide(mFavoriteFragment);
        }
        if (mHistoryFragment != null && mHistoryFragment.isAdded()) {
            ft.hide(mHistoryFragment);
        }
        ft.commit();
    }

    public void launchFavoriteFragment() {
        mTabLayout.setVisibility(View.GONE);
        setTitle(getResources().getString(R.string.string_favorites));
        if (mFavoriteFragment == null) {
            mFavoriteFragment = FavoriteFragment.getInstance(true);
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();

        if (mFavoriteFragment.isAdded()) {
            ft.show(mFavoriteFragment);
        } else {
            ft.add(R.id.fragment_container, mFavoriteFragment, FRAGMENT_FAVORITE);
        }

        if (mExploreFragment != null && mExploreFragment.isAdded()) {
            ft.hide(mExploreFragment);
        }
        if (mHistoryFragment != null && mHistoryFragment.isAdded()) {
            ft.hide(mHistoryFragment);
        }
        ft.commit();
    }

    public void launchHistoryFragment() {
        mTabLayout.setVisibility(View.GONE);
        setTitle(getResources().getString(R.string.string_history));
        if (mHistoryFragment == null) {
            mHistoryFragment = FavoriteFragment.getInstance(false);
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();

        if (mHistoryFragment.isAdded()) {
            ft.show(mHistoryFragment);
        } else {
            ft.add(R.id.fragment_container, mHistoryFragment, FRAGMENT_HISTORY);
        }

        if (mExploreFragment != null && mExploreFragment.isAdded()) {
            ft.hide(mExploreFragment);
        }
        if (mFavoriteFragment != null && mFavoriteFragment.isAdded()) {
            ft.hide(mFavoriteFragment);
        }
        ft.commit();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessage(OpenImageEvent event) {
        launchDetailActivity(event.getImage());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessage(OpenCategoryEvent event) {
        launchSearchActivity(event.getCategory(), event.isColor());
    }

    private void launchSearchActivity(Category category, boolean isColor) {
        Intent intent = new Intent(this, SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.CATEGORY_INSTANCE, category);
        bundle.putBoolean(Constants.IS_COLOR_SEARCH, isColor);
        intent.putExtras(bundle);

        startActivity(intent);
        overridePendingTransition(R.anim.above_activity_enter, R.anim.below_activity_exit);
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Category category = new Category(query, TextUtil.getSearchLink(query), "");

        launchSearchActivity(category, false);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public MainComponent getComponent() {
        return component;
    }

    //    @Override
//    protected void injectDependencies(DPWallz application, AppComponent component) {
//        component.inject(this);
//    }
}
