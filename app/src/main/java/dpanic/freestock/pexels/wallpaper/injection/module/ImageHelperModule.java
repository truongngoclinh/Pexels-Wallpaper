package dpanic.freestock.pexels.wallpaper.injection.module;

import org.greenrobot.eventbus.EventBus;
import android.content.Context;
import dpanic.freestock.pexels.wallpaper.ui.common.ImageActionHelper;
import dpanic.freestock.pexels.wallpaper.ui.common.CustomProgressDialog;
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
