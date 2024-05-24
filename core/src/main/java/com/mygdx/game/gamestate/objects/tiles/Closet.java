package com.mygdx.game.gamestate.objects.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.items.SimpleItem;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;

import static com.mygdx.game.gamestate.GameState.instance;

public class Closet implements BodyData, Interactable, Storage {
    TiledMapTileLayer.Cell cell;
    Array<SimpleItem> inventoryItems = new Array<>();
    Body body;

    public Closet(TiledMapTileLayer.Cell cell, Body body){
        this.cell = cell;
        this.body = body;
        inventoryItems.add(new SimpleItem("deagle_44", "Deagle .44"));
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
    public Array<SimpleItem> getInventoryItems() {
        return inventoryItems;
    }

    @Override
    public void takeItem(SimpleItem item){
        item.removeFromWorld();
        inventoryItems.add(item);
        instance.hud.updateInvHUDContent();
    }
    @Override
    public void dropItem(SimpleItem item){
        inventoryItems.removeValue(item, true);
        instance.hud.updateInvHUDContent();
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }
}
