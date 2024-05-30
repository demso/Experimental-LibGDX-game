package com.mygdx.game.net.messages;

import com.mygdx.game.gamestate.factories.MobsFactory;

public class EntityInfo {
    public float id;
    public float x, y, xSpeed, ySpeed;
    public MobsFactory.Type type;
    public String name;
    public float hp;

    public EntityInfo set(float id, String name, float hp, float x, float y, float xS, float yS){
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        this.id = id;
        this.name = name;
        this.hp = hp;
        return this;
    }
    public EntityInfo set(float x, float y, float xS, float yS){
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        return this;
    }
}
