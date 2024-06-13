package com.mygdx.game.net.messages.server;

import com.mygdx.game.net.messages.client.PlayerMove;

public class EntitiesMove {
    public long id;
    public float x, y, xSpeed, ySpeed;
    public float xImpulse, yImpulse;

    public EntitiesMove set(long id, float x, float y, float xS, float yS){
        this.id = id;
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        return this;
    }

    public EntitiesMove setImpulse(float x, float y){
        xImpulse = x;
        yImpulse = y;
        return this;
    }
}
