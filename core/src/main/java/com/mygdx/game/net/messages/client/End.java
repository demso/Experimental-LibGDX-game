package com.mygdx.game.net.messages.client;

public class End {
    public String playerName;
    public End set(String name) {
        playerName = name;
        return this;
    }
}
