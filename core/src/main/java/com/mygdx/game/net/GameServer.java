package com.mygdx.game.net;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.items.grenade.Grenade;
import com.mygdx.game.gamestate.objects.items.grenade.GrenadeHandler;
import com.mygdx.game.gamestate.objects.tiles.Storage;
import com.mygdx.game.gamestate.tiledmap.tiled.TiledMapTileLayer;
import com.mygdx.game.net.messages.client.*;
import com.mygdx.game.net.messages.common.*;
import com.mygdx.game.net.messages.common.tileupdate.CloseTile;
import com.mygdx.game.net.messages.common.tileupdate.OpenTile;
import com.mygdx.game.net.messages.server.*;
import com.mygdx.game.net.server.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class GameServer {
    com.esotericsoftware.kryonet.Server server;
    Listener.TypeListener listener;
    public volatile Map<Long, PlayerInfo> players;
    public volatile Map<Long, Entity> entities;//equals to gamestate.entities
    public volatile Map<Long, Item> items;
    public volatile AtomicLong entitiesCounter = new AtomicLong(1);//ONLY GET AND INCRIMENT
    public volatile AtomicLong itemsCounter = new AtomicLong(1);
    Vector2 spawnPoint = new Vector2(25,87);
    Thread endlessThread;
    long sleepTime = Math.round(Globals.SERVER_UPDATE_TIME * 1000);
    public ServerGameState gameState;
    public ZombieHelper zhelper = new ZombieHelper(this);
    public ServHandler handler;
    ObjectMap<Storage, Array<Long>> storageListeners = new ObjectMap<>(); //player id -> storage
    PlayerInfo defaultPlayer;

    public final String mapToLoad = "tiled/firstmap/worldmap.tmx";
    public GameServer() {
        try {
            init();
            server = new com.esotericsoftware.kryonet.Server();
            Registerer.register(server.getKryo());
            listener = new Listener.TypeListener();
            server.addListener(listener);

            defaultPlayer = new PlayerInfo("default", null).playerSet(spawnPoint.x, spawnPoint.y, 0, 0, 0);
            defaultPlayer.hp = Globals.PLAYER_HEALTH;
            defaultPlayer.maxHp = defaultPlayer.hp;

            //Log.TRACE();
//            Log.setLogger(new CustomKryoLogger());
            listener.addTypeHandler(Message.class, (connection, message) -> {
                        System.out.println("Recived on server!");
                    });
            listener.addTypeHandler(Begin.class, (con, msg) -> {
                        PlayerInfo plInf = new PlayerInfo(msg.name, con);
                        plInf.id = entitiesCounter.getAndIncrement();
                        plInf.x = defaultPlayer.x;
                        plInf.y = defaultPlayer.y;
                        plInf.hp = defaultPlayer.hp;
                        plInf.maxHp = defaultPlayer.maxHp;
                        players.put(plInf.id, plInf);
                        con.sendTCP(new OnConnection(plInf, mapToLoad)
                                .addPlayers(players.values().toArray(new PlayerInfo[0]))
                                .addEntities(entities.values().toArray(new Entity[0])));
                        sendToAllPlayersBut(new PlayerJoined(plInf), plInf);
                    });
            listener.addTypeHandler(PlayerMove.class, (con, msg) -> {
                        players.get(msg.playerId).playerSet(msg.x, msg.y, msg.xSpeed, msg.ySpeed, msg.rotation);
                      });
            listener.addTypeHandler(PlayerEquip.class, (con, msg) -> {
                            players.get(msg.playerId).equip(msg.equippedItem);
                            sendToAllPlayersBut(msg, players.get(msg.playerId));
                        });
            listener.addTypeHandler(End.class, (con, msg) -> {
                        PlayerInfo inf = players.remove(msg.playerId);
                        sendToAllPlayersBut(msg, inf);
                    });
            listener.addTypeHandler(Ready.class, (con, msg) -> {
                        startWave();
                        //server.sendToTCP(con.getID(), new PlayerEquip().set(ItemInfo.createItemInfo(gameState.itemsFactory.getItem(itemsCounter.incrementAndGet(), "deagle_44"), msg.playerId, null, true)));
                    });
            listener.addTypeHandler(EntityShot.class, (con, msg) -> {
                        Entity zomb = entities.get(msg.id);
                        if (zomb == null || !zomb.isAlive()){
                            HandyHelper.instance.log("[GameServer:93] Entity is unreachable on server, is null = " + (zomb == null)
                                    + ", hp = " + (zomb == null ? "" : zomb.getHp()));
                            return;
                        }
                        if (zomb instanceof Zombie zombie) {
                            zombie.hurt(msg.damage);
                            if (!zombie.isAlive()) {
                                killEntity(msg.id);
                                sendToAllPlayers(new KillEntity().set(msg.id));
                            } else
                                server.sendToAllExceptTCP(players.get(msg.playerId).connection.getID(), msg);
                        }
                    });
            listener.addTypeHandler(GunFire.class, (con, msg) -> {
                        sendToAllPlayersBut(msg, players.get(msg.playerID));
                    });
            listener.addTypeHandler(OpenTile.class, (con, msg) -> {
                        handler.updateTile(msg);
                        sendToAllPlayersBut(msg, players.get(msg.sourceId));
                    });
            listener.addTypeHandler(CloseTile.class, (con, msg) -> {
                        handler.updateTile(msg);
                        sendToAllPlayersBut(msg, players.get(msg.sourceId));
                    });
            listener.addTypeHandler(GetStoredItems.class, (con, msg) -> {
                TiledMapTileLayer.Cell cell = gameState.obstaclesLayer.getCell(msg.x, msg.y);
                if (cell == null) {
                    con.sendTCP(new Message().set("[GetStoredItems] Nothing here! " + msg.x + " " + msg.y));
                    return;
                }
                Object data = cell.getData();
                if (data instanceof Storage storage) {
                    con.sendTCP(new StoredItems().set(msg.x, msg.y, ItemInfo.createItemsInfo(storage.getInventoryItems().toArray(Item.class))));
                } else
                    con.sendTCP(new Message().set("[GetStoredItems] Not a storage! " + msg.x + " " + msg.y));
            });
            listener.addTypeHandler(NeedsStorageUpdate.class, (con, msg) -> {
                TiledMapTileLayer.Cell cell = gameState.obstaclesLayer.getCell(msg.x, msg.y);
                if (cell == null) {
                    con.sendTCP(new Message().set("[NeedsStorageUpdate] Nothing here! " + msg.x + " " + msg.y));
                    return;
                }
                Object data = cell.getData();

                if (data instanceof Storage storage) {
                    con.sendTCP(new StoredItems().set(msg.x, msg.y, ItemInfo.createItemsInfo(storage.getInventoryItems().toArray(Item.class))));
                    if (storageListeners.get(storage) == null)
                        storageListeners.put(storage, new Array<>());
                    storageListeners.get(storage).add(msg.playerId);
                } else
                    con.sendTCP(new Message().set("[NeedsStorageUpdate] Not a storage! " + msg.x + " " + msg.y));
            });
            listener.addTypeHandler(StopStorageUpdate.class, (con, msg) -> {
                TiledMapTileLayer.Cell cell = gameState.obstaclesLayer.getCell(msg.x, msg.y);
                if (cell == null) {
                    con.sendTCP(new Message().set("[StopStorageUpdate] Nothing here! " + msg.x + " " + msg.y));
                    return;
                }
                Object data = cell.getData();

                if (data instanceof Storage storage) {
                    if (storageListeners.get(storage) == null) {
                        con.sendTCP(new Message().set("[StopStorageUpdate] You are not getting updates for this storage (no such storage on map)! " + storage.getName() + " " + msg.x + " " + msg.y));
                        return;
                    }
                    if (!storageListeners.get(storage).removeValue(msg.playerId, false)){
                        con.sendTCP(new Message().set("[StopStorageUpdate] You are not getting updates for this storage (no such player id in array)! " + storage.getName() + " " + msg.x + " " + msg.y));
                        return;
                    }
                    if (storageListeners.get(storage).size == 0)
                        storageListeners.remove(storage);
                } else
                    con.sendTCP(new Message().set("[StopStorageUpdate] Not a storage! " + msg.x + " " + msg.y));
            });
            listener.addTypeHandler(MoveItemFromStorageToStorage.class, (con, msg) -> {
                switch (msg.type) {
                    case 0:
                        TiledMapTileLayer.Cell sourceCell = gameState.obstaclesLayer.getCell(msg.sourceX, msg.sourceY);
                        if (sourceCell == null) {
                            con.sendTCP(new Message().set("[MoveItemFromStorageToStorage0] Nothing in source here! " + msg.sourceX + " " + msg.sourceY));
                            return;
                        }
                        TiledMapTileLayer.Cell targetCell = gameState.obstaclesLayer.getCell(msg.targetX, msg.targetY);
                        if (targetCell == null) {
                            con.sendTCP(new Message().set("[MoveItemFromStorageToStorage0] Nothing in target here! " + msg.targetX + " " + msg.targetY));
                            return;
                        }
                        removeItemFromStorage((Storage) sourceCell.getData(), msg.uid);
                        addItemToStorage((Storage) targetCell.getData(), msg.uid);
                        break;
                    case 1:
                        players.get(msg.sourceId).removeItem(items.get(msg.uid));
                        targetCell = gameState.obstaclesLayer.getCell(msg.targetX, msg.targetY);
                        if (targetCell == null) {
                            con.sendTCP(new Message().set("[MoveItemFromStorageToStorage1] Nothing in target here! " + msg.targetX + " " + msg.targetY));
                            return;
                        }
                        addItemToStorage((Storage) targetCell.getData(), msg.uid);
                        break;
                    case 2:
                        sourceCell = gameState.obstaclesLayer.getCell(msg.sourceX, msg.sourceY);
                        if (sourceCell == null) {
                            con.sendTCP(new Message().set("[MoveItemFromStorageToStorage0] Nothing in source here! " + msg.sourceX + " " + msg.sourceY));
                            return;
                        }
                        removeItemFromStorage((Storage) sourceCell.getData(), msg.uid);
                        players.get(msg.targetId).takeItem(items.get(msg.uid));
                        break;
                    case 3:
                        players.get(msg.sourceId).removeItem(items.get(msg.uid));
                        players.get(msg.targetId).takeItem(items.get(msg.uid));
                        break;
                }
            });
            listener.addTypeHandler(AllocateItem.class, (con, msg) -> {
                Item item = handler.allocateItem(items.get(msg.itemInfo.uid), msg.x, msg.y);
                server.sendToAllExceptTCP(con.getID(),new AllocateItem().set(new ItemInfo().set(item.uid, item.stringID), item.getPosition().x, item.getPosition().y));
            });
            listener.addTypeHandler(RemoveItemFromWorld.class, (con, msg) -> {
                handler.removeFromWorld(items.get(msg.uid));
                server.sendToAllExceptTCP(con.getID(), msg);
            });
            listener.addTypeHandler(DropItems.class, (con, msg) -> {
                Item item = handler.allocateItem(items.get(msg.itemUid), msg.x, msg.y);
                players.get(msg.playerId).removeItem(items.get(msg.itemUid));
                server.sendToAllExceptTCP(con.getID(),new AllocateItem().set(new ItemInfo().set(item.uid, item.stringID), item.getPosition().x, item.getPosition().y));
            });
            listener.addTypeHandler(GrenadeInfo.class, (con, msg) -> {
                Grenade item = (Grenade) gameState.items.get(msg.uid);
                if (item == null){
                    item = (Grenade) gameState.itemsFactory.getItem(msg.uid, msg.tileName);
                    item.fire(Math.round(msg.timeToDetonation * 1000), false);
                }
                item.getGameObject().getBehaviour(GrenadeHandler.class).requestUpdate(msg);
                server.sendToAllExceptTCP(con.getID(),msg);
            });

            server.bind(54555,54777);
            server.start();

            endlessThread = new Thread(() -> {

                    tempTime = System.currentTimeMillis();
                try {
                    update();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            endlessThread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    long tempTime;
    public void update() throws InterruptedException {
        while (true){
                long curTime = System.currentTimeMillis();
                gameState.update((curTime - tempTime)/1000f);
                spawner();
                tempTime = curTime;
                sendUpdatePlayersAndEntities();
                //sendStorageUpdates();
                Thread.sleep(sleepTime);
        }
    }

    PlayerMove[] playerMoves = new PlayerMove[0];
    ZombieMove[] zombieMoves = new ZombieMove[0];
    Entity[] tempEntityArray = new Entity[0];
    EntitiesMoves entityMoves = new EntitiesMoves();

    public void sendUpdatePlayersAndEntities(){
        synchronized (players) {
            if (playerMoves.length != players.size()) {
                playerMoves = new PlayerMove[players.size()];
                for (int i = 0; i < playerMoves.length; i++) {
                    playerMoves[i] = new PlayerMove();
                }
            }

            players.values().forEach(new Consumer<>() {
                int i = 0;
                @Override
                public void accept(PlayerInfo playerInfo) {
                    playerMoves[i].set(playerInfo.id, playerInfo.x, playerInfo.y, playerInfo.xSpeed, playerInfo.ySpeed, playerInfo.itemRotation);
                    i++;
                }
            });
        }

        synchronized (entities) {
            tempEntityArray = entities.values().toArray(tempEntityArray);
            if (zombieMoves.length != entities.size()) {
                zombieMoves = new ZombieMove[tempEntityArray.length];
                for (int i = 0; i < zombieMoves.length; i++) {
                    zombieMoves[i] = new ZombieMove();
                }
            }

            for (int i = 0; i < tempEntityArray.length; i++) {
                Entity entity = tempEntityArray[i];
                if (entity instanceof Zombie zombie) {
                    zombieMoves[i].set(zombie);
                }
            }
        }

        sendToAllPlayers(entityMoves.setPlayersMoves(playerMoves).setZMoves(zombieMoves));
    }

//    public void sendStorageUpdates(){
//        for (Storage storage : storageUpdateMap.keys()) {
//            Vector2 pos = storage.getPosition();
//            sendToAllPlayers(new StoredItems().set((int)Math.floor(pos.x), (int)Math.floor(pos.y), ItemInfo.createItemsInfo(storage.getInventoryItems().toArray(Item.class))));
//        }
//    }

    public void sendToAllPlayers(Object obj){

        var pls = players.values().toArray(new PlayerInfo[0]);
        for (int i = 0; i < players.size(); i ++) {
            PlayerInfo plInf = pls[i];
            plInf.connection.sendTCP(obj);
        }
    }
    public void sendToAllPlayersBut(Object obj, PlayerInfo... playerInfos){
        for (PlayerInfo plInf : players.values()){
            for (PlayerInfo plInf2 : playerInfos)
                if (plInf != plInf2)
                    plInf.connection.sendTCP(obj);
        }
    }
    public void removeItemFromStorage(Storage storage, long uid){
        storage.removeItem(items.get(uid));
        sendStorageUpdateToListeners(storage);
    }
    public void addItemToStorage(Storage storage, long uid){
        storage.takeItem(items.get(uid));
        sendStorageUpdateToListeners(storage);
    }
    public void sendStorageUpdateToListeners(Storage storage){
        if (storageListeners.containsKey(storage)){
            Array<Long> arr = storageListeners.get(storage);
            arr.forEach(aLong -> {
                server.sendToTCP(players.get(aLong).connection.getID(), new StoredItems().set((int)Math.floor(storage.getPosition().x),
                        (int)Math.floor(storage.getPosition().y),
                        ItemInfo.createItemsInfo(storage.getInventoryItems().toArray(Item.class))));
            });
        }
    }

    void init(){
        gameState = new ServGameStateConstructor().createGameState(this);
        handler = gameState.serverHandler;
        players = Collections.synchronizedMap(new HashMap<>());
        entities = gameState.entities;
        items = gameState.items;
    }

    void startWave() {
        wave = 0;
    }

    Vector2 spawnCenter = new Vector2(10, 85);
    float spawnRadius = 5f;
    long spawnPeriod = 2000;
    Vector2 zombieSpawnPoint = new Vector2(1, 1).nor().scl(spawnRadius);
    int wave = 0;
    long lastSpawnTime = 0;

    void spawner(){
        if (wave > 0 && (System.currentTimeMillis() - lastSpawnTime > spawnPeriod)){
            float endSpawnPointX;
            float endSpawnPointY;
            do {
                zombieSpawnPoint.rotateDeg((float) Math.random()*360);
                endSpawnPointX = spawnCenter.x + zombieSpawnPoint.x;
                endSpawnPointY = spawnCenter.y + zombieSpawnPoint.y;
            } while (((TiledMapTileLayer)gameState.map.getLayers().get("obstacles")).getCell((int)endSpawnPointX, (int)endSpawnPointY) != null);
            spawnZombie(endSpawnPointX, endSpawnPointY);
            lastSpawnTime = System.currentTimeMillis();
        }
    }

    void spawnZombie(float x, float y){
        Zombie zombie = handler.spawnZombie(entitiesCounter.getAndIncrement(),  "zombie" + entitiesCounter, 10, x, y);
        sendToAllPlayers(new SpawnEntity().set(new ZombieInfo().set(zombie)));
    }

    public void killEntity(long id){
        Entity entity = entities.get(id);
        if (entity == null) {
            HandyHelper.instance.log("[GameServer:killentity:246] No entity with " + id + " id to kill.");
            return;
        }
        if (entity.isAlive())
            entity.kill();
        entities.remove(id);
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
