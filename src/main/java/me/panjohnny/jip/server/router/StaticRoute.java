package me.panjohnny.jip.server.router;

import me.panjohnny.jip.util.URLUtil;

/**
 * Static route, which matches the path exactly.
 * <p>
 * For example, if the path is "/home", it will match only "/home".
 *
 * @author Jan Štefanča
 * @see DynamicRoute
 * @since 1.0
 */
public final class StaticRoute extends Route {
    public StaticRoute(String path) {
        super(URLUtil.isolateResource(path));
    }

    @Override
    public boolean matches(String path) {
        return this.path.equals(URLUtil.isolateResource(path));
    }
}