package com.mygdx.game.net.messages.client;

public class DropItems {
    public long playerId;
    public long itemUid;
    public float x, y;

    public DropItems set(long playerId, float x, float y, long itemId){
        this.playerId = playerId;
        this.itemUid = itemId;
        this.x = x;
        this.y = y;
        return this;
    }
}
