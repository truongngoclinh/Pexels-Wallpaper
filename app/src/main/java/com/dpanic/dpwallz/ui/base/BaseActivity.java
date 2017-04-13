package com.dpanic.dpwallz.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.dpanic.dpwallz.application.DPWallz;
import com.dpanic.dpwallz.injection.component.AppComponent;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by dpanic on 12/29/2016.
 * Project: DPWallz
 */

public abstract class BaseActivity extends AppCompatActivity {

    CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectDependencies();
    }

    @Override
    protected void onDestroy() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
        super.onDestroy();
    }

    protected abstract void injectDependencies();

    protected AppComponent getAppComponent() {
        return ((DPWallz) getApplication()).getAppComponent();
    }

    protected void addSubscription(Subscription subscription) {
        if (subscription == null) {
            return;
        }

        if (compositeSubscription == null) {
            compositeSubscription = new CompositeSubscription();
        }
        compositeSubscription.add(subscription);
    }
}
