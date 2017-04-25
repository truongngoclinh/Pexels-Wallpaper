package dpanic.freestock.pexels.wallpaper.ui.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dpanic.wallz.pexels.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.wang.avi.AVLoadingIndicatorView;
import com.xiaofeng.flowlayoutmanager.Alignment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import dpanic.freestock.pexels.wallpaper.application.App;
import dpanic.freestock.pexels.wallpaper.busevent.DownloadEvent;
import dpanic.freestock.pexels.wallpaper.busevent.OpenCategoryEvent;
import dpanic.freestock.pexels.wallpaper.busevent.ProgressDialogEvent;
import dpanic.freestock.pexels.wallpaper.data.StorIODBManager;
import dpanic.freestock.pexels.wallpaper.data.model.Category;
import dpanic.freestock.pexels.wallpaper.data.model.Image;
import dpanic.freestock.pexels.wallpaper.data.model.ImageDetail;
import dpanic.freestock.pexels.wallpaper.injection.HasComponent;
import dpanic.freestock.pexels.wallpaper.injection.component.DetailComponent;
import dpanic.freestock.pexels.wallpaper.ui.base.BaseActivity;
import dpanic.freestock.pexels.wallpaper.ui.common.CustomProgressDialog;
import dpanic.freestock.pexels.wallpaper.ui.common.ImageActionHelper;
import dpanic.freestock.pexels.wallpaper.ui.main.MainActivity;
import dpanic.freestock.pexels.wallpaper.ui.preview.PreviewActivity;
import dpanic.freestock.pexels.wallpaper.ui.search.SearchActivity;
import dpanic.freestock.pexels.wallpaper.utils.Constants;
import dpanic.freestock.pexels.wallpaper.utils.DownloadUtil;
import dpanic.freestock.pexels.wallpaper.utils.FileUtil;
import dpanic.freestock.pexels.wallpaper.utils.HTMLParsingUtil;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;
import rx.Observer;
import rx.functions.Action1;
import timber.log.Timber;

