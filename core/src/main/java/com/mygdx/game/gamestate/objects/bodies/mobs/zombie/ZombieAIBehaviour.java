package com.mygdx.game.gamestate.objects.bodies.mobs.zombie;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.player.Player;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class ZombieAIBehaviour extends BehaviourAdapter {

    Zombie zombie;
    Body body;
    Vector2 tempVec = new Vector2();
    Vector2 tempVec2 = new Vector2();

    public ZombieAIBehaviour(GameObject gameObject) {
        super(gameObject);
        body = getGameObject().getBox2dBehaviour().getBody();
        zombie = (Zombie) body.getUserData();
    }


    @Override
    public void fixedUpdate() {
        Player target = getPlayerTarget(zombie);
        Vector2 pos = target.getPosition();
        tempVec.set(pos.x, pos.y);
        tempVec2.set(tempVec.x - body.getPosition().x, tempVec.y - body.getPosition().y).nor();
        tempVec2.scl(zombie.getMaxSpeed() * zombie.getBody().getMass() *  zombie.getBody().getLinearDamping() * GameState.instance.physicsStep);
        body.applyLinearImpulse(tempVec2, Vector2.Zero, true);
    }

    public Player getPlayerTarget(Zombie zombie) {
        var players = GameState.instance.players;
        Vector2 zombiePos = zombie.getPosition();
        Player firstPlayer =  players.values().iterator().next();
        for (Player secondPlayer : players.values()) {
            Vector2 pos1 = firstPlayer.getPosition();
            Vector2 pos2 = secondPlayer.getPosition();
            tempVec.set(pos1.x, pos1.y);
            tempVec2.set(pos2.x, pos2.y);
            if (tempVec2.sub(zombiePos).len2() < tempVec.sub(zombiePos).len2())
                firstPlayer = secondPlayer;
        }
        return firstPlayer;
    }
}
