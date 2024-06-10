package com.mygdx.game.gamestate.objects.bodies.mobs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
import com.mygdx.game.net.messages.server.ZombieMove;
import lombok.Getter;
import lombok.Setter;

public abstract class Entity implements BodyData {
    public enum Friendliness {
        PLAYER("Player"),
        NEUTRAL("Neutral"),
        FRIENDLY("Friendly"),
        HOSTILE("Hostile");
        final String name;
        Friendliness(String n){
            name = n;
        }
        @Override
        public String toString() {
            return name;
        }
    }
    public enum Kind {
        ZOMBIE,
        PLAYER,
        ANOTHER_PLAYER
    }
    @Getter @Setter long id;
    @Getter protected float hp = 1;
    @Getter protected float maxHp = 1;
    protected boolean isAlive = true;

    transient @Setter @Getter protected Body body;
    @Setter @Getter protected Friendliness friendliness = Friendliness.NEUTRAL;
    @Getter @Setter protected Kind kind;
    @Setter protected String name;

    public Entity(){}

    public float hurt(float damage){
        hp = Math.max(0, hp-damage);
        if (hp == 0)
            kill();
        return hp;
    }
    public void setHp(float hp) {
        this.hp = Math.max(0, hp);
        if (this.hp == 0)
            isAlive = false;
    }

    public void setMaxHp(float maxHp) {
        this.maxHp = Math.max(0, maxHp);
        if (this.hp == 0)
            isAlive = false;
    }

    public boolean isAlive(){
        return isAlive;
    }

    @Override
    public String getName() {
        return name == null ? friendliness + " Entity" : name;
    }
    public void kill(){
        isAlive = false;
    };

    public void serverUpdate(ZombieMove move){}

    public void setPosition(float x, float y){
        getBody().setTransform(x, y, getBody().getTransform().getRotation());
    }
    public Vector2 getPosition(){
        if (body != null)
            return body.getPosition();
        else
            return null;
    }

    public Vector2 getVelocity(){
        return body.getLinearVelocity();
    }
}
