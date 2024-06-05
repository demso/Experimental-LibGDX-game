package com.mygdx.game.gamestate.objects.bodies.mobs.zombie;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.ZombieInfo;
import com.mygdx.game.net.messages.server.ZombieMove;
import dev.lyze.gdxUnBox2d.GameObject;

public class ServerZombieAIBehaviour extends ZombieAIBehaviour{

    public ServerZombieAIBehaviour(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void update(float delta) { }

    Vector2 moveVec = new Vector2();
    Vector2 tempVec = new Vector2();
    @Override
    public void fixedUpdate() {
        ZombieInfo curZombInf = SecondGDXGame.instance.server.entities.get(zombie.getId());
        if (curZombInf == null)
            return;
        PlayerInfo target = SecondGDXGame.instance.server.zhelper.getPlayerTarget(curZombInf);
        tempVec.set(target.x, target.y);
        moveVec.set(tempVec.x - body.getPosition().x, tempVec.y - body.getPosition().y).nor();
        body.setLinearVelocity(moveVec.scl(curZombInf.maxSpeed));
    }

    @Override
    public void serverUpdate(ZombieMove move) { }
}
