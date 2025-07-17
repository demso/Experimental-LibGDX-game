package com.mygdx.game.gamestate.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.items.Meds;
import com.mygdx.game.gamestate.objects.items.grenade.Grenade;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.gamestate.objects.tiles.Storage;
import dev.lyze.gdxUnBox2d.GameObject;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;


public class Player extends Entity implements Storage {

    public enum State {
        Standing, Walking, Running, Sneaking
    }
    public enum Facing {
        Right, Left, Up, Down
    }

    float startX = 21;
    float startY = 87;

    @Getter Player.State state =  Player.State.Walking;
    @Getter @Setter
    Player.Facing facing = Player.Facing.Down;

    public float WIDTH = 0.8f;
    public float HEIGHT = 0.8f;
    public float normalSpeed = 3f;
    Body closestObject;

    Array<Item> inventoryItems = new Array<>();

    public Item equipedItem;

    public GameObject playerObject;
    public ClientPlayerHandler playerHandler;

    float normalSpeedMultiplier = 1;
    float currentSpeedMultiplier = normalSpeedMultiplier;
    float runMultiplier = 1.5f;
    float sneakMultiplier = 0.5f;

    public float itemRotation;

    public boolean needsReload;
    public boolean isReloading;
    public float reloadProgress = 1;

    public float reloadFactor = 1f;

    @Nullable
    public Body getClosestObject(){
        return closestObject;
    }

    @Override
    public void takeItem(Item item){
        item.onTaking(this);
        item.removeFromWorld();
        inventoryItems.add(item);
        GameState.instance.hud.updateInvHUDContent();
    }
    @Override
    public void removeItem(Item item){
        item.onDrop();
        if (equipedItem == item)
            uneqipItem();
        inventoryItems.removeValue(item, true);
        GameState.instance.hud.updateInvHUDContent();
    }

    public boolean throwGrenade(long time){
        Grenade item = getItemOfType(Grenade.class);
        if (item != null) {
            item.fire(time);
            return true;
        }

        return false;
    }
        public void reload(){
        if (equipedItem instanceof Gun gun) {

                needsReload = true;
//            if (magaz != null) {
//                gun.reload(magaz);
//            } else {
//                gun.reload(null);
//            }
        }
    }

    public <T extends Item> T getItemOfType(Class<T> itemClass){
        for (Item item : getInventoryItems()){
            if (item.getClass().isInstance(itemClass) || item.getClass().equals(itemClass))
                return (T)item;
        }
        return null;
    }

    public <T extends Item> Array<T> getItemsOfType(Class<T> itemClass){
        Array<T> tempStorage = new Array<>();
        for (Item item : getInventoryItems()){
            if (item.getClass().isInstance(itemClass) || item.getClass().equals(itemClass))
                tempStorage.add((T)item);
        }

        if (tempStorage.size > 0)
            return tempStorage;
        else
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
        }
        else
            HandyHelper.instance.log("[ClientPlayer:autoHeal] No Meds found");
    }

    @Override
    public Array<Item> getInventoryItems(){
        return inventoryItems;
    }

    @Override
    public void setInventoryItems(Item... items) {
        inventoryItems.clear();
        if (items != null && items.length > 0)
            inventoryItems.addAll(items);
    }

    public boolean inventoryContains(Item item){
        return getInventoryItems().contains(item, true);
    }

    public void equipItem(Item item){
        if (item == null)
            return;
        if (!inventoryContains(item))
            takeItem(item);
        if (equipedItem != null)
            equipedItem.onUnequip();
        equipedItem = item;
        item.onEquip(this);
        HandyHelper.instance.log(item.uid + " equipped by " + this.getName() + " ("+ getId() +")");
    }

    public Item uneqipItem(){
        if (equipedItem == null)
            return null;
        equipedItem.onUnequip();
        Item tmpItem = equipedItem;
        equipedItem = null;
        return tmpItem;
    }

    @Override
    public String getName() {
       return super.getName();
    }

    @Override
    public Object getData() {
        return this;
    }

    @Override
    public void kill() {
        super.kill();
        HandyHelper.instance.log("Oh no im killed!");
    }

    public void revive(){
        setHp(getMaxHp());
        isAlive = true;
        HandyHelper.instance.log("Player revived");
    }

    public boolean fire(){
        if (equipedItem != null && equipedItem instanceof Gun gun) {
            return gun.fireBullet(false);
        }
        return false;
    }

    public Vector2 getVelocity(){
        return getBody().getLinearVelocity();
    }

    public boolean interact() {
        if (closestObject != null) {
            var obj = (Interactable) closestObject.getUserData();
            obj.interact(this);
            return true;
        }
        return false;
    }

    public void dispose() {
        for (Item item : inventoryItems) {
            item.dispose();
        }

        if (equipedItem != null){
            uneqipItem();
        }
        playerObject.destroy();
    }

    @Override
    public String toString() {
        return getName();
    }

    public Vector2 getPosition() {
        return super.getPosition();
    }
}
