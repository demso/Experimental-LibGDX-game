package com.mygdx.game.net.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.factories.MobsFactory;

public class ServerMobsFactory extends MobsFactory {
    private Vector2 tempPosition = new Vector2();

    public ServerMobsFactory(World w){
        super(w);
    }

//    @Override
//    public Zombie spawnZombie(long id, float x, float y){
//        tempPosition.set(x,y);
//        Zombie zombie = new Zombie(TileResolver.getTile("zombie"), id, world, tempPosition);
//
//        zombie.zombieObject = new GameObject(zombie.getName(), ServerGameState.instance.unbox);
//
//        new Box2dBehaviour(zombie.getBody(), zombie.zombieObject);
//        new SpriteBehaviour(zombie.zombieObject, TileResolver.getTile("zombie").getTextureRegion(), Globals.ZOMBIE_RENDER_ORDER);
//        new ZombieCollisionBehaviour(zombie.zombieObject);
//        zombie.zombieHandler = new ServerZombieAIBehaviour(zombie.zombieObject);
//        return zombie;
//    }
//
//    @Override
//    public Entity spawnEntity(long id, Entity.Kind type, float x, float y){
//        Entity entity = null;
//        switch (type){
//            case ZOMBIE -> entity = spawnZombie(id, x, y);
//        }
//        return entity;
//    }
//
//    public static BodyDef bodyDef(float x, float y, BodyDef.BodyType type){
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.position.set(x, y);
//        bodyDef.type = type;
//        bodyDef.active = false;
//        return bodyDef;
//    }
//    public static BodyDef bodyDef(float x, float y, BodyDef.BodyType type, boolean bullet){
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.position.set(x, y);
//        bodyDef.type = type;
//        bodyDef.active = false;
//        bodyDef.bullet = bullet;
//        return bodyDef;
//    }
}
