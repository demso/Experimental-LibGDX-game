package com.mygdx.game.behaviours.collision;

import com.badlogic.gdx.physics.box2d.Contact;
import com.mygdx.game.*;
import com.mygdx.game.entities.Zombie;
import dev.lyze.gdxUnBox2d.Behaviour;
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
                    zombie.hurt(data.getDamage());
                }
            }
        }

        getGameObject().destroy();
    }

    @Override
    public void awake() {
        super.awake();
    }
}
