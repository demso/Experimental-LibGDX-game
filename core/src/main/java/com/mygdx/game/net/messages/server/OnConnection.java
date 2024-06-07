package com.mygdx.game.net.messages.server;

import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.common.EntityInfo;
import com.mygdx.game.net.messages.common.ZombieInfo;

public class OnConnection {
   public String map;//локальный путь до карты .tmx
   public float spawnX, spawnY;
   public long id;
   public PlayerInfo[] playersInfo;
   public EntityInfo[] entitiesInfo;

   public OnConnection(){}

   public OnConnection(long i, String m,float x, float y){
      id = i;
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

   public OnConnection addEntities(Entity... entities){
      entitiesInfo = new EntityInfo[entities.length];
      for (int i = 0; i < entities.length; i ++){
         if (entities[i] == null)
            continue;
         if (entities[i] instanceof Zombie zombie){
            this.entitiesInfo[i] = new ZombieInfo().set(zombie);
         } else
            this.entitiesInfo[i] = new EntityInfo().set(entities[i]);
      }
      return this;
   }
}
