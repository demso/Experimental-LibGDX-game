package com.mygdx.game.gamestate.player;

import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.gamestate.objects.items.guns.GunMagazine;

public class ClientPlayer extends Player {
    @Override
    public boolean fire() {
        if(super.fire()){
            SecondGDXGame.instance.client.onGunFire();
            return true;
        }

        return false;
    }

    @Override
    public void removeItem(Item item) {
        super.removeItem(item);
        GameState.instance.hud.updateInvHUDContent();
    }

    @Override
    public void takeItem(Item item) {
        super.takeItem(item);
        GameState.instance.hud.updateInvHUDContent();
    }

    @Override
    public Item uneqipItem() {
        Item tmpItem = super.uneqipItem();
        SecondGDXGame.instance.client.onItemEquipped(tmpItem, false);
        return tmpItem;
    }

    public void reload(){
        if (equipedItem instanceof Gun gun) {
            GunMagazine magaz = (GunMagazine) getItemOfType(GunMagazine.class);
            if (magaz != null) {
                gun.reload(magaz);
            }
        }
    }

    public Item getItemOfType(Class<? extends Item> itemClass){
        for (Item item : getInventoryItems()){
            if (item.getClass().isInstance(itemClass) || item.getClass().equals(itemClass))
                return item;
        }
        return null;
    }


    @Override
    public boolean interact() {
        if (closestObject != null) {
            var obj = (Interactable) closestObject.getUserData();
            obj.interact(this);
            if (obj instanceof Item item)
                GameState.instance.client.pickedUpItem(item);
            return true;
        }
        return false;
    }

    @Override
    public void equipItem(Item item) {
        super.equipItem(item);
        SecondGDXGame.instance.client.onItemEquipped(item, true);
    }
}
