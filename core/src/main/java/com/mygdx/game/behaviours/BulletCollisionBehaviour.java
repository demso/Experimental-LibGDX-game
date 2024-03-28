package com.mygdx.game.behaviours;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.game.*;
import com.mygdx.game.tiledmap.UserData;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class BulletCollisionBehaviour extends BehaviourAdapter {
    Bullet bullet;
    Body bulletBody;
    public BulletCollisionBehaviour(GameObject gameObject) {
        super(gameObject);
        bulletBody = getGameObject().getBox2dBehaviour().getBody();
        bullet = (Bullet) bulletBody.getUserData();
    }

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {

        Fixture[] fixtures = new Fixture[]{contact.getFixtureA(), contact.getFixtureB()};
        for (int i = 0; i < 2; i++) {
            Fixture thisFixture = fixtures[i];
            Fixture anotherFixture = fixtures[(i + 1) % 2];

            Body thisBody = thisFixture.getBody();
            Body anotherBody = anotherFixture.getBody();

            Object thisUserData = thisBody.getUserData();
            Object anotherUserData = anotherBody.getUserData();

            if (thisUserData instanceof UserName) {
                String bodyUserName = ((UserName) thisUserData).getName();
                switch (bodyUserName) {
                    case "bullet" -> {
                                getGameObject().destroy();
                                if (anotherUserData instanceof Zombie zombie){
                                    zombie.hurt(bullet.getDamage());
                                }
                            }
                }
            }
        }
    }

    @Override
    public void awake() {
        super.awake();
    }
}
