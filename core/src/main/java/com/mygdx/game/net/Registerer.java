package com.mygdx.game.net;

import com.esotericsoftware.kryo.Kryo;
import com.mygdx.game.net.messages.*;
import com.mygdx.game.net.messages.client.Begin;
import com.mygdx.game.net.messages.client.End;
import com.mygdx.game.net.messages.server.OnConnection;

public class Registerer {
    public static void register(Kryo kryo){
        kryo.register(Message.class);
        kryo.register(Registerer.class);
        kryo.register(Begin.class);
        kryo.register(End.class);
        kryo.register(OnConnection.class);
    }
}
