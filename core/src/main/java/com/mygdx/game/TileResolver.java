package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.utils.ObjectIntMap;

public class TileResolver {
    public static ObjectIntMap<String> tilemapa = new ObjectIntMap<>();
    public static TiledMapTileSets tilesets;
    private final static int NO_TILE_ID = 958;

    public static TiledMapTile getTile(String id){
        return tilesets.getTile(tilemapa.get(id, NO_TILE_ID));
    }
}
