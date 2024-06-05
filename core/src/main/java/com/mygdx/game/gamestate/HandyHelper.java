package com.mygdx.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.UI.console.sjconsole.LogLevel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

public class HandyHelper {
    public  static HandyHelper instance;
    boolean noSpam = true;
    BufferedWriter writer;

    public HandyHelper(){
        try {
            File file = new File(HandyHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "log.txt");
            if (file.exists())
                file.delete();

            file.createNewFile();

            writer = new BufferedWriter(new FileWriter(file, true));
            System.out.println(HandyHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "log.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void log(String toLog){
        if (noSpam) {
            noSpamLog(toLog);
        } else {
            System.out.println(toLog);
            saveLog(toLog);
        }
    }

    public void log(String toLog, boolean noSpam){
        if (noSpam)
            noSpamLog(toLog);
        else {
            if (GameState.instance != null && GameState.instance.console != null)
                GameState.instance.console.log(toLog, LogLevel.DEFAULT);
            saveLog(toLog);
            System.out.println(toLog);
        }
    }

    String lastString = "";
    long timeOfLastLog;
    long logPeriod = 1000;

    public void noSpamLog(String toLog) {
        if (!lastString.equals(toLog) || System.currentTimeMillis() - timeOfLastLog > logPeriod){
            System.out.println(toLog);
            if (GameState.instance != null && GameState.instance.console != null)
                GameState.instance.console.log(toLog, LogLevel.DEFAULT);
            lastString = toLog;
            saveLog(toLog);
            timeOfLastLog = System.currentTimeMillis();
        }
    }

    long timeOfPeriodicLog;
    long periodicLogTime = 1000;

    public void periodicLog(String toLog){

        if (System.currentTimeMillis() - timeOfPeriodicLog > periodicLogTime){
            System.out.println(toLog);
            if (GameState.instance != null && GameState.instance.console != null)
                GameState.instance.console.log(toLog, LogLevel.DEFAULT);
            saveLog(toLog);
            timeOfPeriodicLog = System.currentTimeMillis();
        }
    }

    public synchronized void saveLog(String toLog){
        try {
            writer.write(toLog + "\n");
            writer.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void dispose() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
