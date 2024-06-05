package com.mygdx.game.gamestate.player;

import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.items.Item;

public class ClientPlayer extends Player{
    @Override
    public void fire() {
        super.fire();
    }

    @Override
    public Item uneqipItem() {
        Item tmpItem = super.uneqipItem();
        SecondGDXGame.instance.client.itemEquipped(tmpItem, false);
        return tmpItem;
    }

    @Override
    public void equipItem(Item item) {
        super.equipItem(item);
        SecondGDXGame.instance.client.itemEquipped(item, true);
    }
}
