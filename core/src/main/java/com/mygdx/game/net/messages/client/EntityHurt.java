package com.mygdx.game.net.messages.client;

public class EntityHurt {
    public long playerId; // who shot
    public long id;
    public float damage;

    public EntityHurt set(long i, float damage, long pi){
        playerId = pi;
        id = i;
        this.damage = damage;
        return this;
    }
}
