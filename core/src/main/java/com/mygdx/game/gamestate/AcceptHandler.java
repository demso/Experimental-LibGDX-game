package com.mygdx.game.gamestate;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongArray;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.gamestate.player.AnotherPlayerConstructor;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.net.GameClient;
import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.common.EntityInfo;
import com.mygdx.game.net.messages.common.ZombieInfo;
import com.mygdx.game.net.messages.client.GunFire;
import com.mygdx.game.net.messages.client.PlayerMove;
import com.mygdx.game.net.messages.common.tileupdate.UpdateTile;
import com.mygdx.game.net.messages.server.EntitiesMoves;
import com.mygdx.game.net.messages.server.PlayerJoined;
import com.mygdx.game.net.messages.server.ZombieMove;

public class AcceptHandler {

    public PlayerJoined playerJoined;
    public boolean entitiesNeedUpdate;
    public EntitiesMoves moves;
    volatile public Array<Player> playersToKill = new Array<>();
    volatile public Array<EntityInfo> entitiesToSpawn = new Array<>();
    volatile public LongArray entitiesToKill = new LongArray();
    volatile public GameClient client;
    public GameState gameState;

    public AcceptHandler(GameClient cli, GameState state){
        client = cli;
        gameState = state;
    }

    public void update(float delta){
        if (entitiesToKill.size != 0) {
            for (long id : entitiesToKill.toArray()){
                Entity entity = gameState.entities.get(id);
                entity.kill();
                gameState.entities.remove(entity.getId());
            }
            entitiesToKill.clear();
        }

        if (entitiesToSpawn.size != 0) {
            for (EntityInfo inf :  new Array.ArrayIterator<>(entitiesToSpawn)){
                Entity entity = gameState.entities.put(inf.id, gameState.mobsFactory.spawnEntity(inf.id, inf.type, inf.x, inf.y));
                if (entity instanceof Zombie zombie){
                    zombie.setTarget(GameState.instance.players.get(((ZombieInfo) inf).targetId));
                }

            }
            entitiesToSpawn.clear();
        }

        if (playersToKill.size != 0) {
            for (Player p :  new Array.ArrayIterator<>(playersToKill)){
                p.dispose();
                gameState.players.remove(p.getId());
            }
        }

        if (entitiesNeedUpdate) {
            for (PlayerMove move : moves.pmoves) {
                if (gameState.players.get(move.playerId) == null)
                    continue;
                gameState.players.get(move.playerId).playerHandler.receivePlayerUpdate(move);
            }
            for (ZombieMove move : moves.zmoves) {
                if (gameState.entities.get(move.id) == null)
                    continue;
                gameState.entities.get(move.id).serverUpdate(move);
            }
            entitiesNeedUpdate = false;
        }

        if (playerJoined != null){
            playerJoined(playerJoined.playerInfo);
            playerJoined = null;
        }
    }

    public void spawnEntity(EntityInfo info){
        entitiesToSpawn.add(info);
    }

    public void receivedKillEntity(long id){
        entitiesToKill.add(id);
    }

    public void playerJoined(PlayerInfo plInf){
        Player anotherPlayer = AnotherPlayerConstructor.createPlayer(plInf.name);
        anotherPlayer.setPosition(plInf.x, plInf.y);
        if (plInf.equippedItem != null)
            anotherPlayer.equipItem(gameState.itemsFactory.getItem(plInf.equippedItem));
        gameState.players.put(plInf.id, anotherPlayer);
    }

    public void playerExited(long playerId) {
        playersToKill.add(gameState.players.get(playerId));
    }

    public void gunFire(GunFire msg){
        gameState.players.get(msg.playerID).fire();
    }

    public void updateTile(UpdateTile updater){
        updater.updateTile(gameState.map);
    }

}
