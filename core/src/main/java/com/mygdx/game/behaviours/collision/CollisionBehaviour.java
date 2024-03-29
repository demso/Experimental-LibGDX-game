package com.mygdx.game.behaviours.collision;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.game.UserName;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class CollisionBehaviour<T> extends BehaviourAdapter {
    T data;
    Body body;
    Fixture thisFixture;
    Fixture otherFixture;

    Body thisBody;
    Body otherBody;

    Object thisUserData;
    Object otherUserData;

    Object thisFixtureUserData;
    Object otherFixtureUserData;

    String otherBodyUserName;

    public CollisionBehaviour(GameObject gameObject) {
        super(gameObject);
        body = getGameObject().getBox2dBehaviour().getBody();
        data = (T) body.getUserData();
    }

    public void preCol(Contact contact){
        if (contact.getFixtureA().getBody() == body){
            thisFixture = contact.getFixtureA();
            otherFixture = contact.getFixtureB();
        } else {
            thisFixture = contact.getFixtureB();
            otherFixture = contact.getFixtureA();
        }

        thisBody = body;
        otherBody = otherFixture.getBody();

        thisUserData = thisBody.getUserData();
        otherUserData = otherBody.getUserData();

        thisFixtureUserData = thisFixture.getUserData();
        otherFixtureUserData = otherFixture.getUserData();

        otherBodyUserName = otherUserData instanceof UserName ? ((UserName) otherUserData).getName() : "";
    }
}
