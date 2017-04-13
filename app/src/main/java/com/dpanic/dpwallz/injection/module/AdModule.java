package com.dpanic.dpwallz.injection.module;

import android.app.Application;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.dpanic.dpwallz.R;
import com.dpanic.dpwallz.injection.scope.ApplicationScope;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 3/6/2017.
 * Project: DPWallz
 */

@Module
public class AdModule {

    @Provides
    AdRequest provideAdRequest() {
        return new AdRequest.Builder().build();
    }

    @Provides
    NativeExpressAdView provideAdView(Application application) {
        final NativeExpressAdView adView = new NativeExpressAdView(application);
        String native_ads_id = application.getResources().getString(R.string.string_image_list_native_ad_id);
        adView.setAdUnitId(native_ads_id);
        adView.setAdSize(new AdSize(360, 100));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                             ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) application.getResources().getDimension(R.dimen.ad_vertical_margin), 0,
                          (int) application.getResources().getDimension(R.dimen.ad_vertical_margin));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adView.setLayoutParams(params);
        return adView;
    }
}
