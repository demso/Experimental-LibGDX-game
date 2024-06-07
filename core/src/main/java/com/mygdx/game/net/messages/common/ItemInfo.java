package com.mygdx.game.net.messages.common;

import com.mygdx.game.gamestate.objects.items.Item;

public class ItemInfo {
    public String itemId;

    public static ItemInfo[] createItemsInfo(Item... items) {
        ItemInfo[] itemInfos = new ItemInfo[items.length];
        for (int i = 0; i < items.length; i++) {
            itemInfos[i] = new ItemInfo();
            itemInfos[i].itemId = items[i].itemId;
        }
        return itemInfos;
    }
}
