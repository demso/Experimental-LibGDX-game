package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;

class Door {
    TiledMapTileLayer.Cell cell;
    TextureRegion openTexture;
    TextureRegion closedTexture;
    Body physicalBody;
    float x;
    float y;
    boolean isOpen = false;
    Door(TiledMapTileLayer.Cell cell, Body body){
        this.cell = cell;
        physicalBody = body;
    }
}
