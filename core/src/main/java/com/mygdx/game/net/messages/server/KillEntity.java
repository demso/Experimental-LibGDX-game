package com.mygdx.game.net.messages.server;

public class KillEntity {
    public long entityId;
    public KillEntity set(long id){
        entityId = id;
        return this;
    }
}
