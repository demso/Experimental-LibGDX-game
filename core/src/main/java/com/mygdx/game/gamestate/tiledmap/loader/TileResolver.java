package com.mygdx.game.gamestate.tiledmap.loader;

import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.mygdx.game.SecondGDXGame;

public class TileResolver {
    public static ObjectIntMap<String> tilemapa = new ObjectIntMap<>();
    public static TiledMapTileSets tilesets;
    public final static int NO_TILE_ID = 1035;

    public static TiledMapTile getTile(String id){
        var tile = tilesets.getTile(tilemapa.get(id, NO_TILE_ID));
        if (tile.getId() == NO_TILE_ID)
            SecondGDXGame.helper.log("[Warning] [TileResolver] No tile for name: " + id);
        return tile;
    }
}
