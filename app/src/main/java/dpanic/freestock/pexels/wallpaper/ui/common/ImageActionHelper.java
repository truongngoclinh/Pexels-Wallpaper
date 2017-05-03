package dpanic.freestock.pexels.wallpaper.ui.common;

import org.greenrobot.eventbus.EventBus;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.dpanic.wallz.pexels.R;
import dpanic.freestock.pexels.wallpaper.busevent.DownloadEvent;
import dpanic.freestock.pexels.wallpaper.busevent.ProgressDialogEvent;
import dpanic.freestock.pexels.wallpaper.data.model.Image;
import dpanic.freestock.pexels.wallpaper.utils.Constants;
import dpanic.freestock.pexels.wallpaper.utils.DownloadUtil;
import dpanic.freestock.pexels.wallpaper.utils.FileUtil;
import dpanic.freestock.pexels.wallpaper.utils.PermissionUtils;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by dpanic on 11/10/2016.
 * Project: Pexels
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
    private Subscription progressSubscription;

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

        progressSubscription = DownloadUtil.getDownloadProgressFromId(context, downloadId).subscribe(new
                                                                                                            Observer<Integer>() {
            @Override
            public void onCompleted() {
                eventBus.post(new ProgressDialogEvent(ProgressDialogEvent.UPDATE_EVENT, 100));

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {

//                        eventBus.post(new ProgressDialogEvent(ProgressDialogEvent.DISMISS_EVENT, 0));
//
//                        Timber.e("send dismiss event");

//                        performActionAfterDownload(action);
//
//                        downloadCompleted(currentAction == Constants.DOWNLOAD_FOR_PREVIEW);
//
//                        currentAction = -1;
//                        downloadId = -1;
//                    }
//                }, 900);

//                Toast.makeText(context, context.getResources().getString(R.string.string_download_completed), Toast.LENGTH_SHORT).show();
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
                Timber.w("progress on next");
                eventBus.post(new ProgressDialogEvent(ProgressDialogEvent.UPDATE_EVENT, progress));
            }
        });

        compositeSubscription.add(progressSubscription);
    }

    public void onDownloadCompleted() {
        performActionAfterDownload(currentAction);

        downloadCompleted(currentAction == Constants.DOWNLOAD_FOR_PREVIEW);

        currentAction = -1;
        downloadId = -1;

        Toast.makeText(context, context.getResources().getString(R.string.string_download_completed), Toast.LENGTH_SHORT).show();
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
        if (progressSubscription != null && !progressSubscription.isUnsubscribed()) {
            progressSubscription.unsubscribe();
        }
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

    public Observable<String> getShortUrl(String author) {
        final BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                // The identifier is what Branch will use to de-dupe the content across many different Universal Objects
//                .setCanonicalIdentifier("pexels/" + img.getPexelId())
                // This is where you define the open graph structure and how the object will appear on Facebook or in a deepview
                .setTitle(img.getName())
                .setContentDescription(context.getString(R.string.string_prefix_by, author))
                .setContentImageUrl(img.getLargeLink())
                // You use this to specify whether this content can be discovered publicly - default is public
//                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                // Here is where you can add custom keys/values to the deep link data
                .addContentMetadata("pexels_id", img.getPexelId())
                .addContentMetadata("name", img.getName())
                .addContentMetadata("detailLink", img.getDetailLink())
                .addContentMetadata("largeLink", img.getLargeLink())
                .addContentMetadata("localLink", img.getLocalLink())
                .addContentMetadata("orgLink", img.getOriginalLink());

        String storeLink = "https://play.google.com/store/apps/details?id=" + context.getPackageName();
        final LinkProperties linkProperties = new LinkProperties()
                .addTag("image_detail")
                .setChannel("facebook")
                .setFeature("sharing")
                .setStage("1")
//                .addControlParameter("$desktop_url", storeLink)
                .addControlParameter("$android_deeplink_path", "image/view/");

        branchUniversalObject.listOnGoogleSearch(context);

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        if (error == null) {
                            Timber.e("url = " + url);
                            subscriber.onNext(url);
                            subscriber.onCompleted();
                        } else {
                            Timber.e("error", error.toString());
                            subscriber.onError(new Exception(error.getMessage()));
                        }
                    }
                });
            }
        });

//        String title = branchUniversalObject.getTitle();
//        String shareTitle = "Sharing photo";
//        String shareMessage = "Check out this amazing photo";
//        String copyUrlMessage = "Save " + title + " url";
//        String copiedUrlMessage = "Added " + title + " url to clipboard";
//        branchUniversalObject.listOnGoogleSearch(context);
//
//        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(context, shareTitle, shareMessage)
//                .setStyleResourceID(R.style.Share_Sheet_Style)
//                .setCopyUrlStyle(context.getResources().getDrawable(android.R.drawable.ic_menu_send), copyUrlMessage,
//                                 copiedUrlMessage)
//                .setMoreOptionStyle(context.getResources().getDrawable(android.R.drawable.ic_menu_search), "More " +
//                        "options")
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK_MESSENGER)
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.PINTEREST)
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
//                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER);
//
//        branchUniversalObject.showShareSheet(((Activity) context), linkProperties, shareSheetStyle, null);
    }
}
