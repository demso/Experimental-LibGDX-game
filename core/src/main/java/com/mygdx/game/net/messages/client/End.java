package com.mygdx.game.net.messages.client;

public class End {
    public long playerId = -1;
    public End set(long id) {
        playerId = id;
        return this;
    }
}
