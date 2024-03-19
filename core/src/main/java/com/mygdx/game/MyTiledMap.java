package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MyTiledMap extends TiledMap {
    World world;
    Array<Body> staticObjects = new Array<>();
    MyTiledMap(GameScreen gs){
        super();
        this.world = gs.world;
    }
}
