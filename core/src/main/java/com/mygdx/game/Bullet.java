package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

public class Bullet implements UserName {
    CustomBox2DSprite sprite;
    Body body;
    Vector2 moveVec;
    int damage = 3;
    public Bullet(TiledMapTile tile, World world, Vector2 position, Vector2 target) {
        float bulletSpeed = 200f;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.04f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = GameItself.BULLET_CF;
        fixtureDef.filter.maskBits = (short) (fixtureDef.filter.maskBits & ~GameItself.LIGHT_CF & ~GameItself.PLAYER_CF & ~GameItself.PLAYER_INTERACT_CF);
        body.setBullet(true);
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);

        MassData massData = new MassData();
        massData.mass = 0.007f;
        massData.center.set(new Vector2(0f,0f));
        body.setMassData(massData);

        sprite = new CustomBox2DSprite(tile.getTextureRegion(), "bullet", this);
        sprite.setSize(0.5f, 0.5f);

        body.setUserData(sprite);

        circle.dispose();

        moveVec = target.nor().scl(bulletSpeed);

        //body.applyForceToCenter(vv, true);
        //body.applyLinearImpulse(vv, body.getPosition(), true);
        body.setLinearVelocity(moveVec);
    }

    public int getDamage(){
        return damage;
    }

    public Vector2 getPosition(){
        return body.getPosition();
    }

    @Override
    public String getName() {
        return "bullet";
    }
}
