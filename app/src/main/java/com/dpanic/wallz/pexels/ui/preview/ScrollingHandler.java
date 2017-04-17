package com.dpanic.wallz.pexels.ui.preview;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * Created by dpanic on 3/3/2017.
 * Project: DPWallz
 */

public class ScrollingHandler extends Handler {
    private final WeakReference<PreviewActivity> mActivity;

    public ScrollingHandler(Context activity) {
        mActivity = new WeakReference<>(((PreviewActivity) activity));
    }

    @Override
    public void handleMessage(Message msg) {
        PreviewActivity activity = mActivity.get();
        if (activity != null) {
            activity.handleScrolling();
        }

    }
}
