package dpanic.freestock.pexels.wallpaper.injection.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Scope;

/**
 * Created by dpanic on 3/6/2017.
 * Project: Pexels
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivityScope {
}
