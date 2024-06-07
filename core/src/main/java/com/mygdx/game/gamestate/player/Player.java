package com.mygdx.game.gamestate.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.gamestate.objects.tiles.Storage;
import dev.lyze.gdxUnBox2d.GameObject;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import static com.mygdx.game.gamestate.GameState.instance;

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
    public PlayerMoveReceiver playerHandler;

    float normalSpeedMultiplier = 1;
    float currentSpeedMultiplier = normalSpeedMultiplier;
    float runMultiplier = 1.5f;
    float sneakMultiplier = 0.5f;

    public float itemRotation;

    @Nullable
    public Body getClosestObject(){
        return closestObject;
    }

    @Override
    public void takeItem(Item item){
        item.onTaking(this);
        item.removeFromWorld();
        inventoryItems.add(item);
        instance.items.put(item.uid, item);
        instance.hud.updateInvHUDContent();
    }
    @Override
    public void removeItem(Item item){
        item.onUnequip();
        if (equipedItem == item)
            equipedItem = null;
        inventoryItems.removeValue(item, true);
        instance.items.remove(item.uid);
        instance.hud.updateInvHUDContent();
    }
    @Override
    public Array<Item> getInventoryItems(){
        return inventoryItems;
    }

    @Override
    public void setInventoryItems(Item... items) {
        inventoryItems.clear();
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
        if (item instanceof Gun gun){
            gun.onEquip(this);
        }
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
        SecondGDXGame.instance.helper.log("Oh no im killed!");
    }

    public void revive(){
        setHp(getMaxHp());
        isAlive = true;
        SecondGDXGame.instance.helper.log("Player revived");
    }

    public void fire(){
        if (equipedItem != null && equipedItem instanceof Gun gun)
            gun.fireBullet(this);
    }

    public Vector2 getVelocity(){
        return getBody().getLinearVelocity();
    }

    public boolean interact(){
        if (closestObject != null) {
            var obj = (Interactable) closestObject.getUserData();
            obj.interact(this);
            return true;
        }
        return false;
    }

    public void dispose() {

        if (equipedItem != null){
            uneqipItem();
            equipedItem.dispose();
        }
        playerObject.destroy();
    }


    public Vector2 getPosition() {
        return super.getPosition();
    }
}
