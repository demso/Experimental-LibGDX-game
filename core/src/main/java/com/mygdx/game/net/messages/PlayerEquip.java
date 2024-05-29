package com.mygdx.game.net.messages;

public class PlayerEquip {
    public String itemId;
    public String playerName;
    public String senderName;
    public boolean isEquipped;
    public PlayerEquip(){}
    public PlayerEquip set(String in, String pn, String sendNam, boolean ie){
        itemId = in;
        playerName = pn;
        isEquipped = ie;
        senderName = sendNam;
        return this;
    }
}
