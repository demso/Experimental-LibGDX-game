package com.mygdx.game.net.messages.client;

import com.mygdx.game.net.messages.common.ItemInfo;

public class PlayerTakeItems {
    public ItemInfo[] uids;
    public long playerId;

    public PlayerTakeItems set(long playerId, ItemInfo... id) {
        this.playerId = playerId;
        uids = id;
        return this;
    }
}
