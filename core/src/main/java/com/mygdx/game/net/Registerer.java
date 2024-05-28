package com.mygdx.game.net;

import com.esotericsoftware.kryo.Kryo;
import com.mygdx.game.net.messages.*;
import com.mygdx.game.net.messages.client.Begin;
import com.mygdx.game.net.messages.client.End;
import com.mygdx.game.net.messages.client.PlayerMove;
import com.mygdx.game.net.messages.server.OnConnection;
import com.mygdx.game.net.messages.server.PlayerJoined;
import com.mygdx.game.net.messages.server.PlayerMoves;

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
        kryo.register(PlayerMoves.class);
        kryo.register(PlayerMove[].class);
    }
}
