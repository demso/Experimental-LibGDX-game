package com.mygdx.game.net.messages.client;

public class StopStorageUpdate {
    public long playerId;
    public int x, y;

    public StopStorageUpdate set(long id, int x, int y) {
        this.playerId = id;
        this.x = x;
        this.y = y;
        return this;
    }
}
