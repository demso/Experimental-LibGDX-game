package com.mygdx.game.net;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.mygdx.game.gamestate.factories.MobsFactory;
import com.mygdx.game.net.messages.*;
import com.mygdx.game.net.messages.client.Begin;
import com.mygdx.game.net.messages.client.End;
import com.mygdx.game.net.messages.client.PlayerMove;
import com.mygdx.game.net.messages.client.Ready;
import com.mygdx.game.net.messages.server.*;

public class Registerer {
    public static void register(Kryo kryo){
        kryo.register(Message.class);
        kryo.register(Registerer.class);
        kryo.register(Begin.class);
        kryo.register(End.class);
        kryo.register(OnConnection.class);
        kryo.register(PlayerMove.class);
        kryo.register(PlayerJoined.class);
        kryo.register(PlayerInfo.class);
        kryo.register(PlayerInfo[].class);
        kryo.register(EntitiesMoves.class);
        kryo.register(PlayerMove[].class);
        kryo.register(ItemInfo.class);
        kryo.register(PlayerEquip.class);
        kryo.register(SpawnEntity.class);
        kryo.register(KillEntity.class);
        kryo.register(EntityInfo.class);
        kryo.register(MobsFactory.Type.class);
        kryo.register(Ready.class);
        kryo.register(ZombieInfo.class);
        kryo.register(ZombieMove.class);
        kryo.register(ZombieMove[].class);
        kryo.register(EntitiesMove.class);
        kryo.register(Vector2.class);
        //kryo.register();
    }
}
