package me.panjohnny.jip.server.router;

import me.panjohnny.jip.server.RequestHandler;

import java.util.HashMap;

/**
 * Správce routování.
 *
 * @author Jan Štefanča
 * @see Route
 * @since 1.0
 */
public class Router {
    private final HashMap<Route, RequestHandler> routes;

    /**
     * Vytvoří nového správce routování.
     */
    public Router() {
        routes = new HashMap<>();
    }

    /**
     * Přidá novou trasu s daným handlerem.
     *
     * @param path    cesta trasy
     * @param handler handler požadavků
     */
    public void route(String path, RequestHandler handler) {
        route(Route.of(path), handler);
    }

    /**
     * Přidá novou trasu s daným handlerem.
     *
     * @param route   trasa
     * @param handler handler požadavků
     */
    public void route(Route route, RequestHandler handler) {
        routes.put(route, handler);
    }

    /**
     * Získá handler pro danou cestu.
     *
     * @param path cesta
     * @return handler požadavků nebo null, pokud trasa neexistuje
     */
    public RequestHandler getHandler(String path) {
        for (Route route : routes.keySet()) {
            if (route.matches(path)) {
                return routes.get(route);
            }
        }
        return null;
    }

    /**
     * Získá handler pro danou trasu.
     *
     * @param route trasa
     * @return handler požadavků nebo null, pokud trasa neexistuje
     */
    public RequestHandler getHandler(Route route) {
        return routes.get(route);
    }

    /**
     * Zkontroluje, zda existuje trasa pro danou cestu.
     *
     * @param path cesta
     * @return true, pokud trasa existuje, jinak false
     */
    public boolean hasRoute(String path) {
        for (Route route : routes.keySet()) {
            if (route.matches(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Získá trasu pro danou cestu.
     *
     * @param path cesta
     * @return trasa nebo null, pokud trasa neexistuje
     */
    public Route getRoute(String path) {
        for (Route route : routes.keySet()) {
            if (route.matches(path)) {
                return route;
            }
        }
        return null;
    }

    /**
     * Odstraní trasu pro danou cestu.
     *
     * @param path cesta
     */
    public void removeRoute(String path) {
        routes.remove(getRoute(path));
    }
}