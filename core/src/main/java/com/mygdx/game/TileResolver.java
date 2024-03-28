package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.utils.ObjectIntMap;

public class TileResolver {
    static ObjectIntMap<String> tilemapa;
    static TiledMapTileSets tilesets;
    private final static int NO_TILE_ID = 958;
    static boolean inited = false;

    public static void init(ObjectIntMap<String> tm, TiledMapTileSets ts){
        TileResolver.tilemapa = tm;
        TileResolver.tilesets = ts;
        inited = true;
    }

    public static TiledMapTile getTile(String id){
        if (!isInited()) return tilesets.getTile(NO_TILE_ID);
        return tilesets.getTile(tilemapa.get(id, NO_TILE_ID));
    }

    private static boolean isInited(){
        if (!inited){
            SecondGDXGame.helper.log("[TileResolver] TileResolver not initialised.");
            return false;
        }
        return true;
    }
}
