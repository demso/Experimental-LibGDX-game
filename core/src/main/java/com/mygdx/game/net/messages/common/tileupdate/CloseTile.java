package com.mygdx.game.net.messages.common.tileupdate;

import com.mygdx.game.gamestate.objects.tiles.interfaces.Closeable;
import com.mygdx.game.gamestate.tiledmap.loader.MyTiledMap;

public class CloseTile extends UpdateTile{
    @Override
    public void updateTile(MyTiledMap map) {
        Object data = getData(map);

        if (data instanceof Closeable closeable) {
            closeable.close();
        }
    }
}
