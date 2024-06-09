package com.mygdx.game.net.messages.client;

public class Ready {
    public long playerId;
    public Ready set(long id) {
        playerId = id;
        return this;
    }
}
