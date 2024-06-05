package com.mygdx.game.net.messages;

import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.net.messages.server.EntitiesMove;

public class ZombieInfo extends EntityInfo {
    public float maxSpeed = 2f;
    public long targetId;

    public ZombieInfo set(long id, String name, float hp, float x, float y, float xS, float yS){
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        this.id = id;
        this.type = Entity.Kind.ZOMBIE;
        this.name = name;
        this.hp = hp;
        return this;
    }

    public ZombieInfo set(Zombie zombie) {
        getInfo(zombie, this);
        return this;
    }

    public static ZombieInfo getInfo(Zombie zomb, ZombieInfo zombieInfo) {
        ZombieInfo zi;
        if (zombieInfo == null)
            zi = new ZombieInfo();
        else
            zi = zombieInfo;
        zi.x = zomb.getPosition().x;
        zi.y = zomb.getPosition().y;
        zi.xSpeed = zomb.getVelocity().x;
        zi.ySpeed = zomb.getVelocity().y;
        zi.id = zomb.getId();
        zi.type = Entity.Kind.ZOMBIE;
        zi.name = zomb.getName();
        zi.hp = zomb.getHp();
        zi.maxHp = zomb.getMaxHp();
        zi.isAlive = zomb.isAlive();
        if (zomb.getTarget() != null)
            zi.targetId = zomb.getTarget().getId();
        zi.maxSpeed = zomb.getMaxSpeed();
        return zi;
    }

    /**
     * @deprecated
     * method set() not implemented
     */
    @Override @Deprecated
    public EntityInfo set(long id, Entity.Kind type, String name, float hp, float x, float y, float xS, float yS) {
        throw new RuntimeException("method set() not implemented");
    }

    /**
     * @deprecated
     * method set() not implemented
     */
    @Override @Deprecated
    public EntityInfo set(float x, float y, float xS, float yS) {
        throw new RuntimeException("method set() not implemented");
    }

    /**
     * @deprecated
     * method set() not implemented
     */
    @Override @Deprecated
    public EntityInfo set(Entity entity) {
        throw new RuntimeException("method set() not implemented");
    }

    /**
     * @deprecated
     * method not implemented
     */
    @Override @Deprecated
    public EntitiesMove getMove() {
        throw new RuntimeException("method getMove() not implemented");
    }
}
