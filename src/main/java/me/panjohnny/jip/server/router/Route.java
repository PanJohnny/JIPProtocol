package me.panjohnny.jip.server.router;

/**
 * Trasa je cesta k prostředku na serveru. Protože tato cesta může potřebovat být dynamická, aby přijímala parametry, existuje třída DynamicRoute.
 *
 * @author Jan Štefanča
 * @see DynamicRoute
 * @see StaticRoute
 * @since 1.0
 */
public abstract sealed class Route permits StaticRoute, DynamicRoute {
    protected final String path;

    /**
     * Vytvoří novou trasu s danou cestou.
     *
     * @param path cesta trasy
     */
    public Route(String path) {
        this.path = path;
    }

    /**
     * Porovná, zda daná cesta odpovídá této trase.
     *
     * @param path cesta k porovnání
     * @return true, pokud cesta odpovídá, jinak false
     */
    public abstract boolean matches(String path);

    /**
     * Vytvoří instanci trasy na základě dané cesty.
     *
     * @param path cesta trasy
     * @return instance trasy
     */
    public static Route of(String path) {
        if (path.contains("[") && path.contains("]")) {
            return new DynamicRoute(path);
        }
        return new StaticRoute(path);
    }
}