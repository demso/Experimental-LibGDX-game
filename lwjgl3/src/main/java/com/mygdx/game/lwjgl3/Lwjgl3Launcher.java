package com.mygdx.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.SecondGDXGame;

import java.io.*;
import java.net.URISyntaxException;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.

        try {
            createApplication();
        } catch (Exception ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            ex.printStackTrace();
            saveLog(errors.toString());
        }

    }

    private static Lwjgl3Application createApplication() {

        return new Lwjgl3Application(new SecondGDXGame(), getDefaultConfiguration());
    }

    private static void saveLog(String s){
        try {
            File file = new File(Lwjgl3Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "crashlog.txt");
            if (file.exists())
                file.delete();
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
//            String consoleLog = null;
//
//            if (SecondGDXGame.instance != null
//                    && SecondGDXGame.instance.gameScreen != null
//                    && SecondGDXGame.instance.gameScreen.gameState != null
//                    && SecondGDXGame.instance.gameScreen.gameState.console != null)
//                consoleLog = SecondGDXGame.instance.gameScreen.gameState.console.getLog().printToString();
            if (SecondGDXGame.instance != null && SecondGDXGame.instance.server != null) {
                SecondGDXGame.instance.server.dispose();
            }
            if (SecondGDXGame.instance != null && SecondGDXGame.instance.client != null) {
                SecondGDXGame.instance.client.dispose();
            }
            if (SecondGDXGame.instance != null && SecondGDXGame.instance.helper != null)
                SecondGDXGame.instance.helper.dispose();
            writer.write("Exception: \n" + s);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
//        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
//        configuration.setTitle("SecondGDXGame");
//        configuration.useVsync(true);
//        //// Limits FPS to the refresh rate of the currently active monitor.
//        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
//        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
//        //// useful for testing performance, but can also be very stressful to some hardware.
//        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
//        configuration.setWindowedMode(640, 480);
//        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
//        return configuration;
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(false);
        config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        config.setTitle("My GDX Game");
        config.setWindowedMode(1000, 700);
        config.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        //config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,2);
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL20, 4, 5);
        return config;
    }
}
