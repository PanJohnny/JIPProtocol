package me.panjohnny.jip.util;

/**
 * Utilitní třída pro manipulaci s URL.
 * <p>
 * Poskytuje metody pro izolaci části URL obsahující zdroj odstraněním dotazovacích parametrů, fragmentů a koncových lomítek.
 * </p>
 *
 * @author Jan Štefanča
 * @since 1.0
 */
public final class URLUtil {
    /**
     * Odstraní dotazovací parametry a fragment z URL. Ořízne koncová lomítka.
     *
     * @param path URL, ze které se má izolovat část obsahující zdroj.
     * @return cesta ke zdroji.
     */
    public static String isolateResource(String path) {
        // Odstraní koncová lomítka
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        // Odstraní dotazovací parametry
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }

        // Odstraní fragment
        if (path.contains("#")) {
            path = path.substring(0, path.indexOf("#"));
        }

        return path;
    }
}