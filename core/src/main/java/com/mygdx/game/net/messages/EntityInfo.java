package com.mygdx.game.net.messages;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.factories.MobsFactory;
import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.server.EntitiesMove;

public class EntityInfo {
    public long id;
    public float x, y, xSpeed, ySpeed;
    public MobsFactory.Type type;
    public String name;
    public float hp;

    public EntityInfo set(long id, MobsFactory.Type type, String name, float hp, float x, float y, float xS, float yS){
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
