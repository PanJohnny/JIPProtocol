package me.panjohnny.jip.server.router;

import me.panjohnny.jip.util.URLUtil;

/**
 * Static route, which matches the path exactly.
 * <p>
 * For example, if the path is "/home", it will match only "/home".
 * @see DynamicRoute
 * @author Jan Štefanča
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