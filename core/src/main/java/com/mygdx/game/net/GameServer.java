package com.mygdx.game.net;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.tiles.Storage;
import com.mygdx.game.gamestate.tiledmap.tiled.TiledMapTileLayer;
import com.mygdx.game.net.messages.client.*;
import com.mygdx.game.net.messages.common.ItemInfo;
import com.mygdx.game.net.messages.common.Message;
import com.mygdx.game.net.messages.common.ZombieInfo;
import com.mygdx.game.net.messages.common.tileupdate.CloseTile;
import com.mygdx.game.net.messages.common.tileupdate.OpenTile;
import com.mygdx.game.net.messages.server.*;
import com.mygdx.game.net.server.*;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class GameServer {
    Server server;
    Listener.TypeListener listener;
    public volatile ObjectMap<Long, PlayerInfo> players;
    public volatile ObjectMap<Long, Entity> entities;//equals to gamestate.entities
    volatile AtomicLong entitiesCounter = new AtomicLong(1);
    Vector2 spawnPoint = new Vector2(10,87);
    Thread endlessThread;
    long sleepTime = Math.round(Globals.SERVER_UPDATE_TIME * 1000);
    public ServGameState gameState;
    public ZombieHelper zhelper = new ZombieHelper(this);
    ObjectMap.Values<Entity> entityMapVals;
    public ServHandler handler;
    ObjectMap<Storage, Array<Long>> storageUpdateMap = new ObjectMap<>(); //player id -> storage

    public final String mapToLoad = "tiled/firstmap/worldmap.tmx";
    public GameServer() {
        try {
            init();
            server = new Server();
            Registerer.register(server.getKryo());
            listener = new Listener.TypeListener();
            server.addListener(listener);
            //Log.TRACE();
//            Log.setLogger(new CustomKryoLogger());
            listener.addTypeHandler(Message.class, (connection, message) -> {
                        System.out.println("Recived on server!");
                    });
            listener.addTypeHandler(Begin.class, (con, msg) -> {
                        PlayerInfo plInf = new PlayerInfo(msg.name, con).playerSet(spawnPoint.x, spawnPoint.y, 0, 0, 0);
                        plInf.id = entitiesCounter.get();
                        entitiesCounter.incrementAndGet();
                        players.put(plInf.id, plInf);
                        con.sendTCP(new OnConnection(plInf.id, mapToLoad, plInf.x, plInf.y)
                                .addPlayers(players.values().toArray().toArray(PlayerInfo.class))
                                .addEntities(entities.values().toArray().toArray(Entity.class)));
                        newPlayerJoined(plInf);
                    });
            listener.addTypeHandler(PlayerMove.class, (con, msg) -> {
                        players.get(msg.playerId).playerSet(msg.x, msg.y, msg.xSpeed, msg.ySpeed, msg.rotation);
                      });
            listener.addTypeHandler(PlayerEquip.class, (con, msg) -> {
                            players.get(msg.playerId).equip(msg.itemId);
                            sendToAllPlayersBut(msg, players.get(msg.playerId));
                        });
            listener.addTypeHandler(End.class, (con, msg) -> {
                        PlayerInfo inf = players.remove(msg.playerId);
                        sendToAllPlayersBut(msg, inf);
                    });
            listener.addTypeHandler(Ready.class, (con, msg) -> {
                        startWave();
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
                    if (storageUpdateMap.get(storage) == null)
                        storageUpdateMap.put(storage, new Array<>());
                    storageUpdateMap.get(storage).add(msg.playerId);
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
                    if (storageUpdateMap.get(storage) == null) {
                        con.sendTCP(new Message().set("[StopStorageUpdate] You are not getting updates for this storage (no such storage on map)! " + storage.getName() + " " + msg.x + " " + msg.y));
                        return;
                    }
                    if (!storageUpdateMap.get(storage).removeValue(msg.playerId, false)){
                        con.sendTCP(new Message().set("[StopStorageUpdate] You are not getting updates for this storage (no such player id in array)! " + storage.getName() + " " + msg.x + " " + msg.y));
                        return;
                    }
                    if (storageUpdateMap.get(storage).size == 0)
                        storageUpdateMap.remove(storage);
                } else
                    con.sendTCP(new Message().set("[StopStorageUpdate] Not a storage! " + msg.x + " " + msg.y));
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
                Thread.sleep(sleepTime);
        }
    }

    PlayerMove[] playerMoves = new PlayerMove[0];
    ZombieMove[] zombieMoves = new ZombieMove[0];
    Array<Entity> tempEntityArray = new Array<>();
    EntitiesMoves entityMoves = new EntitiesMoves();

    public void sendUpdatePlayersAndEntities(){
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
                playerMoves[i].set(playerInfo.id, playerInfo.x, playerInfo.y, playerInfo.xSpeed, playerInfo.ySpeed, playerInfo.itemRotation);
                i++;
            }
        });

        entityMapVals.reset();
        tempEntityArray.clear();
        entityMapVals.toArray(tempEntityArray);
        if (zombieMoves.length != tempEntityArray.size){
            zombieMoves = new ZombieMove[tempEntityArray.size];
            for (int i = 0; i < zombieMoves.length; i ++){
                zombieMoves[i] = new ZombieMove();
            }
        }

        for (int i = 0; i < tempEntityArray.size; i++){
            Entity entity = tempEntityArray.get(i);
            if (entity instanceof Zombie zombie) {
                zombieMoves[i].set(zombie);
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

    public void newPlayerJoined(PlayerInfo beg){
        sendToAllPlayersBut(new PlayerJoined(beg), beg);
    }

    public void sendToAllPlayers(Object obj){
        var pls = players.values().toArray();
        for (int i = 0; i < players.size; i ++) {
            PlayerInfo plInf = pls.get(i);
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

    void init(){
        gameState = new ServGameStateConstructor().createGameState(this);
        handler = gameState.serverHandler;
        players = new ObjectMap<>();
        entities = gameState.entities;
        entityMapVals = new ObjectMap.Values<>(entities);
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
