package com.mygdx.game.net.messages.common;

import com.mygdx.game.gamestate.objects.items.Item;

public class ItemInfo {
    public long uid;
    public String itemId;

    public static ItemInfo[] createItemsInfo(Item... items) {
        ItemInfo[] itemInfos = new ItemInfo[items.length];
        for (int i = 0; i < items.length; i++) {
            itemInfos[i] = new ItemInfo();
            itemInfos[i].itemId = items[i].itemId;
            itemInfos[i].uid = items[i].uid;
        }
        return itemInfos;
    }

    public ItemInfo set(long uid, String itemId) {
        this.uid = uid;
        this.itemId = itemId;
        return this;
    }

    public static ItemInfo createItemInfo(Item item) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.itemId = item.itemId;
        itemInfo.uid = item.uid;
        return itemInfo;
    }
}
