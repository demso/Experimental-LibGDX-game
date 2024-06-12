package com.mygdx.game.gamestate.objects.items.grenade;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.objects.bodies.CollisionBehaviour;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class GrenadeHost extends CollisionBehaviour<Grenade> {
    float timeToExplosion;
    public ObjectSet<Entity> nearEntities = new ObjectSet<>();

    public GrenadeHost(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
        preCol(contact);
        if (thisFixture.isSensor() && otherUserData instanceof Entity entity) {
            nearEntities.add(entity);
        }
    }

    @Override
    public void onCollisionExit(Behaviour other, Contact contact) {
        preCol(contact);
        if (thisFixture.isSensor() && otherUserData instanceof Entity entity) {
            nearEntities.remove(entity);
        }
    }

    @Override
    public void fixedUpdate() {
        super.fixedUpdate();
        timeToExplosion -= getUnBox().getOptions().getTimeStep();
        Grenade grenade = ((Grenade)(getGameObject().getBox2dBehaviour().getBody().getUserData()));
        grenade.timeToDetonation = timeToExplosion;

        if (timeToExplosion <= 0) {
            detonation();
            grenade.onDetonation();
            GameState.instance.client.disposeItem(grenade);
        }
    }

    float fract = 1;
    Vector2 vec = new Vector2();
    //ArrayMap<Entity, Float> entitiesOnRay = new Array<>();
    public void detonation(){
        Grenade grenade = ((Grenade)(getGameObject().getBox2dBehaviour().getBody().getUserData()));
        World world = grenade.physicalBody.getWorld();
        nearEntities.forEach(entity -> {
            world.rayCast(
                    (fixture, point, normal, fraction) -> {
                        if (((fixture.getFilterData().maskBits & (Globals.DEFAULT_CONTACT_FILTER)) == 0) || fixture.isSensor())
                            return -1;
                        Object data = fixture.getBody().getUserData();
                        if (data == entity) {
                            fract = fraction;
                        } else if (data instanceof Entity) {
                            fract = fraction +  0.1f + (float) Math.random() * 0.3f;
                        };
                        return fraction;
                    },
                    grenade.physicalBody.getPosition(),
                    new Vector2(grenade.physicalBody.getPosition()).add( new Vector2(entity.getPosition()).sub(grenade.physicalBody.getPosition()).nor().scl(3)));
            float damage = Math.max(0, 1 - fract) * grenade.damage;
            entity.hurt(damage);
            GameState.instance.client.entityHurt(entity, damage);
        });
    }

    public void thrown(float time){
        timeToExplosion = time;
    }
}
