package com.dpanic.wallz.pexels.ui.common;

import org.greenrobot.eventbus.EventBus;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;
import com.dpanic.wallz.pexels.R;
import com.dpanic.wallz.pexels.busevent.DownloadEvent;
import com.dpanic.wallz.pexels.busevent.ProgressDialogEvent;
import com.dpanic.wallz.pexels.data.model.Image;
import com.dpanic.wallz.pexels.utils.Constants;
import com.dpanic.wallz.pexels.utils.DownloadUtil;
import com.dpanic.wallz.pexels.utils.FileUtil;
import com.dpanic.wallz.pexels.utils.PermissionUtils;
import rx.Observer;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by dpanic on 11/10/2016.
 * Project: DPWallz
 */

public class ImageActionHelper {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int SET_AS_WALLPAPER_REQUEST_CODE = 101;

    private int currentAction = -1;
    private long downloadId;

    private Context context;
    private Image img;
    private CompositeSubscription compositeSubscription;
    private EventBus eventBus;

    public ImageActionHelper(Context context, EventBus eventBus) {
        this.context = context;
        this.eventBus = eventBus;
        compositeSubscription = new CompositeSubscription();
    }

    public void setImage(Image img) {
        this.img = img;
    }

    public void setAsAction() {
        if (FileUtil.isFileDownloaded(img.getOriginalLink())) {
            performSetAs();
        } else {
            performDownload(Constants.DOWNLOAD_FOR_SET_AS);
        }
    }

    private void performSetAs() {
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.setDataAndType(FileUtil.getImageUri(img.getOriginalLink()), "image/*");
        intent.putExtra("jpg", "image/*");
        ((Activity) context)
                .startActivityForResult(Intent.createChooser(intent, context.getString(R.string.string_set_as)),
                                        SET_AS_WALLPAPER_REQUEST_CODE);
    }

    public void downloadAction() {
        if (FileUtil.isFileDownloaded(img.getOriginalLink())) {
            Toast.makeText(context, context.getResources().getString(R.string.string_the_image_has_already_downloaded),
                           Toast.LENGTH_SHORT).show();
            return;
        }

        performDownload(Constants.DOWNLOAD_FOR_DOWNLOAD);
    }

    public boolean checkPermission() {
         return PermissionUtils
                .requestPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQUEST_CODE);
    }

    public int getCurrentAction() {
        return currentAction;
    }

    public void downloadForPreview() {
        if (FileUtil.isFileDownloaded(img.getOriginalLink())) {
            downloadCompleted(true);
        } else {
            performDownload(Constants.DOWNLOAD_FOR_PREVIEW);
        }
    }

    private void downloadCompleted(boolean isForView) {
        DownloadEvent downloadEvent = new DownloadEvent(DownloadEvent.STATUS_COMPLETE);
        downloadEvent.setForView(isForView);
        eventBus.post(downloadEvent);
    }

    private void performDownload(final int action) {
        currentAction = action;
        if (checkPermission()) {
            return;
        }

        Timber.e("send show event");
        eventBus.post(new ProgressDialogEvent(ProgressDialogEvent.SHOW_EVENT, 0));

        downloadId = DownloadUtil.enqueueDownload(context, img.getOriginalLink(), false);
        compositeSubscription.add(DownloadUtil.getDownloadProgressFromId(context, downloadId).subscribe(new
                                                                                                            Observer<Integer>() {
            @Override
            public void onCompleted() {
                eventBus.post(new ProgressDialogEvent(ProgressDialogEvent.UPDATE_EVENT, 100));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        eventBus.post(new ProgressDialogEvent(ProgressDialogEvent.DISMISS_EVENT, 0));

                        Timber.e("send dismiss event");

                        performActionAfterDownload(action);

                        downloadCompleted(currentAction == Constants.DOWNLOAD_FOR_PREVIEW);

                        currentAction = -1;
                        downloadId = -1;
                    }
                }, 900);

                Toast.makeText(context, context.getResources().getString(R.string.string_download_completed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new ProgressDialogEvent(ProgressDialogEvent.DISMISS_EVENT, 0));
                eventBus.post(new DownloadEvent(DownloadEvent.STATUS_ERROR, throwable));

                String filePath = FileUtil.getLocalPath(img.getOriginalLink());
                if (FileUtil.isFileExists(filePath)) {
                    FileUtil.deleteFile(filePath);
                }

                currentAction = -1;
            }

            @Override
            public void onNext(Integer progress) {
                Timber.e("send update event");
                eventBus.post(new ProgressDialogEvent(ProgressDialogEvent.UPDATE_EVENT, progress));
            }
        }));
    }

    private void performActionAfterDownload(int action) {
        switch (action) {
        case Constants.DOWNLOAD_FOR_DOWNLOAD:
            break;
        case Constants.DOWNLOAD_FOR_SET_AS:
            performSetAs();
            break;
        case Constants.DOWNLOAD_FOR_SHARE:
            performShare();
            break;
        case Constants.DOWNLOAD_FOR_PREVIEW:
            break;
        }
    }

    public void shareAction() {
        if (FileUtil.isFileDownloaded(img.getOriginalLink())) {
            performShare();
        } else {
            performDownload(Constants.DOWNLOAD_FOR_SHARE);
        }
    }

    public void performShare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, FileUtil.getImageUri(img.getOriginalLink()));
        share.setType("image/*");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(share, "Share image File"));
    }

    public void cancelDownload() {
        DownloadUtil.dequeueDownload(context, downloadId);
        FileUtil.deleteFile(FileUtil.getLocalPath(img.getOriginalLink()));
    }

    public void destruct() {
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }

    public void resumeInvokedAction() {
        switch (currentAction) {
        case Constants.DOWNLOAD_FOR_DOWNLOAD:
            downloadAction();
            break;
        case Constants.DOWNLOAD_FOR_SET_AS:
            setAsAction();
            break;
        case Constants.DOWNLOAD_FOR_PREVIEW:
            downloadForPreview();
            break;
        case Constants.DOWNLOAD_FOR_SHARE:
            shareAction();
            break;
        }
    }
}
