package com.mygdx.game.net;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.AcceptHandler;
import com.mygdx.game.gamestate.factories.ItemsFactory;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.net.messages.*;
import com.mygdx.game.net.messages.client.*;
import com.mygdx.game.net.messages.server.*;

import java.io.IOException;
import java.util.Objects;

public class GameClient {
    Client client;
    Listener.TypeListener listener;
    public OnConnection startMessage;
    public AcceptHandler handler;

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
                    GameState.instance.players.get(msg.playerId).playerHandler.receivePlayerUpdate(msg);
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
                        if (msg.playerId == (GameState.instance.player.getId())) {
                            if (!Objects.equals(msg.senderName, SecondGDXGame.instance.name)) {
                                GameState.instance.player.uneqipItem();
                            }
                        } else {
                            GameState.instance.players.get(msg.playerId).uneqipItem();
                        }
                    else
                        if (msg.playerId == (GameState.instance.player.getId())) {
                            if (!Objects.equals(msg.senderName, SecondGDXGame.instance.name)) {
                                GameState.instance.player.equipItem(ItemsFactory.getItem(msg.itemId));
                            }
                        } else {
                            GameState.instance.players.get(msg.playerId).equipItem(ItemsFactory.getItem(msg.itemId));
                        }
                });
        listener.addTypeHandler(End.class,
                (con, msg) -> {
                    if (!checkReady(msg)) return;
                    if (msg.playerId < 0)
                        SecondGDXGame.instance.endCause = SecondGDXGame.EndCause.SERVER_LOST;
                    else {
                        handler.playerExited(msg.playerId);
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
        listener.addTypeHandler(GunFire.class,
                (con, msg) -> {
                    if (!checkReady(msg)) return;
                    handler.gunFire(msg);
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
            client.sendTCP(new PlayerEquip().set(item.tileName, GameState.instance.player.getId(), SecondGDXGame.instance.name, isEquipped));
        else
            client.sendTCP(new PlayerEquip().set(null, GameState.instance.player.getId(),  SecondGDXGame.instance.name, false));
    }

    public void sendPlayerMove(long playerId, Vector2 pos, Vector2 speed){
        client.sendTCP(new PlayerMove().set(playerId, pos.x, pos.y, speed.x,speed.y, GameState.instance.player.itemRotation));
    }

    public void hit(long id, float damage){
        client.sendTCP(new EntityShot().set(id, damage, GameState.instance.player.getId()));
    }

    public void gunFire(){
        client.sendTCP(new GunFire().set(GameState.instance.player.getId()));
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
        client.sendTCP(new End().set(GameState.instance.player.getId()));
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
