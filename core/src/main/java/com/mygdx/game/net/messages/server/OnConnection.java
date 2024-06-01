package com.mygdx.game.net.messages.server;

import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.EntityInfo;

public class OnConnection {
   public String map;//локальный путь до карты .tmx
   public float spawnX, spawnY;
   public PlayerInfo[] playersInfo;
   public EntityInfo[] entitiesInfo;

   public OnConnection(){}

   public OnConnection(String m,float x, float y){
      spawnX = x;
      spawnY = y;
      map = m;
   }

   public OnConnection addPlayers(PlayerInfo... players){
      this.playersInfo = new PlayerInfo[players.length];
      for (int i = 0; i < players.length; i ++){
         if (players[i] == null)
            continue;
         this.playersInfo[i] = players[i];
      }
      return this;
   }

   public OnConnection addEntities(EntityInfo... entities){
      entitiesInfo = new EntityInfo[entities.length];
      for (int i = 0; i < entities.length; i ++){
         if (entities[i] == null)
            continue;
         this.entitiesInfo[i] = entities[i];
      }
      return this;
   }
}
