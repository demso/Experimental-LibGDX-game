package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class MobsFactory {
    static World world;
    static boolean inited = false;
    private static Vector2 tempPosition = new Vector2();

    public static void init(World w){
        world = w;
        inited = true;
    }

    public static Zombie spawnZombie(float x, float y){
        tempPosition.set(x,y);
        Zombie zombie = new Zombie(TileResolver.getTile("zombie1"), world, tempPosition);
        return zombie;
    }

    public static Entity spawnEntity(String name, float x, float y){
        Entity entity = null;
        switch (name){
            case "zombie" -> entity = spawnZombie(x, y);
        }
        return entity;
    }

}
