package com.dpanic.wallz.pexels.ui.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import com.dpanic.wallz.pexels.injection.HasComponent;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by dpanic on 12/29/2016.
 * Project: Pexels
 */

public abstract class BaseFragment extends Fragment {
    CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        injectDependencies();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
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

    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }

    protected abstract void injectDependencies();
}
