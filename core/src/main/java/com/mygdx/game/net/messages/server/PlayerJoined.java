package com.mygdx.game.net.messages.server;

import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.client.PlayerMove;

public class PlayerJoined extends PlayerMove {
    public PlayerJoined(){ }

    public PlayerJoined(PlayerInfo plInf){
        this.x = plInf.x;
        this.y = plInf.y;
        this.name = plInf.getName();
    }
}
