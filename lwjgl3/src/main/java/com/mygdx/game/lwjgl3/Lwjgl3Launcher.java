package com.mygdx.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.SecondGDXGame;

import java.io.*;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    static int refreshRate = 75;
    static boolean useDisplayRefreshRate = true;
    static boolean useVSync = false;
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.

        try {
            createApplication();
        } catch (Exception ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            ex.printStackTrace();
            saveCrashLog(errors.toString());
//            try {
//                if (SecondGDXGame.instance != null && SecondGDXGame.instance.server != null) {
//                    SecondGDXGame.instance.server.dispose();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                if (SecondGDXGame.instance != null && SecondGDXGame.instance.client != null) {
//                    SecondGDXGame.instance.client.dispose();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            try {
                if (SecondGDXGame.instance != null && SecondGDXGame.instance.helper != null)
                    SecondGDXGame.instance.helper.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new SecondGDXGame(), getDefaultConfiguration());
    }

    private static void saveCrashLog(String s){
        try {
            File file = new File(Lwjgl3Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "crashlog.txt");
            if (file.exists())
                file.delete();
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            writer.write("Crashed because of error: \n" + s);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        if (useDisplayRefreshRate)
            refreshRate = Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate;
        config.setForegroundFPS(refreshRate);
        config.useVsync(useVSync);
        config.setTitle("My GDX Game");
        config.setWindowedMode(1280, 720);
        config.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL20, 4, 5);
        return config;
    }
}
