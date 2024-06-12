package com.mygdx.game.net.messages.client;

public class GrenadeInfo {
    public String tileName;
    public float timeToDetonation;
    public float x, y, xS, yS;
    public long uid;

    public GrenadeInfo(){}

    public GrenadeInfo set(long uid,String tileName, float timeToDetonation, float x, float y, float xS, float yS){
        this.uid = uid;
        this.tileName = tileName;
        this.timeToDetonation = timeToDetonation;
        this.x = x;
        this.y = y;
        this.xS = xS;
        this.yS = yS;
        return this;
    }
}
