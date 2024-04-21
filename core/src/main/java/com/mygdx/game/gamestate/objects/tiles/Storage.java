package com.mygdx.game.gamestate.objects.tiles;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.objects.Item;

public interface Storage {
    public Array<Item> getInventoryItems();
    public void dropItem(Item item);
    public void takeItem(Item item);
}
