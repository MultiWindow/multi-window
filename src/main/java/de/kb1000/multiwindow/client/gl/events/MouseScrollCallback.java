package de.kb1000.multiwindow.client.gl.events;

@FunctionalInterface
public interface MouseScrollCallback {
    void onMouseScroll(double xOffset, double yOffset);
}
