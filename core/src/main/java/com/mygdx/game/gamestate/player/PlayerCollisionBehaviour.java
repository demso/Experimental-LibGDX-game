package com.mygdx.game.gamestate.player;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.bodies.CollisionBehaviour;
import com.mygdx.game.gamestate.objects.tiles.Storage;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class PlayerCollisionBehaviour extends CollisionBehaviour<Player> {
    /*
    protected Player data;
    protected Body body;
    protected Fixture thisFixture;
    protected Fixture otherFixture;

    Body thisBody;
    protected Body otherBody;

    Object thisUserData;
    protected Object otherUserData;

    Object thisFixtureUserData;
    Object otherFixtureUserData;
    */

    Array<Body> closeBodies = new Array<>();
    public PlayerCollisionBehaviour(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
        preCol(contact);
        if (otherUserData instanceof Interactable && thisFixture.isSensor() && !otherFixture.isSensor()){
            closeBodies.add(otherBody);
            updatePlayerClosestObject();
        }
    }

    @Override
    public void onCollisionExit(Behaviour other, Contact contact) {
        preCol(contact);
        if (otherUserData instanceof Interactable && thisFixture.isSensor() && !otherFixture.isSensor()){
            closeBodies.removeValue(otherBody, true);
            updatePlayerClosestObject();
            if (otherUserData instanceof Storage storage
                    && GameState.instance.hud.storageInventoryHUD.isVisible()
                    && GameState.instance.hud.storageInventoryHUD.getStorage() == storage){
                GameState.instance.hud.closeStorageInventoryHUD(false);
            }
        }

    }

    public void updatePlayerClosestObject(){
        Body formelyClosest = data.closestObject;
        if (closeBodies.isEmpty()){
            data.closestObject = null;
        } else {
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

        if (formelyClosest != null && formelyClosest.getUserData() != GameState.instance.hud.storageInventoryHUD.getStorage() && GameState.instance.hud.playerInventoryHud.isVisible() && GameState.instance.clientPlayer.getClosestObject() != null && GameState.instance.clientPlayer.getClosestObject().getUserData() instanceof Storage storage)
            GameState.instance.hud.showRequestStorageInventoryHUD(storage);

        if (data.getClosestObject() != null && data.getClosestObject().getUserData() instanceof Storage && (formelyClosest == null || !(formelyClosest.getUserData() instanceof Storage)))
            GameState.instance.hud.playerInventoryHud.storageInventoryNear();
        else if ((data.getClosestObject() == null || !(data.getClosestObject().getUserData() instanceof Storage)) && formelyClosest != null && formelyClosest.getUserData() instanceof Storage)
            GameState.instance.hud.playerInventoryHud.storageInventoryFar();
    }

    public Array<Body> getCloseBodies(){
        return closeBodies;
    }
}
