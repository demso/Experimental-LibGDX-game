package com.mygdx.game.gamestate;

import com.strongjoshua.console.GUIConsole;
import com.strongjoshua.console.LogLevel;

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
            if (GameState.Instance != null && GameState.Instance.console != null)
                GameState.Instance.console.log(toLog, LogLevel.DEFAULT);
            lastString = toLog;
        }
    }
}
