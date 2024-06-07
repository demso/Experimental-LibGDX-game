package com.mygdx.game.net.messages.client;

public class NeedsStorageUpdate {
    public int x, y;
    public long playerId;
    public NeedsStorageUpdate set(long id, int x, int y){
        this.x = x;
        this.y = y;
        playerId = id;
        return this;
    }
}
