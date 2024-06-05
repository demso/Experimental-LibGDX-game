package com.mygdx.game.net.messages.server;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;

public class ZombieMove extends EntitiesMove{
    public long targetId;
    public ZombieMove set(long id, long targetId, float x, float y, float xS, float yS){
        this.targetId = targetId;
        this.id = id;
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        return this;
    }

    public ZombieMove set(Zombie entity){
        Vector2 pos = entity.getPosition();
        Vector2 vel = entity.getVelocity();
        set(entity.getId(), entity.getTarget() == null ? 0 :entity.getTarget().getId(), pos.x, pos.y, vel.x, vel.y);
        return this;
    }
}
