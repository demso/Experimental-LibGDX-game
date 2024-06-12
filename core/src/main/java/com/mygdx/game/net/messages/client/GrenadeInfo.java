package com.mygdx.game.net.messages.client;

public class GrenadeInfo {
    public String tileName;
    public float timeToDetonation;
    public float x, y, xS, yS;
    public float rotation;
    public long uid;

    public GrenadeInfo(){}

    public GrenadeInfo set(long uid,String tileName, float timeToDetonation, float x, float y, float xS, float yS, float rotation) {
        this.uid = uid;
        this.tileName = tileName;
        this.timeToDetonation = timeToDetonation;
        this.x = x;
        this.y = y;
        this.xS = xS;
        this.yS = yS;
        this.rotation = rotation;
        return this;
    }
}
