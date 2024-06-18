package com.mygdx.game.gamestate.objects.bodies.mobs.zombie;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.net.messages.server.ZombieMove;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class ZombieAIBehaviour extends BehaviourAdapter {

    Zombie zombie;
    Body body;
    Vector2 zeroVector = new Vector2();
    ZombieMove zombieMove;
    boolean needsUpdate;
    Vector2 tempVec = new Vector2();
    Vector2 velocity = new Vector2();

    public ZombieAIBehaviour(GameObject gameObject) {
        super(gameObject);
        body = getGameObject().getBox2dBehaviour().getBody();
        zombie = (Zombie) body.getUserData();
    }


    @Override
    public void start() {
        zombie.getBody().setLinearDamping(0);
    }

    @Override
    public void update(float delta) {
        if (needsUpdate && zombieMove != null){
            Vector2 pos = zombie.getBody().getPosition();

            velocity.set(zombieMove.xSpeed, zombieMove.ySpeed);

            float offsetX = Math.abs(zombieMove.x - pos.x), offsetY = Math.abs(zombieMove.y - pos.y);
            if (offsetX > 0.5 || offsetY > 0.5)
                zombie.setPosition(zombieMove.x, zombieMove.y);
            else if (offsetX > 0.05f || offsetY > 0.05f) {
                velocity.add(zombieMove.x - pos.x, zombieMove.y - pos.y);
            }
            body.setLinearVelocity(velocity);

            needsUpdate = false;
        }
    }

    @Override
    public void fixedUpdate() {
//        Vector2 moveVec = GameState.instance.player.getPosition();
//        moveVec = moveVec.set(moveVec.x - body.getPosition().x, moveVec.y - body.getPosition().y).nor();
//        body.applyLinearImpulse(moveVec.scl(zombie.getSpeed()), zeroVector, true);
    }

    public void serverUpdate(ZombieMove move){
        needsUpdate = true;
        zombieMove = move;
    }
}
