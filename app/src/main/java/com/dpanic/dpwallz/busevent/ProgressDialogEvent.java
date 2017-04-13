package com.dpanic.dpwallz.busevent;

/**
 * Created by dpanic on 3/3/2017.
 * Project: DPWallz
 */

public class ProgressDialogEvent {
    public static final int SHOW_EVENT = 0;
    public static final int UPDATE_EVENT = 1;
    public static final int DISMISS_EVENT = 2;
    private int progress;
    private int eventType;

    public ProgressDialogEvent(int eventType, int progress) {
        this.progress = progress;
        this.eventType = eventType;
    }

    public int getProgress() {
        return progress;
    }

    public int getEventType() {
        return eventType;
    }
}
