package com.mygdx.game.gamestate.UI.inventory;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.objects.Item;


public interface HasInventory {
        public Array<Item> getInventoryItems();
        public void removeItemFromInventory(Item item);
        public void addItemToInventory(Item item);
        public Vector2 getPosition();
}
