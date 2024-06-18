package com.mygdx.game.gamestate.player;

import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.items.grenade.Grenade;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.items.Meds;
import com.mygdx.game.gamestate.objects.items.guns.Gun;

public class ClientPlayer extends Player {
    public boolean debug = true;
    @Override
    public boolean fire() {
        if (equipedItem != null && equipedItem instanceof Gun gun) {
            if(gun.fireBullet(true)){
                SecondGDXGame.instance.client.onGunFire();
                return true;
            }
        }
        return false;
    }

    public boolean throwGrenade(long time){
        Grenade item = getItemOfType(Grenade.class);
        if (item != null) {
            item.fire(time, true);
            GameState.instance.client.onGrenadeThrown(item);
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
        needsReload = true;
//        if (equipedItem instanceof Gun gun) {
//            GunMagazine magaz = getItemOfType(GunMagazine.class);
//            if (magaz != null) {
//                gun.reload(magaz);
//            } else {
//                gun.reload(null);
//            }
//        }
    }

    public void cancelReload(){
        needsReload = false;
        var plHand = (ClientPlayerHandler)playerHandler;
        plHand.cancelReload();
    }

    public <T extends Item> T getItemOfType(Class<T> itemClass){
        for (Item item : getInventoryItems()){
            if (item.getClass().isInstance(itemClass) || item.getClass().equals(itemClass))
                return (T)item;
        }
        return null;
    }

    public void heal(float hp){
        if (hp <= 0)
            return;
        this.hp += hp;
        if (this.hp > maxHp)
            this.hp = maxHp;
    }

    public void autoHeal(){
        if (getHp() == getMaxHp())
            return;
        Meds meds = getItemOfType(Meds.class);
        if (meds != null) {
            meds.use();
            if (debug) {
                HandyHelper.instance.log("[ClientPlayer:autoHeal] Meds used, player hp: " + hp);
            }
        }
        else
            HandyHelper.instance.log("[ClientPlayer:autoHeal] No Meds found");
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

    public float hurt(float damage){
        hp = Math.max(0, hp-damage);
        if (hp == 0)
            kill();
        return hp;
    }
}
