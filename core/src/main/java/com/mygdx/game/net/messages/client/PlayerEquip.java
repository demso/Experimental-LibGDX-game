package com.mygdx.game.net.messages.client;

import com.mygdx.game.net.messages.common.ItemInfo;

public class PlayerEquip {
    public ItemInfo equippedItem;
    public long playerId;//who to equip
    public boolean isEquipped;
    public PlayerEquip(){}
    public PlayerEquip set(ItemInfo in, long pi, boolean ie){
        equippedItem = in;
        playerId = pi;
        isEquipped = ie;
        return this;
    }
}
