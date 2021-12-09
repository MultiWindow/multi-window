package de.kb1000.multiwindow.client.gl.events;

@FunctionalInterface
public interface SizeChangedCallback {
    void onSizeChanged(int width, int height);
}
