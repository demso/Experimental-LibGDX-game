package com.mygdx.game.gamestate.objects.bodies;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class CollisionBehaviour<T> extends BehaviourAdapter {
    protected T data;
    protected Body body;
    protected Fixture thisFixture;
    protected Fixture otherFixture;

    Body thisBody;
    protected Body otherBody;

    Object thisUserData;
    protected Object otherUserData;

    Object thisFixtureUserData;
    Object otherFixtureUserData;

    protected String otherBodyUserName;

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

        otherBodyUserName = otherUserData instanceof BodyData ? ((BodyData) otherUserData).getName() : "";
    }
}
