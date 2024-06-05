package com.mygdx.game.net.messages.client;

public class EntityShot {
    public long playerId; // who shot
    public long id;
    public float damage;

    public EntityShot set(long i, float damage, long pi){
        playerId = pi;
        id = i;
        this.damage = damage;
        return this;
    }
}
