package com.mygdx.game.net.messages.client;

public class TookItem {
    public long uid;
    public long playerId;

    public TookItem set(long id, long playerId) {
        this.playerId = playerId;
        uid = id;
        return this;
    }
}
