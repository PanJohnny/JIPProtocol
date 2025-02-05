package me.panjohnny.jip.commons;

/**
 * Reprezentuje verze protokolu. Pro získání aktuální verze využijte {@link #getDefault()}.
 *
 * @author Jan Štefanča
 * @since 1.0
 */
@SuppressWarnings("SameParameterValue")
public enum JIPVersion {
    JIP1_0("JIP/1.0");

    private final String version;

    JIPVersion(String version) {
        this.version = version;
    }

    /**
     * @return verze protokolu v podobě JIP/x.x
     */
    @Override
    public String toString() {
        return version;
    }

    /**
     * Získá aktuální verzi protokolu.
     *
     * @return aktuální verze protokolu
     */
    @SuppressWarnings("SameReturnValue")
    public static JIPVersion getDefault() {
        return JIPVersion.JIP1_0; // Going through versions would be O(n), when hardcoded this is O(1)
    }
}
