package com.dpanic.dpwallz.injection.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Scope;

/**
 * Created by dpanic on 2/27/2017.
 * Project: DPWallz
 */

@Scope
@Retention(RetentionPolicy.CLASS)
public @interface ApplicationScope {
}
