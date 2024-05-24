package com.mygdx.game.gamestate.objects.tiles;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.objects.items.SimpleItem;

public interface Storage {
    public Array<SimpleItem> getInventoryItems();
    public void dropItem(SimpleItem item);
    public void takeItem(SimpleItem item);
}
