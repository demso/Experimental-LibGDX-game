package com.mygdx.game.net.messages;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.net.messages.server.EntitiesMove;

public class EntityInfo {
    public long id;
    public float x, y, xSpeed, ySpeed;
    public Entity.Kind type;
    public String name;
    public float hp, maxHp;
    public boolean isAlive;

    public EntityInfo set(long id, Entity.Kind type, String name, float hp, float x, float y, float xS, float yS){
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

    public EntityInfo set(Entity entity){
        Vector2 pos = entity.getPosition();
        Vector2 vel = entity.getVelocity();
        this.x = pos.x;
        this.y = pos.y;
        xSpeed = vel.x;
        ySpeed = vel.y;
        this.id = entity.getId();
        this.type = entity.getKind();
        this.name = entity.getName();
        this.hp = entity.getHp();
        maxHp = entity.getMaxHp();
        return this;
    }

    public EntitiesMove getMove() {
        return new EntitiesMove().set(id, x, y, xSpeed, ySpeed);
    }
}
