package com.mygdx.game.gamestate.objects.bodies.mobs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
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
    @Getter int hp = 1;
    @Getter int maxHp = 1;
    protected boolean isAlive = true;

    @Setter @Getter Body body;
    @Setter @Getter Friendliness friendliness = Friendliness.NEUTRAL;
    @Getter @Setter Kind kind;
    @Setter String name;

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

    public void setMaxHp(int maxHp) {
        this.maxHp = Math.max(0, maxHp);
        if (this.hp == 0)
            isAlive = false;
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
        return name == null ? friendliness + " Entity" : name;
    }
    public void kill(){
        isAlive = false;
    };
}
