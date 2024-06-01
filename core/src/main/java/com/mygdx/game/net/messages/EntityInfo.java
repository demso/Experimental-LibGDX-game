package com.mygdx.game.net.messages;

import com.mygdx.game.gamestate.factories.MobsFactoryC;
import com.mygdx.game.net.messages.server.EntitiesMove;

public class EntityInfo {
    public long id;
    public float x, y, xSpeed, ySpeed;
    public MobsFactoryC.Type type;
    public String name;
    public float hp;

    public EntityInfo set(long id, MobsFactoryC.Type type, String name, float hp, float x, float y, float xS, float yS){
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        this.id = id;
        this.type = type;
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

    public EntitiesMove getMove() {
        return new EntitiesMove().set(id, x, y, xSpeed, ySpeed);
    }
}
