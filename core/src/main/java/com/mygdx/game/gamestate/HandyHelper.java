package com.mygdx.game.gamestate;

import com.mygdx.game.gamestate.UI.console.sjconsole.LogLevel;

public class HandyHelper {
    boolean noSpam = true;
    public void log(String toLog){
        if (noSpam)
            noSpamLog(toLog);
        else
            System.out.println(toLog);
    }

    public void log(String toLog, boolean noSpam){
        if (noSpam)
            noSpamLog(toLog);
        else
            System.out.println(toLog);
    }

    String lastString = "";
    public void noSpamLog(String toLog) {
        if (!lastString.equals(toLog)){
            System.out.println(toLog);
            if (GameState.instance != null && GameState.instance.console != null)
                GameState.instance.console.log(toLog, LogLevel.DEFAULT);
            lastString = toLog;
        }
    }
}
