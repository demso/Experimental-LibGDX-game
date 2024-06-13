package com.mygdx.game.gamestate.objects.bodies.mobs.zombie;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.Utils;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.UI.console.sjconsole.LogLevel;
import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.server.ZombieMove;
import com.mygdx.game.net.server.ServerGameState;
import dev.lyze.gdxUnBox2d.GameObject;

public class ServerZombieAIBehaviour extends ZombieAIBehaviour{

    @Override
    public void start() {

    }

    public ServerZombieAIBehaviour(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void update(float delta) { }

    Vector2 moveVec = new Vector2();
    Vector2 tempVec = new Vector2();
    @Override
    public void fixedUpdate() {
        //HandyHelper.instance.log("[ZombiAIServ] Speed: " + Utils.round(body.getLinearVelocity().len(), 1));
        PlayerInfo target = SecondGDXGame.instance.server.zhelper.getPlayerTarget(zombie);
        tempVec.set(target.x, target.y);
        velocity.set(tempVec.x - body.getPosition().x, tempVec.y - body.getPosition().y).nor();
        //body.setLinearVelocity(moveVec.scl(zombie.getMaxSpeed()));

        //movingVec.scl(speed * mass * damping * physStep)
        //movingImpulse = movingVector.scl(player.currentSpeedMultiplier * player.normalSpeed * player.getBody().getMass() *  player.getBody().getLinearDamping() * GameState.instance.physicsStep);
        velocity.scl(zombie.getMaxSpeed() * zombie.getBody().getMass() *  zombie.getBody().getLinearDamping() * ServerGameState.instance.physicsStep);
        body.applyLinearImpulse(velocity, Vector2.Zero, true);
    }

    @Override
    public void serverUpdate(ZombieMove move) { }
}
