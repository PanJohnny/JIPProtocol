package me.panjohnny.jip.server.router;

public abstract sealed class Route permits StaticRoute, DynamicRoute {
    protected String path;
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