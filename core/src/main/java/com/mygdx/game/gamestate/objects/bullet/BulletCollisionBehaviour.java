package com.mygdx.game.gamestate.objects.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.bodies.CollisionBehaviour;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class BulletCollisionBehaviour extends CollisionBehaviour<Bullet> {
    public BulletCollisionBehaviour(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
       preCol(contact);

        if (otherFixture.isSensor()) {
            return;
        }

        switch (otherBodyUserName) {
            case "zombie" -> {
                if (otherUserData instanceof Zombie zombie){
                    //zombie.hurt(data.getDamage());
                    GameState.instance.client.onHit(zombie.getId(), data.getDamage());
                }
            }
        }

        BulletTracer tracer = getGameObject().getBehaviour(BulletTracer.class);
        tracer.setPosition(contact.getWorldManifold().getPoints()[0]);


        getGameObject().destroy();
    }

    @Override
    public void awake() {
        super.awake();
    }
}
