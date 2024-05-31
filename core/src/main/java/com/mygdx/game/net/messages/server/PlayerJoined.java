package com.mygdx.game.net.messages.server;

import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.client.PlayerMove;

public class PlayerJoined {
    public PlayerInfo playerInfo;
    public PlayerJoined(){ }

    public PlayerJoined(PlayerInfo plInf){
        playerInfo = plInf;
    }
}
