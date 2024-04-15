package com.mygdx.game.behaviours.collision;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entities.Player;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class PlayerCollisionBehaviour extends CollisionBehaviour<Player> {
    Array<Body> closeBodies = new Array<>();
    public PlayerCollisionBehaviour(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
        preCol(contact);
        if (thisFixture.isSensor() && !otherFixture.isSensor()){
            closeBodies.add(otherBody);
            updatePlayerClosestObject();
        }
    }

    @Override
    public void onCollisionExit(Behaviour other, Contact contact) {
        preCol(contact);
        if (thisFixture.isSensor() && !otherFixture.isSensor()){
            closeBodies.removeValue(otherBody, true);
            updatePlayerClosestObject();
        }
    }

    public void updatePlayerClosestObject(){
        if (closeBodies.isEmpty()){
            data.closestObject = null;
            return;
        }
        float minDist = Float.MAX_VALUE;
        float dist;
        for (Body closeBody : closeBodies) {
            dist = body.getPosition().dst2(closeBody.getPosition());
            if (dist < minDist){
                data.closestObject = closeBody;
                minDist = dist;
            }
        }
    }

    public Array<Body> getCloseBodies(){
        return closeBodies;
    }
}
