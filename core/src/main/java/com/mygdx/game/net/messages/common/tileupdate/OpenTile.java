package com.mygdx.game.net.messages.common.tileupdate;

import com.mygdx.game.gamestate.objects.tiles.interfaces.Openable;
import com.mygdx.game.gamestate.tiledmap.loader.MyTiledMap;
import com.mygdx.game.gamestate.tiledmap.tiled.TiledMapTileLayer;

public class OpenTile extends UpdateTile{

    @Override
    public void updateTile(MyTiledMap map) {
        Object data = getData(map);

        if (data instanceof Openable openable) {
            openable.open();
        }
    }
}
