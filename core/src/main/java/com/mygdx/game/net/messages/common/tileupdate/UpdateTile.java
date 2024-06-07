package com.mygdx.game.net.messages.common.tileupdate;

import com.mygdx.game.gamestate.tiledmap.loader.MyTiledMap;
import com.mygdx.game.gamestate.tiledmap.tiled.TiledMapTileLayer;

public abstract class UpdateTile {
    public long sourceId;
    public int x, y;
    public UpdateTile set(long source, int x, int y){
        sourceId = source;
        this.x = x;
        this.y = y;
        return this;
    }
    public abstract void updateTile(MyTiledMap map);
    public Object getData(MyTiledMap map) {
        return ((TiledMapTileLayer)map.getLayers().get("obstacles")).getCell(x,y).getData();
    }
}
