package com.mygdx.game.net;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.net.messages.*;
import com.mygdx.game.net.messages.client.Begin;
import com.mygdx.game.net.messages.client.PlayerMove;
import com.mygdx.game.net.messages.server.OnConnection;
import com.mygdx.game.net.messages.server.PlayerJoined;
import com.mygdx.game.net.messages.server.PlayerMoves;

import java.io.IOException;

public class GameClient {
    GameClient instance;
    Client client;
    Listener.TypeListener listener;
    public OnConnection startMessage;

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
                    startMessage = msg;
                    SecondGDXGame.instance.readyToStart = true;
                });
        listener.addTypeHandler(PlayerJoined.class,
                (con, msg) -> {
                    GameState.instance.playerJoined = msg;
                });
        listener.addTypeHandler(PlayerMove.class,
                (con, msg) -> {
                    //if (GameState.instance.players.get())
                    GameState.instance.players.get(msg.name).playerHandler.receivePlayerUpdate(msg);
                });
        listener.addTypeHandler(PlayerMoves.class,
                (con, msg) -> {
                    //if (GameState.instance.players.get())
//                    GameState.instance.playersNeedUpdate = true;
//                    GameState.instance.moves = msg;
                });

        client.addListener(listener);
    }

//    public void updatePlayers(PlayerMoves moves){
//        for (PlayerMove move : moves.moves){
//            GameState.instance.players.get(move.name).playerHandler.receivePlayerUpdate(move);
//        }
//
//    }

    public void sendPlayerMove(String playerName, Vector2 pos, Vector2 speed){
        client.sendTCP(new PlayerMove(playerName, pos.x, pos.y, speed.x,speed.y));
    }

    public String connect(String host){
        try {
            client.start();
            client.connect(5000, host, 54555, 54777);
            client.sendTCP(new Begin(SecondGDXGame.instance.name));
        } catch (Exception ex) {
            HandyHelper.instance.log(ex.getLocalizedMessage(), false);
            ex.printStackTrace();
            return ex.getLocalizedMessage();
        }
        return null;
    }

    public void dispose(){
        try {
            client.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
