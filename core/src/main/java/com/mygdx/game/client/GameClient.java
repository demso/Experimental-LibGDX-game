package com.mygdx.game.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.server.Message;

public class GameClient {
    Client client;
    Listener.TypeListener listener;

    public GameClient(){
        try {
            client = new Client();
            client.getKryo().register(Message.class);

            listener = new Listener.TypeListener();
            listener.addTypeHandler(Message.class,
                    (con, msg) -> {
                        System.out.println("Recived on client!");
                    });

            client.addListener(listener);
            client.start();
            client.connect(5000, "127.0.0.1", 54555, 54777);
        } catch (Exception ex) {

        }
    }
}
