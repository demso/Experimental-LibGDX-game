package com.mygdx.game.gamestate.player;

import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.objects.items.Item;

public class ClientPlayer extends Player{
    @Override
    public void fire() {
        super.fire();
        SecondGDXGame.instance.client.onGunFire();
    }

    @Override
    public Item uneqipItem() {
        Item tmpItem = super.uneqipItem();
        SecondGDXGame.instance.client.onItemEquipped(tmpItem, false);
        return tmpItem;
    }

    @Override
    public void equipItem(Item item) {
        super.equipItem(item);
        SecondGDXGame.instance.client.onItemEquipped(item, true);
    }
}
