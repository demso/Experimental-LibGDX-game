package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.tiledmap.UserData;

public abstract class Entity implements UserName {
    public enum EntityType {
        PLAYER("Player"),
        NEUTRAL("Neutral"),
        FRIENDLY("Friendly"),
        HOSTILE("Hostile");
        final String name;
        EntityType(String n){
            name = n;
        }
        @Override
        public String toString() {
            return name;
        }
    }
    int hp = 1;
    int maxHp = 1;
    boolean isAlive = true;
    Body body;
    EntityType entityType = EntityType.NEUTRAL;

    public Entity(){}
    public int hurt(int damage){
        hp = Math.max(0, hp-damage);
        if (hp == 0)
            kill();
        return hp;
    }
    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
        if (this.hp == 0)
            isAlive = false;
    }
    public int getHp() {
        return hp;
    }
    public void setMaxHp(int maxHp) {
        this.maxHp = Math.max(0, maxHp);
        if (this.hp == 0)
            isAlive = false;
    }
    public int getMaxHp() {
        return maxHp;
    }
    public Body getBody() {
        return body;
    }
    public void setBody(Body body) {
        this.body = body;
    }
    public EntityType getEntityType() {
        return entityType;
    }
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    public boolean isAlive(){
        return isAlive;
    }
    public Vector2 getPosition(){
        if (body != null)
            return body.getPosition();
        else
            return null;
    }
    @Override
    public String getName() {
        return entityType + " Entity";
    }
    public abstract void kill();
}
