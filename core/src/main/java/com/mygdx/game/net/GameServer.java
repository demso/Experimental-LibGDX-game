package com.mygdx.game.net;

import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.gamestate.tiledmap.loader.MyTiledMap;
import com.mygdx.game.gamestate.tiledmap.loader.MyTmxMapLoader;
import com.mygdx.game.net.messages.client.Begin;
import com.mygdx.game.net.messages.Message;
import com.mygdx.game.net.messages.server.OnConnection;

public class GameServer {
    Server server;
    Listener.TypeListener listener;
    MyTiledMap map;
    World world;

    public final String mapToLoad = "tiled/worldmap.tmx";
    public GameServer() {
        try {
            init();

            server = new Server();
            Registerer.register(server.getKryo());

            listener = new Listener.TypeListener();
            listener.addTypeHandler(Message.class,
                    (connection, message) -> {
                        System.out.println("Recived on server!");
                    });
            listener.addTypeHandler(Begin.class,
                    (con, msg) -> {
                        con.sendTCP(new OnConnection());
                    });

            server.addListener(listener);
            server.bind(54555,54777);
            server.start();

            //server.
        } catch (Exception ex) {

        }
    }
    void init(){
        map = new MyTmxMapLoader(world).load(mapToLoad, null);
    }
}
