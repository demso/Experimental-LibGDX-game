package com.mygdx.game.net;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.net.messages.*;
import com.mygdx.game.net.messages.client.Begin;
import com.mygdx.game.net.messages.server.OnConnection;

public class GameClient {
    GameClient instance;
    Client client;
    Listener.TypeListener listener;

    public GameClient(){
        client = new Client();
        Registerer.register(client.getKryo());

        listener = new Listener.TypeListener();
        listener.addTypeHandler(Message.class,
                (con, msg) -> {
                    System.out.println("Recived on client!");
                });
        listener.addTypeHandler(OnConnection.class,
                (con, msg) -> {
                    System.out.println("Recived on client!");
                });

        client.addListener(listener);
    }

    public boolean connect(String host){
        try {
            client.start();
            client.connect(5000, host, 54555, 54777);
            client.sendTCP(new Begin());
        } catch (Exception ex) {
            HandyHelper.instance.log(ex.getLocalizedMessage(), false);
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