public class DetailActivity extends BaseActivity implements HasComponent<DetailComponent>, DialogInterface
        .OnCancelListener{

    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;

    @BindView(R.id.clp_backdrop_loading)
    AVLoadingIndicatorView clpBackdropLoading;

    @BindView(R.id.detail_backdrop_image)
    ImageView backDropImage;

    @BindView(R.id.detail_author)
    TextView tvAuthor;

    @BindView(R.id.detail_avatar)
    CircleImageView ivAvatar;

    @BindView(R.id.tv_dimen)
    TextView tvDimen;

    @BindView(R.id.tv_file_size)
    TextView tvSize;

    @BindView(R.id.rv_colors)
    RecyclerView rvColors;

    @BindView(R.id.rv_tags)
    RecyclerView rvTags;

    @BindView(R.id.fav_on_btn_container)
    LinearLayout btnFavOn;

    @BindView(R.id.fav_off_btn_container)
    LinearLayout btnFavOff;

    @BindView(R.id.ctn_outer_ad)
    LinearLayout ctnAdOuter;

//    @BindView(R.id.detail_dimen_vertical_divider)
//    ImageView verticalDivider;

//    @BindView(R.id.detail_dimen_container)
//    RelativeLayout detailDimenContainer;

    @BindView(R.id.detail_container)
    LinearLayout mContentLayout;

    @BindView(R.id.clp_detail_content)
    AVLoadingIndicatorView clpContentLoading;

    @BindView(R.id.detail_error_container)
    LinearLayout layoutError;

    @BindView(R.id.detail_ad_container)
    RelativeLayout layoutAd;

    private Image image;
    private Context mContext;
    private DetailComponent component;

    private ArrayList<String> colors;
    private ArrayList<String> tags;
    private boolean performingAction = false;
    private boolean isFavorite = false;
    private boolean isShowAds;

    private int loadedProgress = 0;
    private int currentProgress = 0;

    @Inject
    ColorAdapter mColorAdapter;
    @Inject
    TagAdapter mTagAdapter;
    @Inject
    ImageActionHelper actionHelper;
    @Inject
    CustomProgressDialog progressDialog;
    @Inject
    StorIODBManager mDataManager;
    @Inject
    NativeExpressAdView adView;
    @Inject
    AdRequest mNativeAdRequest;
    @Inject
    EventBus eventBus;

    private static class WeakPrefHandler extends Handler {
        private final WeakReference<DetailActivity> mActivity;

        WeakPrefHandler(DetailActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DetailActivity activity = mActivity.get();
            if (activity != null) {
                activity.updateProgress(msg);
            }
        }
    }

    private WeakPrefHandler progressHandler = new WeakPrefHandler(this);

    private void updateProgress(Message msg) {
        if (progressDialog != null && progressDialog.isShowing()) {
            if (msg.what == Constants.PROGRESS_UPDATE) {
                if (currentProgress < loadedProgress) {
                    currentProgress++;
                    progressDialog.setProgress(currentProgress);

                    if (loadedProgress != 100) {
                        progressHandler.sendEmptyMessageDelayed(Constants.PROGRESS_UPDATE, Constants.PROGRESS_NORMAL_UPDATE_INTERVAL);
                    } else {
                        progressHandler.sendEmptyMessageDelayed(Constants.PROGRESS_UPDATE, Constants.PROGRESS_FAST_UPDATE_INTERVAL);
                    }
                } else {
                    if (loadedProgress != 100) {
                        progressHandler.sendEmptyMessageDelayed(Constants.PROGRESS_UPDATE, Constants.PROGRESS_NORMAL_UPDATE_INTERVAL);
                    } else {
                        if (currentProgress == 100) {
                            progressHandler.sendEmptyMessageDelayed(Constants.PROGRESS_FINISH, Constants
                                    .PROGRESS_FINISH_DELAY);
                        }
                    }
                }
            } else {
                progressDialog.dismiss();
                actionHelper.onDownloadCompleted();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        mContext = this;

        setSupportActionBar(toolbar);
        setTitle("");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isShowAds = preferences.getBoolean(Constants.IS_SHOW_ADS, false);

//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();

        init();
    }

    private void init() {
        if (Branch.isAutoDeepLinkLaunch(this)) {

            Timber.e("deeep linkkkkkkkks");
            try {
                JSONObject object = Branch.getInstance().getLatestReferringParams();
                String pexels_id = object.getString("pexels_id");
                String name = object.getString("name");
                String localLink = object.getString("localLink");
                String largeLink = object.getString("largeLink");
                String originalLink = object.getString("orgLink");
                String detailLink = object.getString("detailLink");

                image = new Image(pexels_id, name, originalLink, largeLink, detailLink, "", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Timber.e("elseeeeeeeee");
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            image = bundle.getParcelable(Constants.IMAGE_INSTANCE);
        }

        loadBackdropImage(image);
        backDropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previewIntent = new Intent(mContext, PreviewActivity.class);

                Bundle previewBundle = new Bundle();
                previewBundle.putParcelable(Constants.IMAGE_INSTANCE, image);
                previewIntent.putExtras(previewBundle);

                unregisterEventBus();

                startActivity(previewIntent);
            }

        });

        actionHelper.setImage(image);
        initView();
        loadData();
    }

    private void unregisterEventBus() {
        if (eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        performingAction = false;

        registerEventBus();
    }

    private void registerEventBus() {
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterEventBus();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessage(ProgressDialogEvent event) {
        Timber.e("ProgressDialogEvent");
        switch (event.getEventType()) {
            case ProgressDialogEvent.SHOW_EVENT:
                Timber.w("SHOW_EVENT");
                if (progressDialog != null) {
                    progressDialog.setOnCancelListener(this);
                    progressDialog.show();
                }
                progressHandler.sendEmptyMessageDelayed(Constants.PROGRESS_UPDATE, Constants.PROGRESS_NORMAL_UPDATE_INTERVAL);
                break;
            case ProgressDialogEvent.UPDATE_EVENT:
                Timber.w("UPDATE_EVENT " + event.getProgress());
//                progressDialog.setProgress(event.getProgress());
                loadedProgress = event.getProgress();
                break;
//            case ProgressDialogEvent.DISMISS_EVENT:
//                Timber.w("DISMISS_EVENT");
//                progressDialog.dismiss();
//                break;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessage(OpenCategoryEvent event) {
        launchSearchActivity(event.getCategory(), event.isColor());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessage(DownloadEvent event) {
        if (event.getStatus() == DownloadEvent.STATUS_COMPLETE) {
            image.setLocalLink(FileUtil.getLocalPath(image.getOriginalLink()));
            mDataManager.addImage(image).subscribe();
        } else if (event.getStatus() == DownloadEvent.STATUS_ERROR) {
            Throwable throwable = event.getException();
            if (throwable != null) {
                if (throwable instanceof SocketTimeoutException || throwable instanceof UnknownHostException) {
                    Toast.makeText(this, getResources().getString(R.string.string_no_internet_connection),
                                   Toast.LENGTH_SHORT).show();
                } else {
                    throwable.printStackTrace();
                }
            }
        }

        performingAction = false;
    }

    private void launchSearchActivity(Category category, boolean isColor) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(Constants.CATEGORY_INSTANCE, category);
        intent.putExtra(Constants.IS_COLOR_SEARCH, isColor);

        startActivity(intent);
        overridePendingTransition(R.anim.above_activity_enter, R.anim.below_activity_exit);
    }

    private void initView() {
        colors = new ArrayList<>();
        tags = new ArrayList<>();

        mColorAdapter.setData(colors);
        FixFlowLayoutManager mColorLayoutManager = new FixFlowLayoutManager();
        mColorLayoutManager.setAutoMeasureEnabled(true);
        mColorLayoutManager.setAlignment(Alignment.LEFT);
        rvColors.setAdapter(mColorAdapter);
        rvColors.setLayoutManager(mColorLayoutManager);
        rvColors.setHasFixedSize(true);                 // fix scroll problem of rv in nestedscrollview
        rvColors.setNestedScrollingEnabled(false);      // fix scroll problem of rv in nestedscrollview

        mTagAdapter.setData(tags);
        FixFlowLayoutManager mTagLayoutManager = new FixFlowLayoutManager();
        mTagLayoutManager.setAutoMeasureEnabled(true);
        mTagLayoutManager.setAlignment(Alignment.LEFT);
        rvTags.setAdapter(mTagAdapter);
        rvTags.setLayoutManager(mTagLayoutManager);
        rvTags.setHasFixedSize(true);                   // fix scroll problem of rv in nestedscrollview
        rvTags.setNestedScrollingEnabled(false);        // fix scroll problem of rv in nestedscrollview

        boolean isNetworkAvailable = DownloadUtil.checkNetworkStatus(mContext.getApplicationContext());

        adView.setAdUnitId(getString(R.string.string_detail_native_ad_id));
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (ctnAdOuter.getVisibility() == View.GONE) {
                    expand(ctnAdOuter);
                }
            }
        });
        adView.loadAd(mNativeAdRequest);

        if (isNetworkAvailable && isShowAds) {
//            layoutAd.setVisibility(View.VISIBLE);
            layoutAd.addView(adView);
        }
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(400);
        v.startAnimation(a);
    }


    @SuppressWarnings("unused")
    @OnClick({ R.id.fav_off_btn_container, R.id.fav_on_btn_container })
    void favClick(View view) {
        switch (view.getId()) {
        case R.id.fav_off_btn_container:
            image.setFavorite(true);
            btnFavOn.setVisibility(View.VISIBLE);
            btnFavOff.setVisibility(View.GONE);
            break;
        case R.id.fav_on_btn_container:
            image.setFavorite(false);
            btnFavOn.setVisibility(View.GONE);
            btnFavOff.setVisibility(View.VISIBLE);
            break;
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_download)
    void downloadAction() {
        if (!performingAction) {
            performingAction = true;
            actionHelper.downloadAction();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_share)
    void shareAction() {
//        if (!performingAction) {
//            performingAction = true;
//            actionHelper.shareAction();
//        }
        final MaterialDialog dialog = new MaterialDialog
                .Builder(this)
                .customView(R.layout.share_option_layout, false)
                .canceledOnTouchOutside(true)
                .build();
        View dialogView = dialog.getCustomView();
        if (dialogView != null) {
            LinearLayout ctnShareImage = (LinearLayout) dialogView.findViewById(R.id.ctn_image_share);
            LinearLayout ctnAppLink = (LinearLayout) dialogView.findViewById(R.id.ctn_app_link_share);

            ctnShareImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    if (!performingAction) {
                        performingAction = true;
                        actionHelper.shareAction();
                    }
                }
            });

            ctnAppLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    generateShortUrl();
                }
            });
        }

        Window window = dialog.getWindow();
        if (window != null) {
            window.getAttributes().windowAnimations = R.style.ShareDialogAnimation;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }
        dialog.show();
    }

    private void generateShortUrl() {
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                // The identifier is what Branch will use to de-dupe the content across many different Universal Objects
                .setCanonicalIdentifier("pexels_id/" + image.getPexelId())
                // This is where you define the open graph structure and how the object will appear on Facebook or in a deepview
                .setTitle("Share photo").setContentDescription("Check out this amazing photo")
                .setContentImageUrl(image.getLargeLink())
                // You use this to specify whether this content can be discovered publicly - default is public
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                // Here is where you can add custom keys/values to the deep link data
                .addContentMetadata("pexels_id", image.getPexelId())
                .addContentMetadata("name", image.getName())
                .addContentMetadata("detailLink", image.getDetailLink())
                .addContentMetadata("largeLink", image.getLargeLink())
                .addContentMetadata("localLink", image.getLocalLink())
                .addContentMetadata("orgLink", image.getOriginalLink());

        String storeLink = "https://play.google.com/store/apps/details?id=" + getPackageName();
        LinkProperties linkProperties = new LinkProperties()
                .addTag("image_detail")
                .setChannel("facebook")
                .setFeature("sharing")
                .setStage("1")
                .addControlParameter("$desktop_url", storeLink)
                .addControlParameter("$android_deeplink_path", "detail/image/");

//        branchUniversalObject.generateShortUrl(DetailActivity.this, linkProperties, new Branch.BranchLinkCreateListener() {
//            @Override
//            public void onLinkCreate(String url, BranchError error) {
//                if (error == null) {
//                    Timber.e("url = " + url);
//                    shareBranchUrl(url);
//                } else {
//                    Timber.e("error", error.toString());
//                }
//            }
//        });

        String monsterName = branchUniversalObject.getTitle();
        String shareTitle = "Sharing photo";
        String shareMessage = "Check out this amazing photo";
        String copyUrlMessage = "Save " + monsterName + " url";
        String copiedUrlMessage = "Added " + monsterName + " url to clipboard";

        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(DetailActivity.this, shareTitle, shareMessage)
                .setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), copyUrlMessage, copiedUrlMessage)
                .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "More options")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK_MESSENGER)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.PINTEREST)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER);

        branchUniversalObject.showShareSheet(DetailActivity.this, linkProperties, shareSheetStyle, null);
    }

    private void shareBranchUrl(String url) {
        Intent i = new Intent(Intent.ACTION_SEND);
        String shareText = "Check out this amazing photo \n" + url;

        i.putExtra(Intent.EXTRA_TEXT, shareText);
        i.setType("text/plain");
        startActivity(Intent.createChooser(i, getResources().getText(R.string.string_share)));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_set_as)
    void setAsAction() {
        if (!performingAction) {
            performingAction = true;
            actionHelper.setAsAction();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.detail_btn_retry)
    void retryAction() {
        clpContentLoading.smoothToShow();
        layoutError.setVisibility(View.GONE);

        loadData();
    }

    private void loadData() {
        addSubscription(mDataManager.getImage(image).subscribe(new Action1<Image>() {
            @Override
            public void call(Image img) {
                if (image != null && img != null) {
                    isFavorite = img.isFavorite();
                    if (!img.getLocalLink().equals("")) {
                        image.setLocalLink(img.getLocalLink());
                    }

                    if (img.isFavorite()) {
                        image.setFavorite(img.isFavorite());

                        btnFavOff.setVisibility(View.GONE);
                        btnFavOn.setVisibility(View.VISIBLE);
                    }
                }
            }
        }));
        //        RealmResults<Image> result = realm.where(Image.class).equalTo("pexelId", image.getPexelId()).findAll();
        //        if (result.size() > 0) {
        //            Image img = result.get(0);
        //            isFavorite = img.isFavorite();
        //            if (!img.getLocalLink().equals("")) {
        //                image.setLocalLink(img.getLocalLink());
        //            }
        //
        //            if (img.isFavorite()) {
        //                image.setFavorite(img.isFavorite());
        //
        //                btnFavOff.setVisibility(View.GONE);
        //                btnFavOn.setVisibility(View.VISIBLE);
        //            }
        //        }

        addSubscription(HTMLParsingUtil.getImageDetailFromUrl(image.getDetailLink())
                .subscribe(new Observer<ImageDetail>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable instanceof SocketTimeoutException || throwable instanceof UnknownHostException) {
                            layoutError.setVisibility(View.VISIBLE);
                            clpContentLoading.hide();
                        }
                    }

                    @Override
                    public void onNext(ImageDetail imageDetail) {
                        refreshData(imageDetail);
                    }
                }));
    }

    private void refreshData(ImageDetail imageDetail) {
        tvAuthor.setText(String.format(getResources().getString(R.string.string_prefix_by), imageDetail.getAuthor()));

        Glide.with(this)
                .load(imageDetail.getAvatarUrl())

                .placeholder(R.drawable.ic_default_avatar)
                .into(ivAvatar);

        if (image.isFavorite()) {
            btnFavOn.setVisibility(View.VISIBLE);
            btnFavOff.setVisibility(View.GONE);
        } else {
            btnFavOn.setVisibility(View.GONE);
            btnFavOff.setVisibility(View.VISIBLE);
        }

        tvDimen.setText(imageDetail.getDimen());
        tvSize.setText(imageDetail.getSize());

        if (colors.size() == 0) {
            colors.addAll(imageDetail.getColors());
        }

        if (tags.size() == 0) {
            tags.addAll(imageDetail.getTags());
        }

        mColorAdapter.notifyDataSetChanged();
        mTagAdapter.notifyDataSetChanged();

        clpContentLoading.hide();

        mContentLayout.setVisibility(View.VISIBLE);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(400);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mContentLayout.startAnimation(alphaAnimation);
    }

    private void loadBackdropImage(Image image) {
        clpBackdropLoading.smoothToShow();
        Glide.with(this).load(image.getLargeLink()).asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target,
                                               boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        Timber.d("onResourceReady: DetailActivity");
                        Timber.d("onResourceReady: size = " + (resource.getByteCount()/1024));
                        Timber.d("onResourceReady: isFromMemoryCache = " + isFromMemoryCache);
                        Timber.d("onResourceReady: isFirstResource = " + isFirstResource);
                        Timber.d("onResourceReady: resource w = " + resource.getWidth() + " - h = " +
                                resource.getHeight());
                        clpBackdropLoading.hide();
                        return false;
                    }
                }).dontAnimate().into(backDropImage);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            actionHelper.resumeInvokedAction();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        init();
    }

    @Override
    protected void onDestroy() {

        Glide.clear(backDropImage);

        if (layoutAd != null) {
            layoutAd.removeAllViews();
        }
        if (adView != null) {
            adView.destroy();
            adView.setAdListener(null);
            adView = null;
        }

        if (mColorAdapter != null) {
            mColorAdapter = null;
        }
        if (mTagAdapter != null) {
            mTagAdapter = null;
        }
        if (colors != null) {
            colors = null;
        }
        if (tags != null) {
            tags = null;
        }
        if (actionHelper != null) {
            actionHelper.destruct();
            actionHelper = null;
        }
        if (mContext != null) {
            mContext = null;
        }

        super.onDestroy();
    }

    @Override
    protected void injectDependencies() {
        component = DetailComponent.Initializer.init(App.get(this).getAppComponent(), this);
        component.inject(this);
    }


    @Override
    public void onBackPressed() {
        if (isFavorite) {
            if (!image.isFavorite()) {
                if (image.getLocalLink().equals("")) {
                    addSubscription(mDataManager.deleteImage(image).subscribe
                            (new Action1<DeleteResult>() {
                                @Override
                                public void call(DeleteResult deleteResult) {
                                    Timber.d("onBackPress: deleted " + deleteResult.numberOfRowsDeleted() + " item");
                                }
                            }));
                } else {
                    mDataManager.addImage(image).subscribe();
                }
            }
        } else {
            if (image.isFavorite()) {
                mDataManager.addImage(image).subscribe();
            }
        }

        if (isTaskRoot()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        finish();
        overridePendingTransition(R.anim.below_activity_enter, R.anim.above_activity_exit);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        actionHelper.cancelDownload();
    }

    @Override
    public DetailComponent getComponent() {
        return component;
    }
}
