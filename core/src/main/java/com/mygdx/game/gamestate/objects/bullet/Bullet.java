package com.mygdx.game.gamestate.objects.bullet;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
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
        body = GameState.instance.bodyResolver.bulletBody(position.x, position.y, this);

        bulletObject = new GameObject(getName(), GameState.instance.unbox);

        new Box2dBehaviour(body, bulletObject);

        Sprite tracer = new Sprite(GameState.instance.bulletTracer);
        tracer.setRotation(target.angleDeg()+90);

        new BulletTracer(bulletObject, tracer, Globals.DEFAULT_RENDER_ORDER);

        new BulletCollisionBehaviour(bulletObject);


        //new SpriteBehaviour(bulletObject, 0.5f, 0.5f, tile.getTextureRegion(), Globals.DEFAULT_RENDER_ORDER);

        moveVec = target.nor().scl(bulletSpeed);

        body.setLinearDamping(1);
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
