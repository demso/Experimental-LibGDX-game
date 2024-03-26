package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.tiledmap.BodyUserName;

public class Entity implements BodyUserName {
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
    Body body;
    EntityType entityType = EntityType.NEUTRAL;

    public Entity(){}
    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
    }
    public int getHp() {
        return hp;
    }
    public void setMaxHp(int maxHp) {
        this.maxHp = Math.max(0, maxHp);
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
}
