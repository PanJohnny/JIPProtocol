package me.panjohnny.jip.server.router;

/**
 * Route is a resource path on the server. Because, this path may need to be dynamic in order to accept parameters, there is DynamicRoute.
 *
 * @see DynamicRoute
 * @see StaticRoute
 * @author Jan Štefanča
 */
public abstract sealed class Route permits StaticRoute, DynamicRoute {
    protected final String path;
    public Route(String path) {
        this.path = path;
    }

    public abstract boolean matches(String path);

    public static Route of(String path) {
        if (path.contains("[") && path.contains("]")) {
            return new DynamicRoute(path);
        }
        return new StaticRoute(path);
    }
}