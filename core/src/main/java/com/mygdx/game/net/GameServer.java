package com.mygdx.game.net;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.factories.MobsFactoryC;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.gamestate.tiledmap.loader.MyTiledMap;
import com.mygdx.game.gamestate.tiledmap.tiled.TiledMapTileLayer;
import com.mygdx.game.net.messages.*;
import com.mygdx.game.net.messages.client.Begin;
import com.mygdx.game.net.messages.client.End;
import com.mygdx.game.net.messages.client.PlayerMove;
import com.mygdx.game.net.messages.client.Ready;
import com.mygdx.game.net.messages.server.*;
import com.mygdx.game.net.server.ServGameState;
import com.mygdx.game.net.server.ServGameStateConstructor;
import com.mygdx.game.net.server.ZombieHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GameServer {
    Server server;
    Listener.TypeListener listener;
    public volatile ObjectMap<String, PlayerInfo> players;
    public volatile ObjectMap<Long, ZombieInfo> entities;
    long entitiesCounter  = 0;
    Vector2 spawnPoint = new Vector2(10,87);
    Thread endlessThread;
    long sleepTime = Math.round(Globals.SERVER_UPDATE_TIME * 1000);
    public ServGameState gameState;
    public ZombieHelper zhelper = new ZombieHelper(this);

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
                        con.sendTCP(new OnConnection(mapToLoad, plInf.x, plInf.y)
                                .addPlayers(players.values().toArray().toArray(PlayerInfo.class))
                                .addEntities(entities.values().toArray().toArray(EntityInfo.class)));
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
                            sendToAllPlayersBut(msg, players.get(msg.playerName));
                        });
            listener.addTypeHandler(End.class,
                    (con, msg) -> {
                        PlayerInfo inf = players.remove(msg.playerName);
                        sendToAllPlayersBut(msg, inf);
                    });
            listener.addTypeHandler(Ready.class,
                    (con, msg) -> {
                        startWave();
                        //con.sendTCP();
                    });
            listener.addTypeHandler(EntityShot.class,
                    (con, msg) -> {
                        Entity zomb = gameState.entities.get(msg.id);
                        if (zomb == null || !zomb.isAlive()){
                            HandyHelper.instance.log("[GameServer:93] Entity is unreachable on server, is null = " + (zomb == null)
                                    + ", hp = " + (zomb == null ? "" : zomb.getHp()));
                            return;
                        }
                        gameState.entities.get(msg.id).hurt(msg.damage);
                        ZombieInfo zInf = entities.get(msg.id);
                        zInf.hp = entities.get(msg.id).hp = zomb.getHp();
                        if (!gameState.entities.get(msg.id).isAlive()){
                            killEntity(msg.id);
                            sendToAllPlayers(new KillEntity().set(msg.id));
                        } else
                            server.sendToAllExceptTCP(players.get(msg.playerName).connection.getID(), msg);
                    });

            server.addListener(listener);
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
                playerMoves[i].set(playerInfo.name, playerInfo.x, playerInfo.y, playerInfo.xSpeed, playerInfo.ySpeed, playerInfo.itemRotation);
                i++;
            }
        });

        if (zombieMoves.length != entities.size){
            zombieMoves = new ZombieMove[entities.size];
            for (int i = 0; i < zombieMoves.length; i ++){
                zombieMoves[i] = new ZombieMove();
            }
        }

        int z = 0;
        for (ZombieInfo zomb :  new Array.ArrayIterator<>(entities.values().toArray())){
            Zombie zo = (Zombie) gameState.entities.get(zomb.id);
            zomb.setInfoFromZombie((Zombie) gameState.entities.get(zomb.id));
            zhelper.fillMove(zomb, zombieMoves[z]);
            //HandyHelper.instance.periodicLog(zo.getPosition().x + " " + zo.getPosition().y + " " + zomb.xSpeed + " " + zomb.ySpeed);
            z++;
        }

        sendToAllPlayers(new EntitiesMoves().setPlayersMoves(playerMoves).setZMoves(zombieMoves));
    }

//    public void sendUpdateEntities(){
//        for (ZombieInfo zomb :  new Array.ArrayIterator<>(entities.values().toArray())){
//            zomb.getMove();
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
        entities = new ObjectMap<>();
        gameState = new ServGameStateConstructor().createGameState(this);
    }

    void startWave() {
        wave = 1;
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
        ZombieInfo zomInf = new ZombieInfo().set(entitiesCounter,  MobsFactoryC.Type.ZOMBIE,
                "zombie" + entitiesCounter, 10, x, y, 0, 0);
        gameState.getServerHandler().spawnEntity(zomInf);
        entities.put(entitiesCounter, zomInf);
        sendToAllPlayers(new SpawnEntity().set(zomInf));
        entitiesCounter++;
    }

    public void killEntity(long id){
        if (gameState.entities.get(id).isAlive())
            gameState.entities.get(id).kill();
        gameState.entities.remove(id);
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
