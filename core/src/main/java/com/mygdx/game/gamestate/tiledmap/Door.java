package com.mygdx.game.gamestate.tiledmap;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.factories.TileResolver;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.bodies.player.Player;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
import com.mygdx.game.gamestate.objects.bodies.userdata.SimpleUserData;
import lombok.Getter;

public class Door implements Interactable, BodyData {
    TiledMapTileLayer.Cell cell;
    TiledMapTile closedTile;
    TiledMapTile openTile;
    Filter closedFilter;
    Filter openFilter;
    Body physicalBody;
    GameState gameState;
    @Getter float x;
    @Getter float y;
    @Getter boolean isOpen = false;
    public Door( TiledMapTileLayer.Cell cell, Body body, String tileName, float x, float y) {
        this.gameState = GameState.Instance;
        this.cell = cell;
        this.openTile = TileResolver.getTile(tileName + "_opened");
        this.closedTile = TileResolver.getTile(tileName + "_closed");
        physicalBody = body;
        this.x = x;
        this.y = y;
        closedFilter = new Filter();
        openFilter = BodyResolver.createFilter((short) (Globals.NONE_CONTACT_FILTER | Globals.PLAYER_INTERACT_CONTACT_FILTER), closedFilter.categoryBits, closedFilter.groupIndex);
    }

    public void open(){
        isOpen = true;
        physicalBody.getFixtureList().get(0).getFilterData().set(openFilter);
        cell.setTile(openTile);
    }

    public void close(){
        isOpen = false;
        physicalBody.getFixtureList().get(0).getFilterData().set(closedFilter);
        cell.setTile(closedTile);
    }

    public void toggle(){
        if (isOpen)
            close();
        else
            open();
    }

    @Override
    public void interact(Player player) {
        toggle();
        player.getBody().getFixtureList().get(0).refilter();
    }

    @Override
    public String getName() {
        return "door at " + x + " " + y;
    }

    @Override
    public Object getData() {
        return this;
    }
}
