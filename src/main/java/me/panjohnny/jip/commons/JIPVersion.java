package me.panjohnny.jip.commons;

@SuppressWarnings("SameParameterValue")
public enum JIPVersion {
    JIP1_0("JIP/1.0");

    private final String version;
    JIPVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return version;
    }

    @SuppressWarnings("SameReturnValue")
    public static JIPVersion getDefault() {
        return JIPVersion.JIP1_0; // Going through versions would be O(n), when hardcoded this is O(1)
    }
}
