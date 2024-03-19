package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;

class Door extends BodyUserData{
    TiledMapTileLayer.Cell cell;
    TiledMapTile closed;
    TiledMapTile open;
    TiledMapTile currentTile;
    Body physicalBody;
    GameItself gameItself;
    float x;
    float y;
    boolean isOpen = false;
    Door(GameItself gameItself, TiledMapTileLayer.Cell cell, Body body, TiledMapTile open, TiledMapTile closed, float x, float y){
        super(cell,"door");
        this.gameItself = gameItself;
        this.cell = cell;
        this.open = open;
        this.closed = closed;
        physicalBody = body;
        this.x = x;
        this.y = y;
    }

    void doAction() {
        isOpen = !isOpen;
        currentTile = isOpen ? open : closed;
        Filter filtr = physicalBody.getFixtureList().get(0).getFilterData();
        if (isOpen)
            filtr.maskBits = 0x0002;
        else
            filtr.maskBits = -1;
        gameItself.game.player.body.getFixtureList().get(0).refilter();
        cell.setTile(currentTile);
    }
}
