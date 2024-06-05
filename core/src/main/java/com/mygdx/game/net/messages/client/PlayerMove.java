package com.mygdx.game.net.messages.client;

public class PlayerMove {
    public long playerId;
    public float x;
    public float y;
    public float xSpeed;
    public float ySpeed;
    public float rotation;
    public PlayerMove(){}

    public PlayerMove set(long id, float x, float y, float xS, float yS, float rot){
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        this.playerId = id;
        rotation = rot;
        return this;
    }
}
