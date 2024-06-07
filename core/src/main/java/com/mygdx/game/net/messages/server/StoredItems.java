package com.mygdx.game.net.messages.server;

import com.mygdx.game.net.messages.common.ItemInfo;

public class StoredItems {
    public int x, y;
    public ItemInfo[] items;
    public StoredItems set(int x, int y, ItemInfo... items) {
        this.items = items;
        this.x = x;
        this.y = y;
        return this;
    }
}
