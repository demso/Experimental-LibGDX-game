package com.mygdx.game.net;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.tiledmap.loader.MyTiledMap;
import com.mygdx.game.net.messages.PlayerEquip;
import com.mygdx.game.net.messages.client.Begin;
import com.mygdx.game.net.messages.Message;
import com.mygdx.game.net.messages.client.End;
import com.mygdx.game.net.messages.client.PlayerMove;
import com.mygdx.game.net.messages.server.OnConnection;
import com.mygdx.game.net.messages.server.PlayerJoined;
import com.mygdx.game.net.messages.server.PlayerMoves;

import java.util.function.Consumer;

public class GameServer {
    Server server;
    Listener.TypeListener listener;
    MyTiledMap map;
    World world;
    volatile ObjectMap<String, PlayerInfo> players;
    Vector2 spawnPoint = new Vector2(10,87);
    Thread endlessThread;
    long sleepTime = Math.round(Globals.SERVER_UPDATE_TIME * 1000);

    public final String mapToLoad = "tiled/firstmap/worldmap.tmx";
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
                        PlayerInfo plInf = new PlayerInfo(msg.name, con).playerSet(spawnPoint.x, spawnPoint.y, 0, 0, 0);
                        players.put(msg.name, plInf);
                        con.sendTCP(new OnConnection(mapToLoad, plInf.x, plInf.y).addPlayers(players.values().toArray().toArray(PlayerInfo.class)));
                        newPlayerJoined(plInf);
                    });

            listener.addTypeHandler(PlayerMove.class,
                    (con, msg) -> {
                        players.get(msg.name).playerSet(msg.x, msg.y, msg.xSpeed, msg.ySpeed, msg.rotation);
                        //sendToAllPlayersBut(new PlayerMove(msg.name, msg.x, msg.y, msg.xSpeed, msg.ySpeed), players.get(msg.name));
                        //PlayerInfo pl = players.get(msg.name);
                       // HandyHelper.instance.log(Math.round(pl.x * 10f)/10f + " " + Math.round(pl.y*10f)/10f + " " + Math.round(pl.xSpeed*10f)/10f + " " + Math.round(pl.ySpeed*10f)/10f);
                    });

            listener.addTypeHandler(PlayerEquip.class,
                    (con, msg) -> {
                            players.get(msg.playerName).equip(msg.itemId);
                            sendToAllPlayers(msg);
                        });
            listener.addTypeHandler(End.class,
                    (con, msg) -> {
                        PlayerInfo inf = players.remove(msg.playerName);
                        sendToAllPlayersBut(msg, inf);
                    });

            server.addListener(listener);
            server.bind(54555,54777);
            server.start();

            endlessThread = new Thread(() -> {
                try {
                    update();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            endlessThread.start();
        } catch (Exception ex) {

        }
    }

    public void update() throws InterruptedException {
        while (true){
            if (players.size > 1)
                sendUpdatePlayers();
            Thread.sleep(sleepTime);
        }
    }

    PlayerMove[] playerMoves = new PlayerMove[]{};
    public void sendUpdatePlayers(){
        if (playerMoves.length != players.size){
            playerMoves = new PlayerMove[players.size];
            for (int i = 0; i < playerMoves.length; i ++){
                playerMoves[i] = new PlayerMove();
            }
        }

        players.values().forEach(new Consumer<>() {
            int i = 0;
            @Override
            public void accept(PlayerInfo playerInfo) {
                playerMoves[i].set(playerInfo.name, playerInfo.x, playerInfo.y, playerInfo.xSpeed, playerInfo.ySpeed, playerInfo.itemRotation);
                i++;
            }
        });

        sendToAllPlayers(new PlayerMoves().setMoves(playerMoves));
    }

    public void newPlayerJoined(PlayerInfo beg){
        sendToAllPlayersBut(new PlayerJoined(beg), beg);
    }

    public void sendToAllPlayers(Object obj){
        var pls = players.values().toArray();
        for (int i = 0; i < players.size; i ++) {
            PlayerInfo plInf = pls.get(i);
            plInf.connection.sendTCP(obj);
        }
//        for (PlayerInfo plInf : players.values()){
//            plInf.connection.sendTCP(obj);
//        }
    }
    public void sendToAllPlayersBut(Object obj, PlayerInfo... playerInfos){
        for (PlayerInfo plInf : players.values()){
            for (PlayerInfo plInf2 : playerInfos)
                if (plInf != plInf2)
                    plInf.connection.sendTCP(obj);
        }
    }

    void init(){
        players = new ObjectMap<>();
    }

    public void dispose(){
        try {
            sendToAllPlayers(new End());
            server.dispose();
            endlessThread.interrupt();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
