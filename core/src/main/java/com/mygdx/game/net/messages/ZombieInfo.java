package com.mygdx.game.net.messages;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.factories.MobsFactoryC;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.server.ZombieMove;

public class ZombieInfo extends EntityInfo {
    public float maxSpeed = 2f;
    public String target;

    public ZombieInfo set(long id, MobsFactoryC.Type type, String name, float hp, float x, float y, float xS, float yS){
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        this.id = id;
        this.type = type;
        this.name = name;
        this.hp = hp;
        return this;
    }

    public ZombieInfo setInfoFromZombie(Zombie zombie) {
        set(zombie.getPosition().x, zombie.getPosition().y,
                zombie.getBody().getLinearVelocity().x, zombie.getBody().getLinearVelocity().y);
        return this;
    }
}
