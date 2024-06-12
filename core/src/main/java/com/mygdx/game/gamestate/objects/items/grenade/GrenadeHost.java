package com.mygdx.game.gamestate.objects.items.grenade;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.objects.bodies.CollisionBehaviour;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

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

    Vector2 vec = new Vector2();
    ArrayMap<Entity, Float> entitiesOnRay = new ArrayMap<>();
    int rayNum = 15;
    Array<Vector2> rayEnds = new Array<>();
    public void detonation(){
        Grenade grenade = ((Grenade)(getGameObject().getBox2dBehaviour().getBody().getUserData()));
        World world = grenade.physicalBody.getWorld();
        entitiesOnRay.clear();


        float anglePart = 360f / rayNum;
        for (int i = 0; i < rayNum; i++) {
            Vector2 endVec = new Vector2((float) Math.cos(Math.toRadians(anglePart * i)), (float) Math.sin(Math.toRadians(anglePart * i)));
            rayEnds.add(endVec.scl(grenade.radius));
        }

        for (Vector2 pos : rayEnds) {
            world.rayCast(
                    (fixture, point, normal, fraction) -> {
                        if (((fixture.getFilterData().maskBits & (Globals.DEFAULT_CONTACT_FILTER)) == 0) || fixture.isSensor())
                            return -1;
                        Object data = fixture.getBody().getUserData();
                        if (data instanceof Entity entity1) {
                            if (fraction < entitiesOnRay.get(entity1, 1f) )
                                entitiesOnRay.put(entity1, fraction);
                        }
                        return 1;
                    },
                    grenade.physicalBody.getPosition(),
                    new Vector2(grenade.physicalBody.getPosition()).add(pos)
            );
        }

        Vector2 addPos1 = new Vector2(grenade.physicalBody.getPosition());
        addPos1.y += grenade.radius;
        Vector2 addPos2 = new Vector2(grenade.physicalBody.getPosition());
        addPos2.y -= grenade.radius;
        world.rayCast(
                (fixture, point, normal, fraction) -> {
                    if (((fixture.getFilterData().maskBits & (Globals.DEFAULT_CONTACT_FILTER)) == 0) || fixture.isSensor())
                        return -1;
                    Object data = fixture.getBody().getUserData();
                    if (data instanceof Entity entity1) {
                        if (fraction < entitiesOnRay.get(entity1, 1f) )
                            entitiesOnRay.put(entity1, fraction);
                    }
                    return 1;
                },
                addPos1,
                addPos2
        );

        for (int i = 0; i < entitiesOnRay.size; i++) {
            Entity entity = entitiesOnRay.getKeyAt(i);
            float fract = entitiesOnRay.getValueAt(i);
            float damage = Math.max(0, 1 - (fract + 0.1f * i + (float) Math.random() * 0.3f * i)) * grenade.damage;
            HandyHelper.instance.log("[GrenadeHost:detonation] Damage for " + entity.getName() + " " + damage);
            entity.hurt(damage);
            GameState.instance.client.entityHurt(entity, damage);
        }

        GameState.instance.client.localGrenades.remove(grenade);
    }

    public void thrown(float time){
        timeToExplosion = time;
    }
}
