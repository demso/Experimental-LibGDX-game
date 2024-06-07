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
import com.mygdx.game.net.messages.client.*;
import com.mygdx.game.net.messages.common.Message;
import com.mygdx.game.net.messages.common.tileupdate.CloseTile;
import com.mygdx.game.net.messages.common.tileupdate.OpenTile;
import com.mygdx.game.net.messages.server.*;

import java.io.IOException;
import java.util.Objects;

public class GameClient {
    Client client;
    Listener.TypeListener listener;
    public OnConnection startMessage;
    public AcceptHandler handler;
    public GameState gameState;

    public GameClient(){
        client = new Client();
        Registerer.register(client.getKryo());
        listener = new Listener.TypeListener();
        client.addListener(listener);

        listener.addTypeHandler(Message.class, (con, msg) -> {
                    HandyHelper.instance.log("[GameClient] Message from server:  " + msg.message);
                });
        listener.addTypeHandler(OnConnection.class, (con, msg) -> {
                    startMessage = msg;
                    SecondGDXGame.instance.readyToInit = true;
                });
        listener.addTypeHandler(PlayerJoined.class, (con, msg) -> {
                    if (!checkReady(msg)) return;
                    handler.playerJoined = msg;
                });
        listener.addTypeHandler(PlayerMove.class, (con, msg) -> {
                    if (!checkReady(msg)) return;
                    GameState.instance.players.get(msg.playerId).playerHandler.receivePlayerUpdate(msg);
                });
        listener.addTypeHandler(EntitiesMoves.class, (con, msg) -> {
                    if (!checkReady(msg)) return;
                    handler.entitiesNeedUpdate = true;
                    handler.moves = msg;
                });
        listener.addTypeHandler(PlayerEquip.class, (con, msg) -> {
                    if (!checkReady(msg)) return;
                    if (msg.itemId == null || !msg.isEquipped)
                        if (msg.playerId == (GameState.instance.clientPlayer.getId())) {
                            if (!Objects.equals(msg.senderName, SecondGDXGame.instance.name)) {
                                GameState.instance.clientPlayer.uneqipItem();
                            }
                        } else {
                            GameState.instance.players.get(msg.playerId).uneqipItem();
                        }
                    else
                        if (msg.playerId == (GameState.instance.clientPlayer.getId())) {
                            if (!Objects.equals(msg.senderName, SecondGDXGame.instance.name)) {
                                GameState.instance.clientPlayer.equipItem(ItemsFactory.getItem(msg.itemId));
                            }
                        } else {
                            GameState.instance.players.get(msg.playerId).equipItem(ItemsFactory.getItem(msg.itemId));
                        }
                });
        listener.addTypeHandler(End.class, (con, msg) -> {
                    if (!checkReady(msg)) return;
                    if (msg.playerId < 0)
                        SecondGDXGame.instance.endCause = SecondGDXGame.EndCause.SERVER_LOST;
                    else {
                        handler.playerExited(msg.playerId);
                    }

                });
        listener.addTypeHandler(SpawnEntity.class, (con, msg) -> {
                    if (!checkReady(msg)) return;
                    handler.spawnEntity(msg.entity);
                });
        listener.addTypeHandler(KillEntity.class, (con, msg) -> {
                    if (!checkReady(msg)) return;
                    handler.receivedKillEntity(msg.entityId);
                });
        listener.addTypeHandler(EntityShot.class, (con, msg) -> {
                    if (!checkReady(msg)) return;
                    GameState.instance.entities.get(msg.id).hurt(msg.damage);
                });
        listener.addTypeHandler(GunFire.class, (con, msg) -> {
                    if (!checkReady(msg)) return;
                    handler.gunFire(msg);
                });
        listener.addTypeHandler(OpenTile.class, (con, msg) -> {
                    if (!checkReady(msg)) return;
                    handler.updateTile(msg);
                });
        listener.addTypeHandler(CloseTile.class, (con, msg) -> {
                    if (!checkReady(msg)) return;
                    handler.updateTile(msg);
                });
        listener.addTypeHandler(StoredItems.class, (con, msg) -> {
            if (!checkReady(msg)) return;

            Vector2 pos = gameState.hud.storageInventoryHUD.getStorage().getPosition();
            if (msg.x != (int)Math.floor(pos.x) || msg.y != (int) Math.floor(pos.y))
                return;

            Item[] items = new Item[msg.items.length];
            for (int i = 0; i < msg.items.length; i++) {
                items[i] = ItemsFactory.getItem(msg.items[i].itemId);
            }

            gameState.hud.onStoredItemsReceived(msg.x, msg.y, items);

//            gameState.hud.storageInventoryHUD.getStorage().setInventoryItems(items);
//            gameState.hud.storageInventoryHUD.refill();
        });
    }

    public boolean checkReady(Object obj) {
        if (SecondGDXGame.instance.gameIsReady) {
            return true;
        } else {
            HandyHelper.instance.log("[Warning] [GameClient:126] " + obj.getClass().getSimpleName() + " fail, game is not ready.");
            return false;
        }
    }

    public void onItemEquipped(Item item, boolean isEquipped){
        if (item != null)
            client.sendTCP(new PlayerEquip().set(item.itemId, GameState.instance.clientPlayer.getId(), SecondGDXGame.instance.name, isEquipped));
        else
            client.sendTCP(new PlayerEquip().set(null, GameState.instance.clientPlayer.getId(),  SecondGDXGame.instance.name, false));
    }

    public void sendPlayerMove(long playerId, Vector2 pos, Vector2 speed){
        client.sendTCP(new PlayerMove().set(playerId, pos.x, pos.y, speed.x,speed.y, GameState.instance.clientPlayer.itemRotation));
    }

    public void onHit(long id, float damage){
        client.sendTCP(new EntityShot().set(id, damage, GameState.instance.clientPlayer.getId()));
    }

    public void onGunFire(){
        client.sendTCP(new GunFire().set(GameState.instance.clientPlayer.getId()));
    }

    public void onTileOpen(int x, int y){
        client.sendTCP(new OpenTile().set(gameState.clientPlayer.getId(), x, y));
    }

    public void onTileClose(int x, int y){
        client.sendTCP(new CloseTile().set(gameState.clientPlayer.getId(), x, y));
    }

//    public void getStoredItems(float x, float y){
//        client.sendTCP(new GetStoredItems().set((int) Math.floor(x), (int) Math.floor(y)));
//    }

    public void needsStorageUpdate(float x, float y){
        client.sendTCP(new NeedsStorageUpdate().set(GameState.instance.clientPlayer.getId(), (int) Math.floor(x), (int) Math.floor(y)));
    }

    public void stopStorageUpdate(float x, float y){
        client.sendTCP(new StopStorageUpdate().set(GameState.instance.clientPlayer.getId(), (int) Math.floor(x), (int) Math.floor(y)));
    }

    public String connect(String host){
        try {
            client.start();
            client.connect(5000, host, 54555, 54777);
            client.sendTCP(new Begin(SecondGDXGame.instance.name));
        } catch (Exception ex) {
            HandyHelper.instance.log(ex.getLocalizedMessage());
            ex.printStackTrace();
            return ex.getLocalizedMessage();
        }
        return null;
    }

    public void ready(GameState state){
        gameState = state;
        client.sendTCP(new Ready());
    }

    public void end() {
        client.sendTCP(new End().set(GameState.instance.clientPlayer.getId()));
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
