package com.mygdx.game.net.server;

import com.esotericsoftware.minlog.Log;
import com.mygdx.game.SecondGDXGame;

public class CustomKryoLogger extends Log.Logger {
    @Override
    protected void print(String message) {
        SecondGDXGame.instance.helper.saveLog(message);
    }
}
