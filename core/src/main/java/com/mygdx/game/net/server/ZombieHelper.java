package com.mygdx.game.net.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.net.GameServer;
import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.ZombieInfo;
import com.mygdx.game.net.messages.server.ZombieMove;

public class ZombieHelper {
    GameServer server;
    Vector2 tempVec = new Vector2();
    Vector2 tempVec2 = new Vector2();
    Vector2 tempVec3 = new Vector2();
    public ZombieHelper(GameServer serv) {
        server = serv;
    }
//    public PlayerInfo getPlayerTarget(ZombieInfo zinfo) {
//        var players = SecondGDXGame.instance.server.players;
//        tempVec3.set(zinfo.x, zinfo.y);
//        PlayerInfo plinf =  new Array.ArrayIterator<>(players.values().toArray()).next();
//        for (PlayerInfo info :  new Array.ArrayIterator<>(players.values().toArray())) {
//            tempVec.set(plinf.x, plinf.y);
//            tempVec2.set(info.x, info.y);
//            if (tempVec2.sub(tempVec3).len2() < tempVec.sub(tempVec3).len2())
//                plinf = info;
//        }
//        return plinf;
//    }

    public PlayerInfo getPlayerTarget(Zombie zombie) {
        var players = SecondGDXGame.instance.server.players;
        Vector2 pos = zombie.getPosition();
        tempVec3.set(pos.x, pos.y);
        PlayerInfo plinf =  new Array.ArrayIterator<>(players.values().toArray()).next();
        for (PlayerInfo info :  new Array.ArrayIterator<>(players.values().toArray())) {
            tempVec.set(plinf.x, plinf.y);
            tempVec2.set(info.x, info.y);
            if (tempVec2.sub(tempVec3).len2() < tempVec.sub(tempVec3).len2())
                plinf = info;
        }
        return plinf;
    }

//    transient Vector2 tempVec4 = new Vector2();
//
//    public ZombieMove getMove(ZombieInfo zinfo) {
//        PlayerInfo plInf = getPlayerTarget(zinfo);
//        tempVec4.set(Math.signum(plInf.x - zinfo.x), Math.signum(plInf.y - zinfo.y)).nor().scl(zinfo.maxSpeed);
//        zinfo.xSpeed = tempVec4.x;
//        zinfo.ySpeed = tempVec4.y;
//        return new ZombieMove().set(zinfo.id, getPlayerTarget(zinfo).id, zinfo.x, zinfo.y, zinfo.xSpeed, zinfo.ySpeed);
//    }
//
//
//    public void fillMove(ZombieInfo zinfo, ZombieMove move) {
//        move.set(zinfo.id, zinfo.targetId, zinfo.x, zinfo.y, zinfo.xSpeed, zinfo.ySpeed);
//    }
}
