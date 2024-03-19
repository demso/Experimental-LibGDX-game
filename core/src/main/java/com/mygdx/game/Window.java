package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;

public class Window extends BodyUserData{
    TiledMapTileLayer.Cell cell;
    TiledMapTile closed;
    TiledMapTile open;
    TiledMapTile currentTile;
    Body physicalBody;
    GameScreen game;
    float x;
    float y;
    boolean isOpen = false;
    Window(GameScreen game, TiledMapTileLayer.Cell cell, Body body, TiledMapTile open, TiledMapTile closed, float x, float y){
        super(cell,"window");
        this.game = game;
        this.cell = cell;
        this.open = open;
        this.closed = closed;
        physicalBody = body;
    }

    void doAction() {
        isOpen = !isOpen;
        currentTile = isOpen ? open : closed;
        Filter filtr = physicalBody.getFixtureList().get(0).getFilterData();
        if (isOpen)
            filtr.maskBits = 0x0002;
        else
            filtr.maskBits = -1;
        game.player.body.getFixtureList().get(0).refilter();
        cell.setTile(currentTile);
    }
}
