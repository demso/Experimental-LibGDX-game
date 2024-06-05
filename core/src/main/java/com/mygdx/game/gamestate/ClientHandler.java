package com.mygdx.game.gamestate;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongArray;
import com.mygdx.game.gamestate.factories.ItemsFactory;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.gamestate.player.AnotherPlayerConstructor;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.net.GameClient;
import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.EntityInfo;
import com.mygdx.game.net.messages.client.PlayerMove;
import com.mygdx.game.net.messages.server.EntitiesMoves;
import com.mygdx.game.net.messages.server.PlayerJoined;
import com.mygdx.game.net.messages.server.ZombieMove;

public class ClientHandler {

    public PlayerJoined playerJoined;
    public boolean entitiesNeedUpdate;
    public EntitiesMoves moves;
    volatile public Array<Player> playersToKill = new Array<>();
    volatile public Array<EntityInfo> entitiesToSpawn = new Array<>();
    volatile public LongArray entitiesToKill = new LongArray();
    volatile public GameClient client;

    public ClientHandler(GameClient cli){
        client = cli;
    }

    public void update(float delta){
        if (entitiesToKill.size != 0) {
            for (long id : entitiesToKill.toArray()){
                Entity entity = GameState.instance.entities.get(id);
                entity.kill();
                GameState.instance.entities.remove(entity.getId());
            }
            entitiesToKill.clear();
        }

        if (entitiesToSpawn.size != 0) {
            for (EntityInfo inf :  new Array.ArrayIterator<>(entitiesToSpawn))
                GameState.instance.entities.put(inf.id, GameState.instance.mobsFactory.spawnEntity(inf.id, inf.type, inf.x, inf.y));
            entitiesToSpawn.clear();
        }

        if (playersToKill.size != 0) {
            for (Player p :  new Array.ArrayIterator<>(playersToKill)){
                p.destroy();
                GameState.instance.players.remove(p.getName());
            }
        }

        if (entitiesNeedUpdate) {
            for (PlayerMove move : moves.pmoves) {
                if (GameState.instance.players.get(move.name) == null)
                    continue;
                GameState.instance.players.get(move.name).playerHandler.receivePlayerUpdate(move);
            }
            for (ZombieMove move : moves.zmoves) {
                if (GameState.instance.entities.get(move.id) == null)
                    continue;
                GameState.instance.entities.get(move.id).serverUpdate(move);
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
        if (plInf.equippedItemId != null)
            anotherPlayer.equipItem(ItemsFactory.getItem(plInf.equippedItemId));
        GameState.instance.players.put(plInf.name, anotherPlayer);
    }

    public void playerExited(String playerName) {
        playersToKill.add(GameState.instance.players.get(playerName));
    }


}
