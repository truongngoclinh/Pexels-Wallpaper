package com.dpanic.wallz.pexels.ui.detail;

import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

/**
 * Created by dpanic on 10/7/2016.
 * Project: DPWallz
 */

class FixFlowLayoutManager extends FlowLayoutManager {

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }
}
