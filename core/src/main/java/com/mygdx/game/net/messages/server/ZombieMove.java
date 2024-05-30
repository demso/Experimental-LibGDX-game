package com.mygdx.game.net.messages.server;

import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;

public class ZombieMove extends EntitiesMove{
    public String target;
    public ZombieMove set(long id, String targetName, float x, float y, float xS, float yS){
        this.target = targetName;
        this.id = id;
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        return this;
    }
}
