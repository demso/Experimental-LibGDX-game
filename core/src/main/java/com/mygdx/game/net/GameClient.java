package com.mygdx.game.net;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.factories.ItemsFactory;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.net.messages.*;
import com.mygdx.game.net.messages.client.Begin;
import com.mygdx.game.net.messages.client.PlayerMove;
import com.mygdx.game.net.messages.server.OnConnection;
import com.mygdx.game.net.messages.server.PlayerJoined;
import com.mygdx.game.net.messages.server.PlayerMoves;

import java.io.IOException;
import java.util.Objects;

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
                        GameState.instance.playersNeedUpdate = true;
                        GameState.instance.moves = msg;
                });
        listener.addTypeHandler(PlayerEquip.class,
                (con, msg) -> {
                    if (msg.itemId == null || !msg.isEquipped)
                        if (msg.playerName.equals(SecondGDXGame.instance.name)) {
                            if (!Objects.equals(msg.senderName, SecondGDXGame.instance.name)) {
                                GameState.instance.player.uneqipItem();
                            }
                        } else {
                            GameState.instance.players.get(msg.playerName).uneqipItem();
                        }
                    else
                        if (msg.playerName.equals(SecondGDXGame.instance.name)) {
                            if (!Objects.equals(msg.senderName, SecondGDXGame.instance.name)) {
                                GameState.instance.player.equipItem(ItemsFactory.getItem(msg.itemId));
                            }
                        } else {
                            GameState.instance.players.get(msg.playerName).equipItem(ItemsFactory.getItem(msg.itemId));
                        }
                });

        client.addListener(listener);
    }

//    public void updatePlayers(PlayerMoves moves){
//        for (PlayerMove move : moves.moves){
//            GameState.instance.players.get(move.name).playerHandler.receivePlayerUpdate(move);
//        }
//
//    }

    public void itemEquipped(Item item, boolean isEquipped){
        if (item != null)
            client.sendTCP(new PlayerEquip().set(item.tileName, SecondGDXGame.instance.name, SecondGDXGame.instance.name, isEquipped));
        else
            client.sendTCP(new PlayerEquip().set(null, SecondGDXGame.instance.name,  SecondGDXGame.instance.name, false));
    }

    public void sendPlayerMove(String playerName, Vector2 pos, Vector2 speed){
        client.sendTCP(new PlayerMove().set(playerName, pos.x, pos.y, speed.x,speed.y, GameState.instance.player.itemRotation));
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
