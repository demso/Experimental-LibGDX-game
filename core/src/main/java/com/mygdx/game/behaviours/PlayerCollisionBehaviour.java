package com.mygdx.game.behaviours;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Player;
import com.mygdx.game.UserName;
import com.mygdx.game.tiledmap.UserData;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.Box2dWorldContactListener;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class PlayerCollisionBehaviour extends BehaviourAdapter {
    Player player;
    Body playerBody;
    Array<Body> closeBodies = new Array<>();
    public PlayerCollisionBehaviour(GameObject gameObject) {
        super(gameObject);
        playerBody = gameObject.getBox2dBehaviour().getBody();
        player = (Player) playerBody.getUserData();
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
                    case "player" -> {
                        if (thisFixture.isSensor() && !anotherFixture.isSensor()) closeBodies.add(anotherBody);
                        updatePlayerClosestObject();
                    }
                }
            }
        }
    }

    @Override
    public void onCollisionStay(Behaviour other) {

    }

    @Override
    public void onCollisionExit(Behaviour other, Contact contact) {
        var fixtureA = contact.getFixtureA();
        var fixtureB = contact.getFixtureB();
        var dataA = contact.getFixtureA().getBody().getUserData();
        var dataB = contact.getFixtureB().getBody().getUserData();
        if (dataA instanceof UserName && ((UserName) dataA).getName().equals("player") && fixtureA.isSensor() && !fixtureB.isSensor()){
            closeBodies.removeValue(contact.getFixtureB().getBody(), true);
            updatePlayerClosestObject();
        }
        if (dataB instanceof UserName && ((UserName) dataB).getName().equals("player") && !fixtureA.isSensor() && fixtureB.isSensor()){
            closeBodies.removeValue(contact.getFixtureA().getBody(), true);
            updatePlayerClosestObject();
        }
    }

    public void updatePlayerClosestObject(){
        if (closeBodies.isEmpty()){
            player.closestObject = null;
            return;
        }
        float minDist = Float.MAX_VALUE;
        float dist = 0;
        for (Body closeBody : closeBodies) {
            dist = playerBody.getPosition().dst2(closeBody.getPosition());
            if (dist < minDist){
                player.closestObject = closeBody;
                minDist = dist;
            }

        }
    }

    public Array<Body> getCloseBodies(){
        return closeBodies;
    }
}
