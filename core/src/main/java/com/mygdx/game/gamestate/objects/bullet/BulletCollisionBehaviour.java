package com.mygdx.game.gamestate.objects.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.bodies.CollisionBehaviour;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class BulletCollisionBehaviour extends CollisionBehaviour<Bullet> {
    boolean real;
    public BulletCollisionBehaviour(GameObject gameObject, boolean real) {
        super(gameObject);
        this.real = real;
    }

    @Override
    public boolean onCollisionPreSolve(Behaviour other, Contact contact, Manifold oldManifold) {
//        preCol(contact);
//
//        if (otherFixture.isSensor()) {
//            return true;
//        }
//
//        switch (otherBodyUserName) {
//            case "zombie" -> {
//                if (otherUserData instanceof Zombie zombie){
//                    //zombie.hurt(data.getDamage());
//                    GameState.instance.client.onHit(zombie.getId(), data.getDamage(), new Vector2(thisBody.getLinearVelocity()).scl);
//                }
//            }
//        }
//
//        BulletTracer tracer = getGameObject().getBehaviour(BulletTracer.class);
//        tracer.setPosition(contact.getWorldManifold().getPoints()[0]);
//
//
//        getGameObject().destroy();
        return false;
    }

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
       preCol(contact);

        if (otherFixture.isSensor()) {
            return;
        }

<<<<<<< HEAD
        if (real && otherUserData instanceof Zombie zombie){
            GameState.instance.client.onHit(zombie.getId(), data.getDamage(), new Vector2(thisBody.getLinearVelocity()).scl(thisBody.getMass()));
=======
        switch (otherBodyUserName) {
            case "zombie" -> {
                if (otherUserData instanceof Zombie zombie){
                    zombie.hurt(data.getDamage());
                    //GameState.instance.client.onHit(zombie.getId(), data.getDamage(), new Vector2(thisBody.getLinearVelocity()).scl(thisBody.getMass()));
                }
            }
>>>>>>> single
        }

        body.setLinearVelocity(0, 0);

        BulletTracer tracer = getGameObject().getBehaviour(BulletTracer.class);
        tracer.setPosition(contact.getWorldManifold().getPoints()[0]);

        getGameObject().destroy();
    }

    @Override
    public void awake() {
        super.awake();
    }
}
