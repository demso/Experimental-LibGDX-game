package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.behaviours.collision.BulletCollisionBehaviour;
import com.mygdx.game.behaviours.SpriteBehaviour;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.SoutBehaviour;
import lombok.Getter;

public class Bullet implements UserName {
    GameObject bulletObject;
    Body body;
    Vector2 moveVec;
    @Getter int damage = 3;
    public Bullet(TiledMapTile tile, World world, Vector2 position, Vector2 target) {
        float bulletSpeed = 200f;
        BodyDef bodyDef = MobsFactory.bodyDef(position.x, position.y, BodyDef.BodyType.DynamicBody, true);
        body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(0.04f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = GameItself.BULLET_CF;
        fixtureDef.filter.maskBits = (short) (fixtureDef.filter.maskBits & ~GameItself.LIGHT_CF & ~GameItself.PLAYER_CF & ~GameItself.PLAYER_INTERACT_CF);
        circle.dispose();
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);

        MassData massData = new MassData();
        massData.mass = 0.007f;
        massData.center.set(new Vector2(0f,0f));
        body.setMassData(massData);

        body.setUserData(this);

        bulletObject = new GameObject(getName(), GameItself.unbox);

        new Box2dBehaviour(body, bulletObject);
        new SpriteBehaviour(bulletObject, 0.5f, 0.5f, tile.getTextureRegion(), GameConstants.DEFAULT_RO);
        new BulletCollisionBehaviour(bulletObject);
        new SoutBehaviour("bulletLog", false, bulletObject);

        moveVec = target.nor().scl(bulletSpeed);

        //body.applyForceToCenter(vv, true);
        //body.applyLinearImpulse(vv, body.getPosition(), true);
        body.setLinearVelocity(moveVec);
    }

    public Vector2 getPosition(){
        return body.getPosition();
    }

    @Override
    public String getName() {
        return "bullet";
    }
}
