package com.mygdx.game.net.messages.common;
//do not send sender
public class AllocateItem {
    public ItemInfo itemInfo;
    public float x, y;

    public AllocateItem set(ItemInfo itemInfo, float x, float y) {
        this.itemInfo = itemInfo;
        this.x = x;
        this.y = y;
        return this;
    }
}
