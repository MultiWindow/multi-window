package de.kb1000.multiwindow.client.gl.events;

@FunctionalInterface
public interface MouseButtonCallback {
    void onMouseButton(int button, int action, int mods);
}
