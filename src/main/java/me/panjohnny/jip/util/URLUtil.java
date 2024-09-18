package me.panjohnny.jip.util;

public final class URLUtil {
    /**
     * Removes query parameters and fragment from a URL. Trims trailing slashes.
     *
     * @param path The URL to isolate the resource from.
     * @return The resource path.
     */
    public static String isolateResource(String path) {
        // Remove trailing slashes
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        // Remove query parameters
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }

        // Remove fragment
        if (path.contains("#")) {
            path = path.substring(0, path.indexOf("#"));
        }

        return path;
    }
}