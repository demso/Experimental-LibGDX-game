package com.mygdx.game.gamestate.objects.tiles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.objects.items.Item;

public interface Storage {
    Array<Item> getInventoryItems();
    void setInventoryItems(Item... items);
    void dropItem(Item item);
    void takeItem(Item item);
    Vector2 getPosition();
    String getName();
}
