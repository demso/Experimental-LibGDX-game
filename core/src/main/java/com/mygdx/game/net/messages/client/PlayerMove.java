package com.mygdx.game.net.messages.client;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.gamestate.player.Player;

public class PlayerMove {
    public String name;
    public float x;
    public float y;
    public float xSpeed;
    public float ySpeed;
    public PlayerMove(){}

    public PlayerMove(String name, float x, float y, float xS, float yS){
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        this.name = name;
    }
}
