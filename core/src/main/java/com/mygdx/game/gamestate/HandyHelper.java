package com.mygdx.game.gamestate;

import com.mygdx.game.gamestate.UI.console.sjconsole.LogLevel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HandyHelper {
    public  static HandyHelper instance;
    boolean noSpam = true;
    BufferedWriter writer;
    boolean consoleNeedsRefresh = false;

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

    float refreshLogTime = 0;
    float logRefreshPeriod = 0.2f;

    public void refreshLogsInConsole(float delta) {
        if (consoleNeedsRefresh) {
            refreshLogTime += delta;
            if (refreshLogTime > logRefreshPeriod) {
                refreshLogTime = 0f;
                GameState.instance.console.refreshLogs();
            }
        }
    }

    public void log(String toLog){
        if (noSpam) {
            noSpamLog(toLog, LogLevel.DEFAULT);
        } else {
            logInConsole(toLog, LogLevel.DEFAULT);
            saveLog(toLog);
            System.out.println(toLog);
        }
    }

    public void log(String toLog, LogLevel level){
        if (noSpam) {
            noSpamLog(toLog, level);
        } else {
            logInConsole(toLog, level);
            saveLog(toLog);
            System.out.println(toLog);
        }

    }

    String lastString = "";
    long timeOfLastLog;
    long logPeriod = 2000;

    public void noSpamLog(String toLog, LogLevel level) {
        var curTime = System.currentTimeMillis();
        if (!lastString.equals(toLog) || curTime - timeOfLastLog > logPeriod){
            logInConsole(toLog, level);
            System.out.println(toLog);
            saveLog(toLog);
            lastString = toLog;
            timeOfLastLog = curTime;
        }
    }

    long timeOfPeriodicLog;
    long periodicLogTime = 4000;

    public void periodicLog(String toLog){
        if (System.currentTimeMillis() - timeOfPeriodicLog > periodicLogTime){
            System.out.println(toLog);
            logInConsole(toLog, LogLevel.DEFAULT);
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

    private void logInConsole(String toLog, LogLevel lev){
        if (GameState.instance == null || GameState.instance.console == null)
            return;
        GameState.instance.console.logWithoutRefresh(toLog, lev);
        consoleNeedsRefresh = true;
    }

    public void dispose() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
