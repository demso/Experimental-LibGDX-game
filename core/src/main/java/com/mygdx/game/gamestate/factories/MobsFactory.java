package com.mygdx.game.gamestate.factories;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;

public class MobsFactory {
    public enum Type {
        ZOMBIE("ZOMBIE");

        String name;
        Type(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    static World world = GameState.Instance.world;
    private static Vector2 tempPosition = new Vector2();

    public static Zombie spawnZombie(float x, float y){
        tempPosition.set(x,y);
        Zombie zombie = new Zombie(TileResolver.getTile("zombie1"), world, tempPosition);
        return zombie;
    }

    public static Entity spawnEntity(Type type, float x, float y){
        Entity entity = null;
        switch (type){
            case ZOMBIE -> entity = spawnZombie(x, y);
        }
        return entity;
    }

    public static BodyDef bodyDef(float x, float y, BodyDef.BodyType type){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = type;
        bodyDef.active = false;
        return bodyDef;
    }
    public static BodyDef bodyDef(float x, float y, BodyDef.BodyType type, boolean bullet){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = type;
        bodyDef.active = false;
        bodyDef.bullet = bullet;
        return bodyDef;
    }

}
