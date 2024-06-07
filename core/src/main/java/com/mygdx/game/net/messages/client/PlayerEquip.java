package com.mygdx.game.net.messages.client;

import com.mygdx.game.net.messages.common.ItemInfo;

public class PlayerEquip {
    public ItemInfo equippedItem;
    public long playerId;
    public String senderName;
    public boolean isEquipped;
    public PlayerEquip(){}
    public PlayerEquip set(ItemInfo in, long pi, String sendNam, boolean ie){
        equippedItem = in;
        playerId = pi;
        isEquipped = ie;
        senderName = sendNam;
        return this;
    }
}
