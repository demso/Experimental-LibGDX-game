package com.mygdx.game.gamestate.tiledmap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.UI.inventory.HasInventory;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.Item;
import com.mygdx.game.gamestate.objects.bodies.player.Player;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;

public class Closet implements BodyData, Interactable, HasInventory {

    Array<Item> inventoryItems = new Array<>();
    Body body;

    public Closet(){

    }

    @Override
    public void interact(Player player) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public Array<Item> getInventoryItems() {
        return inventoryItems;
    }

    @Override
    public void removeItemFromInventory(Item item) {
        inventoryItems.removeValue(item, true);
    }

    @Override
    public void addItemToInventory(Item item) {
        inventoryItems.add(item);
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }
}
