package com.mygdx.game.gamestate.objects.bodies.mobs.zombie;

import com.badlogic.gdx.physics.box2d.Contact;
import com.mygdx.game.gamestate.objects.CollisionBehaviour;
import com.mygdx.game.gamestate.objects.bodies.player.Player;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class ZombieCollisionBehaviour extends CollisionBehaviour<Zombie> {

    public ZombieCollisionBehaviour(GameObject gameObject) {
        super(gameObject);
    }

    Player player;

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
        preCol(contact);
        if (otherUserData instanceof Player curPlayer && !otherFixture.isSensor()){
            player = curPlayer;
            accumulator = 1;
        }
    }

    @Override
    public void onCollisionExit(Behaviour other, Contact contact) {
        preCol(contact);
        if (otherUserData instanceof Player curPlayer && !otherFixture.isSensor()){
            player = null;
            accumulator = 1;
        }
    }

    @Override
    public void fixedUpdate() {
        float attackCoolDown = data.getAttackCoolDown();
        if (attackCoolDown > 0){
            attackCoolDown = (Math.max(0, attackCoolDown - getUnBox().getOptions().getTimeStep()));
            data.setAttackCoolDown(attackCoolDown);
        }
        if(attackCoolDown <= 0 && player != null && player.isAlive()){
            data.attack(player);
        }
    }

    float accumulator = 1;

    @Override
    public void update(float delta) {

    }
}
