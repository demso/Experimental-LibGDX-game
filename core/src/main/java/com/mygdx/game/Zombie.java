package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.behaviours.SpriteBehaviour;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.SoutBehaviour;

public class Zombie extends Entity{
    CustomBox2DSprite sprite;
    int damage = 4;
    GameObject zombieObject;
    public Zombie(TiledMapTile tile, World world, Vector2 position){
        setEntityType(EntityType.HOSTILE);
        setHp(10);
        setMaxHp(10);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);

        CircleShape circle = new CircleShape();
        circle.setRadius(0.2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.01f;
        fixtureDef.restitution = 0.01f;
        fixtureDef.filter.categoryBits = GameItself.ZOMBIE_CF;
        fixtureDef.filter.maskBits = (short) (fixtureDef.filter.maskBits & ~GameItself.LIGHT_CF);

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);

        MassData massData = new MassData();
        massData.mass = 60f;
        massData.center.set(new Vector2(0f,0f));

        body.setMassData(massData);
        body.setLinearDamping(10);
        body.setUserData(this);
        circle.dispose();

        body.setUserData(this);

        zombieObject = new GameObject(getName(), GameItself.unbox);

        new Box2dBehaviour(body, zombieObject);
        new SpriteBehaviour(zombieObject, tile.getTextureRegion(), GameConstants.ZOMBIE_RO);
        new SoutBehaviour("zombieLogger", false, zombieObject);
    }

    @Override
    public int hurt(int damage) {
        int hp = super.hurt(damage);
        return hp;
    }

    @Override
    public String getName(){
        return "zombie";
    }

    @Override
    public void kill() {
        zombieObject.destroy();
    }
}
