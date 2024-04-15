package com.mygdx.game.tiledmap;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameState;

public class MyTiledMap extends TiledMap {
    World world;
    Array<Body> staticObjects = new Array<>();

    MyTiledMap(GameState gameState){
        super();
        this.world = gameState.world;
    }
}
