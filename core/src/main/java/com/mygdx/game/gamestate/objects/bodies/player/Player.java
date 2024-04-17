package com.mygdx.game.gamestate.objects.bodies.player;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.Item;
import dev.lyze.gdxUnBox2d.GameObject;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public class Player extends Entity {

    public enum State {
        Standing, Walking, Running, Sneaking
    }
    public enum Facing {
        Right, Left, Up, Down
    }

    @Getter Player.State state =  Player.State.Walking;
    @Getter Player.Facing facing = Player.Facing.Down;

    public float WIDTH = 0.8f;
    public float HEIGHT = 0.8f;
    public float normalSpeed = 2.5f;
    public Body closestObject;

    Array<Item> inventoryItems = new Array<>();
    public Item equipedItem;

    public GameObject playerObject;

    float normalSpeedMultiplier = 1;
    float currentSpeedMultiplier = normalSpeedMultiplier;
    float runMultiplier = 1.5f;
    float sneakMultiplier = 0.5f;

    @Nullable
    public Body getClosestObject(){
        return closestObject;
    }
    public void pickupItem(Item item){
        item.removeFromWorld();
        addItemToInventory(item);
    }
    public void addItemToInventory(Item item){
        inventoryItems.add(item);
    }
    public void removeItemFromInventory(Item item){
        if (equipedItem == item)
            equipedItem = null;
        inventoryItems.removeValue(item, true);
    }
    public Array<Item> getInventoryItems(){
        return inventoryItems;
    }
    public void equipItem(Item item){
        equipedItem = item;
    }
    public Item freeHands(){
        Item item = equipedItem;
        equipedItem = null;
        return item;
    }

    @Override
    public String getName() {
        return "player";
    }

    @Override
    public Object getData() {
        return this;
    }

    @Override
    public void kill() {
        super.kill();
        SecondGDXGame.helper.log("Oh no im killed!");
    }

    public void revive(){
        setHp(getMaxHp());
        isAlive = true;
        SecondGDXGame.helper.log("Player revived");
    }
}
