package com.mygdx.game.net.messages.server;

import com.mygdx.game.net.messages.EntityInfo;

public class KillEntity {
    public long entityId;
    public KillEntity set(long id){
        entityId = id;
        return this;
    }
}
