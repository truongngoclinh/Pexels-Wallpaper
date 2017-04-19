package com.dpanic.wallz.pexels.injection.module;

import org.greenrobot.eventbus.EventBus;
import com.dpanic.wallz.pexels.injection.scope.ApplicationScope;
import dagger.Module;
import dagger.Provides;

/**
 * Created by dpanic on 1/9/2017.
 * Project: Pexels
 */

@Module
public class EventBusModule {

    @Provides
    @ApplicationScope
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }
}
