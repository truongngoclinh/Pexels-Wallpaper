package com.dpanic.dpwallz.busevent;

/**
 * Created by dpanic on 3/3/2017.
 * Project: DPWallz
 */

public class ProgressDialogEvent {
    public static final int SHOW_EVENT = 10;
    public static final int UPDATE_EVENT = 11;
    public static final int DISMISS_EVENT = 12;
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
