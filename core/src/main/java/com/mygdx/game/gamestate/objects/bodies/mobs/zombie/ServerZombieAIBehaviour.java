package com.mygdx.game.gamestate.objects.bodies.mobs.zombie;

import com.badlogic.gdx.physics.box2d.Body;
import dev.lyze.gdxUnBox2d.GameObject;

public class ServerZombieAIBehaviour extends ZombieAIBehaviour{
    Zombie zombie;
    Body body;

    @Override
    public void start() {
        body = getGameObject().getBox2dBehaviour().getBody();
        zombie = (Zombie) body.getUserData();
    }

    public ServerZombieAIBehaviour(GameObject gameObject) {
        super(gameObject);
    }

//    @Override
//    public void update(float delta) { }
//
//    Vector2 tempVec = new Vector2();
//    Vector2 tempVec2 = new Vector2();
//    @Override
//    public void fixedUpdate() {
//        Player target = getPlayerTarget(zombie);
//        Vector2 pos = target.getPosition();
//        tempVec.set(pos.x, pos.y);
//        velocity.set(tempVec.x - body.getPosition().x, tempVec.y - body.getPosition().y).nor();
//        velocity.scl(zombie.getMaxSpeed() * zombie.getBody().getMass() *  zombie.getBody().getLinearDamping() * ServerGameState.instance.physicsStep);
//        body.applyLinearImpulse(velocity, Vector2.Zero, true);
//    }
//
//    public Player getPlayerTarget(Zombie zombie) {
//        var players = GameState.instance.players;
//        Vector2 zombiePos = zombie.getPosition();
//        Player firstPlayer =  players.values().iterator().next();
//        for (Player secondPlayer : players.values()) {
//            Vector2 pos1 = firstPlayer.getPosition();
//            Vector2 pos2 = secondPlayer.getPosition();
//            tempVec.set(pos1.x, pos1.y);
//            tempVec2.set(pos2.x, pos2.y);
//            if (tempVec2.sub(zombiePos).len2() < tempVec.sub(zombiePos).len2())
//                firstPlayer = secondPlayer;
//        }
//        return firstPlayer;
//    }
}
