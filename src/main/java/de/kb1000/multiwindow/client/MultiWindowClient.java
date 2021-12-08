package de.kb1000.multiwindow.client;

import de.kb1000.multiwindow.client.gui.ScreenWindow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class MultiWindowClient implements ClientModInitializer {
    public static final List<ScreenWindow> ALL_WINDOWS = new ArrayList<>();
    public static final boolean RENDERDOC_ENABLED = true;
    @Override
    public void onInitializeClient() {
//        if (RENDERDOC_ENABLED) {
//            System.loadLibrary("renderdoc");
//        }
    }
}
