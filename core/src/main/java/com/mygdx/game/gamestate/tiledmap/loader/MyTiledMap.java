package com.mygdx.game.gamestate.tiledmap.loader;

import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.GameState;

public class MyTiledMap extends TiledMap {
    World world;
    public Array<Body> staticObjects = new Array<>();

    MyTiledMap(GameState gameState){
        super();
        this.world = gameState.world;
    }
}
