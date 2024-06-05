package com.mygdx.game.net.messages.client;

public class PlayerEquip {
    public String itemId;
    public long playerId;
    public String senderName;
    public boolean isEquipped;
    public PlayerEquip(){}
    public PlayerEquip set(String in, long pi, String sendNam, boolean ie){
        itemId = in;
        playerId = pi;
        isEquipped = ie;
        senderName = sendNam;
        return this;
    }
}
