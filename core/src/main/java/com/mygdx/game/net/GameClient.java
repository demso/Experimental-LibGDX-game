package com.mygdx.game.net;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.ObjectSet;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.AcceptHandler;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.items.grenade.Grenade;
import com.mygdx.game.gamestate.objects.items.grenade.GrenadeHandler;
import com.mygdx.game.net.messages.client.*;
import com.mygdx.game.net.messages.common.*;
import com.mygdx.game.net.messages.common.tileupdate.CloseTile;
import com.mygdx.game.net.messages.common.tileupdate.OpenTile;
import com.mygdx.game.net.messages.server.*;

import java.io.IOException;

public class GameClient {
    Client client;
    Listener.TypeListener listener;
    public OnConnection startMessage;
    public AcceptHandler handler;
    public GameState gameState;
    public ObjectSet<Grenade> localGrenades = new ObjectSet<>();

    public GameClient(){
        client = new Client(32768, 32768);
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
                    if (msg.equippedItem == null || !msg.isEquipped)
                        if (msg.playerId == (gameState.clientPlayer.getId())) {
                            gameState.clientPlayer.uneqipItem().dispose();
                        } else {
                            gameState.players.get(msg.playerId).uneqipItem();
                        }
                    else {
                        Item item = gameState.items.get(msg.equippedItem.uid);
                        if (item == null) {
                            item = gameState.itemsFactory.getItem(msg.equippedItem.uid, msg.equippedItem.itemId);
                        }

                        if (msg.playerId == (gameState.clientPlayer.getId())) {
                            gameState.clientPlayer.equipItem(item);
                        } else {
                            gameState.players.get(msg.playerId).equipItem(item);
                        }
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
        listener.addTypeHandler(EntityHurt.class, (con, msg) -> {
                    if (!checkReady(msg)) return;
                    Entity entity = GameState.instance.entities.get(msg.id);
                    if (entity != null) {
                        entity.hurt(msg.damage);
                        return;
                    }
                    entity = GameState.instance.players.get(msg.id);
                    if (entity != null) {
                        entity.hurt(msg.damage);
                        return;
                    }
                    if (msg.id == GameState.instance.clientPlayer.getId()) {
                        GameState.instance.clientPlayer.hurt(msg.damage);
                    }
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
                items[i] = gameState.itemsFactory.getItem(msg.items[i].uid, msg.items[i].itemId);
            }

            gameState.hud.onStoredItemsReceived(msg.x, msg.y, items);
        });
        listener.addTypeHandler(AllocateItem.class, (con, msg) -> {
            Item item = gameState.items.get(msg.itemInfo.uid);
            if (item == null){
                item = gameState.itemsFactory.getItem(msg.itemInfo);
            }
            item.allocate(new Vector2(msg.x, msg.y));
        });
        listener.addTypeHandler(RemoveItemFromWorld.class, (con, msg) -> {
            Item item = gameState.items.get(msg.uid);
            if (item != null)
                item.dispose();
        });
        listener.addTypeHandler(GrenadeInfo.class, (con, msg) -> {
            Grenade item = (Grenade) gameState.items.get(msg.uid);
            if (item == null){
                item = (Grenade) gameState.itemsFactory.getItem(msg.uid, msg.tileName);
                item.fire(Math.round(msg.timeToDetonation * 1000), false);
            } else if ( item.getGameObject() != null){
                GrenadeHandler handler = item.getGameObject().getBehaviour(GrenadeHandler.class);
                if (handler != null)
                    handler.requestUpdate(msg);
            }
        });
        listener.addTypeHandler(DisposeItem.class, (con, msg) -> {
            Item item = gameState.items.get(msg.uid);
            if (item != null)
                item.dispose();
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
            client.sendTCP(new PlayerEquip().set(ItemInfo.createItemInfo(item), GameState.instance.clientPlayer.getId(), isEquipped));
        else
            client.sendTCP(new PlayerEquip().set(null, GameState.instance.clientPlayer.getId(), false));
    }

    public void sendPlayerMove(long playerId, Vector2 pos, Vector2 speed){
        client.sendTCP(new PlayerMove().set(playerId, pos.x, pos.y, speed.x,speed.y, GameState.instance.clientPlayer.itemRotation));
    }

    public void onHit(long id, float damage){
        client.sendTCP(new EntityHurt().set(id, damage, GameState.instance.clientPlayer.getId()));
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

    public void tookItemFromStorage(Item item, int fromX, int fromY){
        client.sendTCP(new MoveItemFromStorageToStorage().set(item.uid, fromX, fromY, GameState.instance.clientPlayer.getId()));
    }

    public void storedItem(Item item, int toX, int toY){
        client.sendTCP(new MoveItemFromStorageToStorage().set(item.uid, GameState.instance.clientPlayer.getId(), toX, toY));
    }

    public void droppedItem(Item item, Vector2 pos){
        client.sendTCP(new DropItems().set(gameState.clientPlayer.getId(), pos.x, pos.y, item.uid));
        //client.sendTCP(new AllocateItem().set(new ItemInfo().set(item.uid, item.itemId), pos.x, pos.y));
    }

    public void pickedUpItem(Item item){
        client.sendTCP(new TakeItems().set(gameState.clientPlayer.getId(), ItemInfo.createItemInfo(item)));
        client.sendTCP(new RemoveItemFromWorld().set(item.uid));
    }

    public void onGrenadeThrown(Grenade gr){
        localGrenades.add(gr);
    }

    public void disposeItem(Item item){
        client.sendTCP(new DisposeItem().set(item.uid));
    }

    public void entityHurt(Entity entity, float damage){
        client.sendTCP(new EntityHurt().set(entity.getId(), damage, GameState.instance.clientPlayer.getId()));
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


    float updateTimeAccumulator = 0;
    float updatePeriod = (Globals.SERVER_UPDATE_TIME);
    public void update(float delta){
        if (localGrenades.size > 0) {
            updateTimeAccumulator += delta;
            if (updateTimeAccumulator >= updatePeriod) {
                for (Grenade gr : localGrenades) {
                    Body body = gr.getPhysicalBody();
                    Vector2 vel = body.getLinearVelocity();
                    client.sendTCP(new GrenadeInfo().set(gr.uid, gr.stringID, gr.timeToDetonation, gr.getPosition().x, gr.getPosition().y, vel.x, vel.y, body.getTransform().getRotation()));
                }
                updateTimeAccumulator = 0;
            }
        }
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
