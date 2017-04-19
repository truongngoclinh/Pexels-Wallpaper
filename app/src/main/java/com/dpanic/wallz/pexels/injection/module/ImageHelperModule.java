package com.dpanic.wallz.pexels.injection.module;

import org.greenrobot.eventbus.EventBus;
import android.content.Context;
import com.dpanic.wallz.pexels.ui.common.ImageActionHelper;
import com.dpanic.wallz.pexels.ui.common.CustomProgressDialog;
import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 3/3/2017.
 * Project: Pexels
 */

@Module
public class ImageHelperModule {

    @Provides
    ImageActionHelper provideImageActionHelper(Context activity, EventBus eventBus) {
        return new ImageActionHelper(activity, eventBus);
    }

    @Provides
    CustomProgressDialog provideProgressDialog(Context activity) {
        return new CustomProgressDialog(activity);
    }


}
