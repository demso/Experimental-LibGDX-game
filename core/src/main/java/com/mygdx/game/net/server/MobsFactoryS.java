package com.mygdx.game.net.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.factories.MobsFactoryC;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.ServerZombieAIBehaviour;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.ZombieAIBehaviour;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.ZombieCollisionBehaviour;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class MobsFactoryS {
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

    World world;
    private Vector2 tempPosition = new Vector2();

    public MobsFactoryS(World w){
        world = w;
    }

    public Zombie spawnZombie(long id, float x, float y){
        tempPosition.set(x,y);
        Zombie zombie = new Zombie(TileResolver.getTile("zombie"), id, world, tempPosition);

        zombie.zombieObject = new GameObject(zombie.getName(), ServGameState.instance.unbox);

        new Box2dBehaviour(zombie.getBody(), zombie.zombieObject);
        new SpriteBehaviour(zombie.zombieObject, TileResolver.getTile("zombie").getTextureRegion(), Globals.ZOMBIE_RENDER_ORDER);
        new ZombieCollisionBehaviour(zombie.zombieObject);
        zombie.zombieHandler = new ServerZombieAIBehaviour(zombie.zombieObject);
        return zombie;
    }

    public Entity spawnEntity(long id, MobsFactoryC.Type type, float x, float y){
        Entity entity = null;
        switch (type){
            case ZOMBIE -> entity = spawnZombie(id, x, y);
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
