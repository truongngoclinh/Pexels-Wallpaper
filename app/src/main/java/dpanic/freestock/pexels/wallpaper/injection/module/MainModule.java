package dpanic.freestock.pexels.wallpaper.injection.module;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import dpanic.freestock.pexels.wallpaper.ui.common.ImageAdapter;
import dpanic.freestock.pexels.wallpaper.ui.category.CategoryAdapter;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dpanic.wallz.pexels.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import dagger.Module;
import dagger.Provides;
import dpanic.freestock.pexels.wallpaper.ui.main.AppRate;

/**
 * Created by dpanic on 3/6/2017.
 * Project: Pexels
 */

@Module
public class MainModule {
    @Provides
    ImageAdapter provideImageAdapter(Context context, AdRequest adRequest, NativeExpressAdView adView) {
        return new ImageAdapter(context, adRequest, adView);
    }

    @Provides
    CategoryAdapter provideCategoryAdapter(Context context) {
        return new CategoryAdapter(context);
    }

    @Provides
    MaterialDialog provideNewVersionDialog(final Context context) {
        return new MaterialDialog.Builder(context)
                .title(R.string.string_update_available)
                .content(R.string.string_new_app_version)
                .negativeText(R.string.string_later)
                .negativeColor(ContextCompat.getColor(context, R.color.color_primary_50))
                .positiveText(R.string.string_update)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // update
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                                     Uri.parse("market://details?id=" + context.getPackageName())));
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, context.getString(R.string.string_no_play_store),
                                           Toast.LENGTH_SHORT).show();
                        }
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // cancel
                        dialog.dismiss();
                    }
                }).build();
    }

    @Provides
    AppRate provideAppRate(Context context) {
        return new AppRate(context)
                .setShowIfAppHasCrashed(false)
                .setMinLaunchesUntilPrompt(3);
    }

}
