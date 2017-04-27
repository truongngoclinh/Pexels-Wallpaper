package dpanic.freestock.pexels.wallpaper.ui.common;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

/**
 * Created by dpanic on 4/27/17.
 * Project: DPWallz
 */

public class MyAppBarBehavior extends AppBarLayout.Behavior {

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, false);
    }
}
