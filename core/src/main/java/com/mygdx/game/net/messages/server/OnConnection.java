package com.mygdx.game.net.messages.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.tiledmap.loader.MyTiledMap;

public class OnConnection {
   public String map;
   public Vector2 spawnPoint;
}
