package com.mygdx.game.net.messages.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gamestate.tiledmap.loader.MyTiledMap;
import com.mygdx.game.net.PlayerInfo;

public class OnConnection {
   public String map;//локальный путь до карты .tmx
   public float spawnX, spawnY;
   public PlayerInfo[] players;

   public OnConnection(){}

   public OnConnection(String m,float x, float y){
      spawnX = x;
      spawnY = y;
      map = m;
   }

   public OnConnection addPlayers(PlayerInfo... players){
      this.players = new PlayerInfo[players.length];
      for (int i = 0; i < players.length; i ++){
         if (players[i] == null)
            continue;
         this.players[i] = players[i];
      }
      return this;
   }
}
