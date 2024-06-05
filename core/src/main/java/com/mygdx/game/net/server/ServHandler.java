package com.mygdx.game.net.server;

import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.net.GameServer;

public class ServHandler {
    public ServGameState gameState;
    public GameServer server;
//    public PlayerJoined playerJoined;
//    public boolean entitiesNeedUpdate;
//    public EntitiesMoves moves;
//    volatile public Array<Player> playersToKill = new Array<>();
//    volatile public Array<EntityInfo> entitiesToSpawn = new Array<>();
//    volatile public LongArray entitiesToKill = new LongArray();

    public ServHandler(ServGameState state, GameServer serv) {
        gameState = state;
        server = serv;
    }

    public void update(){
//        if (entitiesToKill.size != 0) {
//            for (long id : entitiesToKill.items)
//                gameState.entities.get(id).kill();
//            entitiesToKill.clear();
//        }

//        if (entitiesToSpawn.size != 0) {
//            for (EntityInfo inf :  new Array.ArrayIterator<>(entitiesToSpawn)) {
//                Zombie zomb = (Zombie) MobsFactory.spawnEntity(inf.id, inf.type, inf.x, inf.y);
//                zomb.zombieHandler.destroy();
//                zomb.zombieHandler = new ServerZombieAIBehaviour(zomb.zombieObject);
//                gameState.entities.put(inf.id, zomb);
//            }
//            entitiesToSpawn.clear();
//        }

//        if (playersToKill.size != 0) {
//            for (Player p :  new Array.ArrayIterator<>(playersToKill)){
//                p.destroy();
//                gameState.players.remove(p.getName());
//            }
//        }


//        if (playerJoined != null){
//            playerJoined(playerJoined.playerInfo);
//            playerJoined = null;
//        }
    }

//    public void spawnEntity(EntityInfo inf){
//        Zombie zomb = (Zombie) gameState.mobsFactory.spawnEntity(inf.id, inf.type, inf.x, inf.y);
//        gameState.entities.put(inf.id, zomb);
//    }

    public Zombie spawnZombie(long id, String name, float hp, float x, float y){
        Zombie zomb = (Zombie) gameState.mobsFactory.spawnEntity(id, Entity.Kind.ZOMBIE, x, y);
        zomb.setName(name);
        zomb.setHp(hp);
        gameState.entities.put(id, zomb);
        return zomb;
    }

//    public void killEntity(long id){
//        gameState.entities.get(id).kill();
//    }

//    public void playerJoined(PlayerInfo plInf){
//        Player anotherPlayer = AnotherPlayerConstructor.createPlayer(plInf.name);
//        anotherPlayer.setPosition(plInf.x, plInf.y);
//        if (plInf.equippedItemId != null)
//            anotherPlayer.equipItem(ItemsFactory.getItem(plInf.equippedItemId));
//        gameState.players.put(plInf.id, anotherPlayer);
//    }
//
//    public void playerExited(long id) {
//        Player p = gameState.players.get(id);
//        p.destroy();
//        gameState.players.remove(p.getId());
//    }
}
