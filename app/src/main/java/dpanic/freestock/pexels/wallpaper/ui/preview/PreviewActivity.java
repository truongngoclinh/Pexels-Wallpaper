package dpanic.freestock.pexels.wallpaper.ui.preview;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dpanic.wallz.pexels.R;
import com.wang.avi.AVLoadingIndicatorView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dpanic.freestock.pexels.wallpaper.application.App;
import dpanic.freestock.pexels.wallpaper.busevent.DownloadEvent;
import dpanic.freestock.pexels.wallpaper.busevent.ProgressDialogEvent;
import dpanic.freestock.pexels.wallpaper.data.StorIODBManager;
import dpanic.freestock.pexels.wallpaper.data.model.Image;
import dpanic.freestock.pexels.wallpaper.injection.HasComponent;
import dpanic.freestock.pexels.wallpaper.injection.component.PreviewComponent;
import dpanic.freestock.pexels.wallpaper.ui.base.BaseActivity;
import dpanic.freestock.pexels.wallpaper.ui.common.CustomProgressDialog;
import dpanic.freestock.pexels.wallpaper.ui.common.ImageActionHelper;
import dpanic.freestock.pexels.wallpaper.utils.Constants;
import dpanic.freestock.pexels.wallpaper.utils.FileUtil;

public class PreviewActivity extends BaseActivity implements HasComponent<PreviewComponent>, Dialog.OnCancelListener {

    @BindView(R.id.preview_toolbar)
    Toolbar toolbar;

    @BindView(R.id.preview_image)
    ImageView preImage;

    @BindView(R.id.preview_scroll_view)
    HorizontalScrollView previewScrollView;

    @BindView(R.id.preview_error_container)
    RelativeLayout layoutError;

    @BindView(R.id.preview_loading_progress)
    AVLoadingIndicatorView clpImageLoading;

    private String imgLink;
    private Image image;
    private PreviewComponent component;
    private ActionBar actionBar;

    @Inject
    ScrollingHandler mHandler;

    @Inject
    EventBus eventBus;

    @Inject
    ImageActionHelper actionHelper;

    @Inject
    CustomProgressDialog progressDialog;

    @Inject
    StorIODBManager mDataManager;

    private int currentProgress = 0;
    private int loadedProgress = 0;

    private static class WeakPrefHandler extends Handler {
        private final WeakReference<PreviewActivity> mActivity;

        WeakPrefHandler(PreviewActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PreviewActivity activity = mActivity.get();
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preview);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.hide();
        }
        setTitle("");

        eventBus.register(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        eventBus.unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessage(ProgressDialogEvent event) {
        switch (event.getEventType()) {
            case ProgressDialogEvent.SHOW_EVENT:
                if (progressDialog != null) {
                    progressDialog.setOnCancelListener(this);
                    progressDialog.reset();
                    progressDialog.show();
                }

                progressHandler.sendEmptyMessageDelayed(Constants.PROGRESS_UPDATE, Constants.PROGRESS_NORMAL_UPDATE_INTERVAL);
                break;
            case ProgressDialogEvent.UPDATE_EVENT:
//                progressDialog.setProgress(event.getProgress());
                loadedProgress = event.getProgress();
                break;
            case ProgressDialogEvent.DISMISS_EVENT:
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog.showDownloadError();
                    progressDialog.reset();
                }
                break;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessage(DownloadEvent event) {
        if (event.getStatus() == DownloadEvent.STATUS_COMPLETE) {
            if (event.isForView()) {
                loadPreviewImage();
            }

            image.setLocalLink(imgLink);

            mDataManager.addImage(image).subscribe();
        } else {
            layoutError.setVisibility(View.VISIBLE);
            clpImageLoading.hide();
            Throwable throwable = event.getException();

            actionBar.show();
            FileUtil.deleteFile(imgLink);
        }
    }

    private void init() {
        Intent intent = getIntent();
        image = intent.getParcelableExtra(Constants.IMAGE_INSTANCE);

        imgLink = FileUtil.getLocalPath(image.getOriginalLink());

        actionHelper.setImage(image);

        if (FileUtil.isFileDownloaded(image.getOriginalLink())) {
            loadPreviewImage();
        } else {
            actionHelper.downloadForPreview();
        }

        previewScrollView.setOnTouchListener(new View.OnTouchListener() {
            float startX = 0, startY = 0, endX, endY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mHandler.removeMessages(0);
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    startY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    endX = event.getRawX();
                    endY = event.getRawY();

                    if (Math.abs(endX - startX) < 20 && Math.abs(endY - startY) < 20) {
                        if (actionBar != null) {
                            if (actionBar.isShowing()) {
                                actionBar.hide();
                            } else {
                                actionBar.show();
                            }
                        }
                    }
                    break;
                }
                return false;
            }
        });
    }

    public void handleScrolling() {
        previewScrollView.smoothScrollBy(2, 0);
        mHandler.sendEmptyMessageDelayed(0, 50);
    }

    private void loadPreviewImage() {
        if (actionHelper.checkPermission()) {
            return;
        }

        clpImageLoading.smoothToShow();
        Glide.with(this)
                .load(imgLink)
                .asBitmap()
                .animate(R.anim.preview_showing_img)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontTransform()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target,
                                               boolean isFirstResource) {
                        if (e instanceof SocketTimeoutException) {
                            Toast.makeText(PreviewActivity.this,
                                           getResources().getString(R.string.string_no_internet_connection),
                                           Toast.LENGTH_SHORT).show();
                        }
                        if (e != null) {
                            e.printStackTrace();
                        }
                        clpImageLoading.smoothToHide();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        preImage.setVisibility(View.VISIBLE);
                        clpImageLoading.smoothToHide();
                        mHandler.sendEmptyMessage(0);
                        return false;
                    }
                })
                .skipMemoryCache(true)
                .into(preImage);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_preview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            break;
        case R.id.pre_menu_set_as:
            actionHelper.setAsAction();
            break;
        case R.id.pre_menu_share:
            actionHelper.performShare();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (actionHelper.getCurrentAction() == -1) {
                loadPreviewImage();
            } else {
                actionHelper.resumeInvokedAction();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        if (actionHelper != null) {
            actionHelper.destruct();
            actionHelper = null;
        }

        Glide.clear(preImage);

        super.onDestroy();
    }

    @Override
    protected void injectDependencies() {
        component = PreviewComponent.Initializer.init(App.get(this).getAppComponent(), this);
        component.inject(this);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.preview_btn_retry)
    void onClick() {
        layoutError.setVisibility(View.GONE);
        actionHelper.downloadForPreview();
        actionBar.hide();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        actionHelper.cancelDownload();
        finish();
    }

    @Override
    public PreviewComponent getComponent() {
        return component;
    }

    //    @Override
    //    protected void injectDependencies(App application, AppComponent component) {
    //        component.inject(this);
    //    }
}
