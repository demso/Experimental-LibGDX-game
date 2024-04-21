package com.mygdx.game.gamestate.objects.tiles;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.bodies.player.Player;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
import lombok.Getter;

public class Door implements Interactable, BodyData {
    TiledMapTileLayer.Cell cell;
    TiledMapTile closedTile;
    TiledMapTile openTile;
    Filter closedFilter;
    Filter openFilter;
    Body physicalBody;
    GameState gameState;
    boolean isBoarded;
    public boolean peep;
    @Getter boolean isOpen = false;
    public Door( TiledMapTileLayer.Cell cell, Body body) {
        this.gameState = GameState.Instance;
        this.cell = cell;
        physicalBody = body;
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

    public void board(){
        isBoarded = true;
    }

    public void unBoard(){
        isBoarded = false;
    }

    @Override
    public void interact(Player player) {
        toggle();
        player.getBody().getFixtureList().get(0).refilter();
    }

    public TiledMapTile getTile(){
        return cell.getTile();
    }

    @Override
    public String getName() {
        return getTile().getProperties().get("name", String.class);
    }

    @Override
    public Object getData() {
        return this;
    }

    public Vector2 getPosititon(){
        return physicalBody.getPosition();
    }
}
