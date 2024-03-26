package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Zombie extends Entity{
    EntitySprite sprite;
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

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);

        MassData massData = new MassData();
        massData.mass = 60f;
        massData.center.set(new Vector2(0f,0f));

        body.setMassData(massData);
        body.setLinearDamping(2);
        circle.dispose();

        sprite = new EntitySprite(tile.getTextureRegion(), "zombie", this, 1f, 1f);

        body.setUserData(sprite);
    }
    @Override
    public String getName(){
        return "zombie";
    }
}
