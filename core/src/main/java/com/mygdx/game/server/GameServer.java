package com.mygdx.game.server;

import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class GameServer {
    Server server;
    Listener.TypeListener listener;
    public GameServer() {
        try {
            server = new Server();
            server.getKryo().register(Message.class);

            listener = new Listener.TypeListener();
            listener.addTypeHandler(Message.class,
                    (connection, message) -> {
                        System.out.println("Recived on server!");
                    });

            server.addListener(listener);
            server.bind(54555,54777);
            server.start();
        } catch (Exception ex) {

        }
    }
}
