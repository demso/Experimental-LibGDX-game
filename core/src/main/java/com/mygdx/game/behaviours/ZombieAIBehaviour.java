package com.mygdx.game.behaviours;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.GameItself;
import com.mygdx.game.entities.Zombie;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class ZombieAIBehaviour extends BehaviourAdapter {

    Zombie zombie;
    Body body;
    Vector2 zeroVector = new Vector2();

    public ZombieAIBehaviour(GameObject gameObject) {
        super(gameObject);
        body = getGameObject().getBox2dBehaviour().getBody();
        zombie = (Zombie) body.getUserData();
    }

    @Override
    public void fixedUpdate() {
        Vector2 moveVec = GameItself.player.getPosition();
        moveVec = moveVec.set(moveVec.x - body.getPosition().x, moveVec.y - body.getPosition().y).nor();
        body.applyLinearImpulse(moveVec.scl(zombie.getSpeed()), zeroVector, true);
    }
}
