package com.mygdx.game.net.messages;

public class EntityShot {
    public String playerName; // who shot
    public long id;
    public float damage;

    public EntityShot set(long i, float damage, String pn){
        playerName = pn;
        id = i;
        this.damage = damage;
        return this;
    }
}
