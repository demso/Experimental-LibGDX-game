package com.mygdx.game.gamestate.tiledmap.loader;

import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.GameState;

public class MyTiledMap extends TiledMap {
    public transient World world;
    public transient Array<Body> staticObjects = new Array<>();

    public MyTiledMap(World world){
        super();
        this.world = world;
    }
}
