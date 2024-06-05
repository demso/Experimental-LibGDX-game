package com.mygdx.game.net;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.ClientHandler;
import com.mygdx.game.gamestate.factories.ItemsFactory;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.net.messages.*;
import com.mygdx.game.net.messages.client.Begin;
import com.mygdx.game.net.messages.client.End;
import com.mygdx.game.net.messages.client.PlayerMove;
import com.mygdx.game.net.messages.client.Ready;
import com.mygdx.game.net.messages.server.*;

import java.io.IOException;
import java.util.Objects;

public class GameClient {
    Client client;
    Listener.TypeListener listener;
    public OnConnection startMessage;
    public ClientHandler handler;

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
                    SecondGDXGame.instance.readyToInit = true;
                });
        listener.addTypeHandler(PlayerJoined.class,
                (con, msg) -> {
                    if (!checkReady(msg)) return;
                    handler.playerJoined = msg;
                });
        listener.addTypeHandler(PlayerMove.class,
                (con, msg) -> {
                    if (!checkReady(msg)) return;
                    GameState.instance.players.get(msg.name).playerHandler.receivePlayerUpdate(msg);
                });
        listener.addTypeHandler(EntitiesMoves.class,
                (con, msg) -> {
                    if (!checkReady(msg)) return;
                    handler.entitiesNeedUpdate = true;
                    handler.moves = msg;
                });
        listener.addTypeHandler(PlayerEquip.class,
                (con, msg) -> {
                    if (!checkReady(msg)) return;
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
        listener.addTypeHandler(End.class,
                (con, msg) -> {
                    if (!checkReady(msg)) return;
                    if (msg.playerName == null)
                        SecondGDXGame.instance.endCause = SecondGDXGame.EndCause.SERVER_LOST;
                    else {
                        handler.playerExited(msg.playerName);
                    }

                });
        listener.addTypeHandler(SpawnEntity.class,
                (con, msg) -> {
                    if (!checkReady(msg)) return;
                    handler.spawnEntity(msg.entity);
                });
        listener.addTypeHandler(KillEntity.class,
                (con, msg) -> {
                    if (!checkReady(msg)) return;
                    handler.receivedKillEntity(msg.entityId);
                });
        listener.addTypeHandler(EntityShot.class,
                (con, msg) -> {
                    if (!checkReady(msg)) return;
                    GameState.instance.entities.get(msg.id).hurt(msg.damage);
                });
        client.addListener(listener);
    }

    public boolean checkReady(Object obj) {
        if (SecondGDXGame.instance.gameIsReady) {
            return true;
        } else {
            HandyHelper.instance.log("[Warning] [GameClient:126] " + obj.getClass().getSimpleName() + " fail, game is not ready.");
            return false;
        }
    }

    public void itemEquipped(Item item, boolean isEquipped){
        if (item != null)
            client.sendTCP(new PlayerEquip().set(item.tileName, SecondGDXGame.instance.name, SecondGDXGame.instance.name, isEquipped));
        else
            client.sendTCP(new PlayerEquip().set(null, SecondGDXGame.instance.name,  SecondGDXGame.instance.name, false));
    }

    public void sendPlayerMove(String playerName, Vector2 pos, Vector2 speed){
        client.sendTCP(new PlayerMove().set(playerName, pos.x, pos.y, speed.x,speed.y, GameState.instance.player.itemRotation));
    }


    public void hit(long id, float damage){
        client.sendTCP(new EntityShot().set(id, damage, SecondGDXGame.instance.name));
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

    public void ready(){
        client.sendTCP(new Ready());
    }

    public void end() {
        client.sendTCP(new End().set(SecondGDXGame.instance.name));
    }

    public void dispose(){
        try {
            end();
            client.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
