package de.kb1000.multiwindow.client.gl.events;

import java.nio.file.Path;

@FunctionalInterface
public interface FilesDroppedCallback {
    void onFilesDropped(Path[] files);
}
