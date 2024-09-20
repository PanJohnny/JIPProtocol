package me.panjohnny.jip.server.router;

import me.panjohnny.jip.server.RequestHandler;

import java.util.HashMap;

public class Router {
    private final HashMap<Route, RequestHandler> routes;
    public Router() {
        routes = new HashMap<>();
    }

    public void route(String path, RequestHandler handler) {
        route(Route.of(path), handler);
    }

    public void route(Route route, RequestHandler handler) {
        routes.put(route, handler);
    }

    public RequestHandler getHandler(String path) {
        for (Route route : routes.keySet()) {
            if (route.matches(path)) {
                return routes.get(route);
            }
        }
        return null;
    }

    public RequestHandler getHandler(Route route) {
        return routes.get(route);
    }

    public boolean hasRoute(String path) {
        System.out.println("Searching for route: " + path);
        for (Route route : routes.keySet()) {
            if (route.matches(path)) {
                return true;
            }
            System.out.println("Loop");
        }
        System.out.println("Route not found: " + path);
        return false;
    }

    public Route getRoute(String path) {
        for (Route route : routes.keySet()) {
            if (route.matches(path)) {
                return route;
            }
        }
        return null;
    }

    public void removeRoute(String path) {
        routes.remove(getRoute(path));
    }
}