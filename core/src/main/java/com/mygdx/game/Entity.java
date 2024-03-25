package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;



public class Entity {
    int hp = 1;
    int maxHp = 1;
    Body body;
    Box2DSprite sprite;
    EntityType entityType = EntityType.FRIENDLY;

    public Entity(Body body, TextureRegion r){

    }

    public enum EntityType {
        PLAYER, FRIENDLY, HOSTILE
    }

    //Action when player pressed 'E' beside entity
    public void interact() {
    }

    public int makeDamage(int damage) {
        hp = Math.max(0, hp - damage);
        return hp;
    }

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

    public Box2DSprite getSprite() {
        return sprite;
    }

    public void setSprite(Box2DSprite sprite) {
        this.sprite = sprite;
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
}
