package com.dpanic.dpwallz.injection.module;

import org.greenrobot.eventbus.EventBus;
import com.dpanic.dpwallz.injection.scope.ApplicationScope;
import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 1/9/2017.
 * Project: DPWallz
 */

@Module
public class EventBusModule {

    @Provides
    @ApplicationScope
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }
}
