package com.mygdx.game.gamestate.factories;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.mygdx.game.SecondGDXGame;

public class TileResolver {
    public static ObjectIntMap<String> tilemapa = new ObjectIntMap<>();
    public static TiledMapTileSets tilesets;
    public final static int NO_TILE_ID = 958;

    public static TiledMapTile getTile(String id){
        var tile = tilesets.getTile(tilemapa.get(id, NO_TILE_ID));
        if (tile.getId() == NO_TILE_ID)
            SecondGDXGame.helper.log("[Warning] No tile for name: " + id);
        return tile;
    }
}
