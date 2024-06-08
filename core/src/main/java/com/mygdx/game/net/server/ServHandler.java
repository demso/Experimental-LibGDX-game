package com.mygdx.game.net.server;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.tiles.Storage;
import com.mygdx.game.net.GameServer;
import com.mygdx.game.net.messages.common.AllocateItem;
import com.mygdx.game.net.messages.common.tileupdate.UpdateTile;

public class ServHandler {
    public ServGameState gameState;
    public GameServer server;

    public ServHandler(ServGameState state, GameServer serv) {
        gameState = state;
        server = serv;
    }

    public void update(){}

    public Zombie spawnZombie(long id, String name, float hp, float x, float y){
        Zombie zomb = (Zombie) gameState.mobsFactory.spawnEntity(id, Entity.Kind.ZOMBIE, x, y);
        zomb.setName(name);
        zomb.setHp(hp);
        gameState.entities.put(id, zomb);
        return zomb;
    }

    public void updateTile(UpdateTile updater){
        updater.updateTile(gameState.map);
    }

    public Item allocateItem(Item item, float toX, float toY){
        Storage owner = item.getOwner();
        if (owner != null)
            owner.removeItem(item);
        item.allocate(new Vector2(toX, toY));
        return item;
    }

    public void removeFromWorld(Item item){
        item.removeFromWorld();
    }
}
