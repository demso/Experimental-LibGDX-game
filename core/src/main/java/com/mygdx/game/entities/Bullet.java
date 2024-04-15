package com.mygdx.game.entities;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.BodyData;
import com.mygdx.game.BodyResolver;
import com.mygdx.game.GameState;
import com.mygdx.game.Globals;
import com.mygdx.game.behaviours.collision.BulletCollisionBehaviour;
import com.mygdx.game.behaviours.SpriteBehaviour;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import lombok.Getter;

public class Bullet implements BodyData {
    GameObject bulletObject;
    Body body;
    Vector2 moveVec;
    @Getter int damage = 3;
    public Bullet(TiledMapTile tile,  Vector2 position, Vector2 target) {
        float bulletSpeed = 200f;
        body = BodyResolver.bulletBody(position.x, position.y, this);

        bulletObject = new GameObject(getName(), GameState.unbox);

        new Box2dBehaviour(body, bulletObject);
        new SpriteBehaviour(bulletObject, 0.5f, 0.5f, tile.getTextureRegion(), Globals.DEFAULT_RO);
        new BulletCollisionBehaviour(bulletObject);
        //new SoutBehaviour("bulletLog", false, bulletObject);

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

    @Override
    public Object getData() {
        return this;
    }
}
