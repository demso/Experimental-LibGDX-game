package com.mygdx.game.net.messages.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.factories.MobsFactory;
import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.EntityInfo;

public class ZombieInfo extends EntityInfo {
    float maxSpeed = 10f;
    Vector2 tempVec4 = new Vector2();
    @Override
    public ZombieMove getMove() {
        PlayerInfo plInf = getPlayerTarget();
        tempVec4.set(Math.signum(plInf.x - x), Math.signum(plInf.y - y)).nor().scl(maxSpeed);
        xSpeed = tempVec4.x;
        ySpeed = tempVec4.y;
        return new ZombieMove().set(id, getPlayerTarget().name, x, y, xSpeed, ySpeed);
    }


    public ZombieMove setMove(ZombieMove move) {
        move.set(id, getPlayerTarget().name, x, y, xSpeed, ySpeed);
        return move;
    }

    Vector2 tempVec = new Vector2();
    Vector2 tempVec2 = new Vector2();
    Vector2 tempVec3 = new Vector2();
    public PlayerInfo getPlayerTarget() {
        var players = SecondGDXGame.instance.server.players;
        tempVec3.set(x, y);
        PlayerInfo plinf =  new Array.ArrayIterator<>(players.values().toArray()).next();
        for (PlayerInfo info :  new Array.ArrayIterator<>(players.values().toArray())) {
            tempVec.set(plinf.x, plinf.y);
            tempVec2.set(info.x, info.y);
            if (tempVec2.sub(tempVec3).len2() < tempVec.sub(tempVec3).len2())
                plinf = info;
        }
        return plinf;
    }

    public ZombieInfo set(long id, MobsFactory.Type type, String name, float hp, float x, float y, float xS, float yS){
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        this.id = id;
        this.type = type;
        this.name = name;
        this.hp = hp;
        return this;
    }
}
