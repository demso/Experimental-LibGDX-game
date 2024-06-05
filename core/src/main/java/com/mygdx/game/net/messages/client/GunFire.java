package com.mygdx.game.net.messages.client;

public class GunFire {
    public long playerID;
    public GunFire set(long id){
        playerID = id;
        return this;
    }
}
