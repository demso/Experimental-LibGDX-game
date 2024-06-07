package com.mygdx.game.net;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.net.messages.client.*;
import com.mygdx.game.net.messages.common.*;
import com.mygdx.game.net.messages.common.tileupdate.CloseTile;
import com.mygdx.game.net.messages.common.tileupdate.OpenTile;
import com.mygdx.game.net.messages.common.tileupdate.UpdateTile;
import com.mygdx.game.net.messages.server.*;

public class Registerer {
    public static void register(Kryo kryo){
        kryo.register(Message.class);
        kryo.register(Registerer.class);
        kryo.register(Begin.class);
        kryo.register(End.class);
        kryo.register(OnConnection.class);
        kryo.register(PlayerMove.class);
        kryo.register(PlayerJoined.class);
        kryo.register(PlayerInfo.class);
        kryo.register(PlayerInfo[].class);
        kryo.register(EntitiesMoves.class);
        kryo.register(PlayerMove[].class);
        kryo.register(ItemInfo.class);
        kryo.register(PlayerEquip.class);
        kryo.register(SpawnEntity.class);
        kryo.register(KillEntity.class);
        kryo.register(EntityInfo.class);
        kryo.register(Ready.class);
        kryo.register(ZombieInfo.class);
        kryo.register(ZombieMove.class);
        kryo.register(ZombieMove[].class);
        kryo.register(EntitiesMove.class);
        kryo.register(Vector2.class);
        kryo.register(EntityInfo[].class);
        kryo.register(EntityShot.class);
        kryo.register(Entity.Kind.class);
        kryo.register(GunFire.class);
        kryo.register(UpdateTile.class);
        kryo.register(OpenTile.class);
        kryo.register(CloseTile.class);
        kryo.register(GetStoredItems.class);
        kryo.register(StoredItems.class);
        kryo.register(ItemInfo.class);
        kryo.register(ItemInfo[].class);
        kryo.register(NeedsStorageUpdate.class);
        kryo.register(StopStorageUpdate.class);
        kryo.register(MoveItemFromStorageToStorage.class);
        kryo.register(AllocateItem.class);
        kryo.register(DisposeItem.class);
        kryo.register(long[].class);
        //kryo.register();
    }
}
