package com.mygdx.game.gamestate.player;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.gamestate.objects.tiles.Storage;
import dev.lyze.gdxUnBox2d.GameObject;
import lombok.Getter;
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
    @Getter Player.Facing facing = Player.Facing.Down;

    public float WIDTH = 0.8f;
    public float HEIGHT = 0.8f;
    public float normalSpeed = 3f;
    public Body closestObject;

    Array<Item> inventoryItems = new Array<>();

    public Item equipedItem;

    public GameObject playerObject;
    public PlayerMoveReceiver playerHandler;

    float normalSpeedMultiplier = 1;
    float currentSpeedMultiplier = normalSpeedMultiplier;
    float runMultiplier = 1.5f;
    float sneakMultiplier = 0.5f;

    @Nullable
    public Body getClosestObject(){
        return closestObject;
    }

    @Override
    public void takeItem(Item item){
        item.removeFromWorld();
        inventoryItems.add(item);
        instance.hud.updateInvHUDContent();
    }
    @Override
    public void dropItem(Item item){
        item.unequip();
        if (equipedItem == item)
            equipedItem = null;
        inventoryItems.removeValue(item, true);
        instance.hud.updateInvHUDContent();
    }
    @Override
    public Array<Item> getInventoryItems(){
        return inventoryItems;
    }

    public boolean inventoryContains(Item item){
        return getInventoryItems().contains(item, true);
    }

    public void equipItem(Item item){
        if (!inventoryContains(item))
            takeItem(item);
        if (equipedItem != null)
            equipedItem.unequip();
        equipedItem = item;
        if (item instanceof Gun gun){
            gun.equip(this);
        }
    }

    public void uneqipItem(){
        equipedItem.unequip();
        equipedItem = null;
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

    public void fire(){
        if (equipedItem != null && equipedItem instanceof Gun gun)
            gun.fireBullet(this);
    }

    public void setPosition(float x, float y){
        getBody().setTransform(x, y, getBody().getTransform().getRotation());
    }
}
