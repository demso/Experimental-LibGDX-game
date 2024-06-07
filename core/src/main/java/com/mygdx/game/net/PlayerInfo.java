package com.mygdx.game.net;

import com.esotericsoftware.kryonet.Connection;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.net.messages.common.EntityInfo;
import com.mygdx.game.net.messages.common.ItemInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

public class PlayerInfo extends EntityInfo {
    public ItemInfo equippedItem;
    public float itemRotation;
    public ItemInfo[] inventoryItems;
    @Getter @Setter transient Connection connection;

    public PlayerInfo(String name, Connection con){
        this.name = name;
        connection = con;
    }

    public PlayerInfo(){}


    public PlayerInfo playerSet(float x, float y, float xS, float yS, float itemRot){
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        itemRotation = itemRot;
        return this;
    }

    public PlayerInfo equip(ItemInfo in){
        equippedItem = in;
        return this;
    }

    public PlayerInfo setInventory(ItemInfo... items){
        inventoryItems = items;
        return this;
    }

    public PlayerInfo addItem(Item item){
        if (inventoryItems == null)
            inventoryItems = new ItemInfo[1];
        else
            inventoryItems = Arrays.copyOf(inventoryItems, inventoryItems.length + 1);
        inventoryItems[inventoryItems.length - 1] = new ItemInfo().set(item.uid, item.itemId);
        item.ownerId = id;
        return this;
    }

    public PlayerInfo removeItem(Item item){
        if (inventoryItems == null)
            return this;

        for (int i = 0; i < inventoryItems.length; i++){
            if (inventoryItems[i].itemId == item.itemId){
                inventoryItems[i] = null;

                ItemInfo[] newInventoryItems = new ItemInfo[inventoryItems.length - 1];
                int z = 0;
                for (int j = 0; j < inventoryItems.length; j++){
                    if (inventoryItems[j] != null)
                        newInventoryItems[z++] = inventoryItems[j];
                }

                inventoryItems = newInventoryItems;

                break;
            }
        }
        return this;
    }

}
