package com.mygdx.game;

public class HandyHelper {
    boolean noSpam = true;
    public void log(String toLog){
        if (noSpam)
            noSpamLog(toLog);
        else
            System.out.println(toLog);
    }

    String lastString = "";
    public void noSpamLog(String toLog) {
        if (!lastString.equals(toLog)){
            System.out.println(toLog);
            lastString = toLog;
        }
    }
}
