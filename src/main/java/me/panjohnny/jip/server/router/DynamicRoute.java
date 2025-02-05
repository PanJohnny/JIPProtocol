package me.panjohnny.jip.server.router;

import me.panjohnny.jip.util.URLUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Dynamická trasa, umožňuje dynamické porovnávání cesty.
 * <p>
 * <h2>Dynamické porovnávání cesty</h2>
 * - Trasy s [] jsou dynamické trasy.
 * - Například, pokud je cesta "/home/[id]", bude odpovídat "/home/1", "/home/2", atd.
 * - Můžete vnořit dynamické trasy, například "/home/[id]/[action]" bude odpovídat "/home/1/edit", "/home/2/delete", atd.
 *
 * @author Jan Štefanča
 * @see StaticRoute
 * @since 1.0
 */
public final class DynamicRoute extends Route {
    /**
     * Vytvoří novou dynamickou trasu s danou cestou.
     *
     * @param path cesta trasy
     */
    public DynamicRoute(String path) {
        super(URLUtil.isolateResource(path));
    }

    /**
     * Porovná, zda daná cesta odpovídá této dynamické trase.
     *
     * @param path cesta k porovnání
     * @return true, pokud cesta odpovídá, jinak false
     */
    @Override
    public boolean matches(String path) {
        String[] pathParts = URLUtil.isolateResource(path).split("/");
        String[] routeParts = this.path.split("/");
        if (pathParts.length != routeParts.length) {
            return false;
        }
        for (int i = 0; i < pathParts.length; i++) {
            if (routeParts[i].startsWith("[") && routeParts[i].endsWith("]")) {
                continue;
            }
            if (!pathParts[i].equals(routeParts[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Analyzuje parametry z dané cesty.
     *
     * @param path cesta k analýze
     * @return nemodifikovatelná mapa parametrů
     */
    public Map<String, String> parseParameters(String path) {
        String[] pathParts = URLUtil.isolateResource(path).split("/");
        String[] routeParts = this.path.split("/");
        HashMap<String, String> parameters = new HashMap<>();
        for (int i = 0; i < pathParts.length; i++) {
            if (routeParts[i].startsWith("[") && routeParts[i].endsWith("]")) {
                parameters.put(routeParts[i].substring(1, routeParts[i].length() - 1), pathParts[i]);
            }
        }

        return Collections.unmodifiableMap(parameters);
    }
}