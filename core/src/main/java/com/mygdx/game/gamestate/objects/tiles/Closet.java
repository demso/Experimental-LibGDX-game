package com.mygdx.game.gamestate.objects.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
import com.mygdx.game.gamestate.tiledmap.tiled.*;

import static com.mygdx.game.gamestate.GameState.instance;

public class Closet implements BodyData, Interactable, Storage {
    com.mygdx.game.gamestate.tiledmap.tiled.TiledMapTileLayer.Cell cell;
    Array<Item> inventoryItems = new Array<>();
    Body body;

    public Closet(TiledMapTileLayer.Cell cell, Body body){
        this.cell = cell;
        this.body = body;
    }

    @Override
    public void interact(Player player) {
        boolean offPlayersInv = !Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);
        GameState.instance.hud.toggleStorageInventoryHUD(this, offPlayersInv);
    }

    @Override
    public String getName() {
        return cell.getTile().getProperties().get("name", String.class);
    }

    @Override
    public Object getData() {
        return this;
    }

    @Override
    public Array<Item> getInventoryItems() {
        return inventoryItems;
    }

    @Override
    public void takeItem(Item item){
        item.removeFromWorld();
        inventoryItems.add(item);
        instance.hud.updateInvHUDContent();
    }
    @Override
    public void dropItem(Item item){
        inventoryItems.removeValue(item, true);
        instance.hud.updateInvHUDContent();
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }
}
