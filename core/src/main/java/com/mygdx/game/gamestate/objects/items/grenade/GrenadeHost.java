package com.mygdx.game.gamestate.objects.items.grenade;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import com.mygdx.game.Utils;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.objects.bodies.CollisionBehaviour;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.userdata.SimpleUserData;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import java.util.Arrays;
import java.util.HashMap;

public class GrenadeHost extends CollisionBehaviour<Grenade> {
    float timeToExplosion;
    Array<ContactData> nearEntities = new Array<>();
    ObjectSet<Entity> bodiesInEpicenter= new ObjectSet<>();

    public GrenadeHost(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
        preCol(contact);
        if (thisFixture.isSensor() && otherUserData instanceof Entity entity) {
            bodiesInEpicenter.add(entity);
        }
    }

    @Override
    public void onCollisionExit(Behaviour other, Contact contact) {
        preCol(contact);
        if (thisFixture.isSensor() && otherUserData instanceof Entity entity) {
            bodiesInEpicenter.remove(entity);
        }
    }

    class ContactData{
        Body body;
        float fraction;
        int position;
        ContactData(Body body, float fraction){
            this.body = body;
            this.fraction = fraction;
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

    int rayNum = 30;
    Array<Vector2> rayEnds = new Array<>();
    public void detonation(){
        Grenade grenade = ((Grenade)(getGameObject().getBox2dBehaviour().getBody().getUserData()));
        World world = grenade.physicalBody.getWorld();
        nearEntities.clear();

        float anglePart = 360f / rayNum;
        for (int i = 0; i < rayNum; i++) {
            Vector2 endVec = new Vector2((float) Math.cos(Math.toRadians(anglePart * i)), (float) Math.sin(Math.toRadians(anglePart * i)));
            rayEnds.add(endVec.scl(grenade.radius));
        }

        HashMap<Body,ContactData> colliders= new HashMap<>();
        for (Vector2 pos : rayEnds) {
            colliders.clear();
            world.rayCast(
                    (fixture, point, normal, fraction) ->
                    {
                        Body body = fixture.getBody();
                        ContactData contactData = colliders.get(body);
                        if (contactData != null) {
                            if (contactData.fraction > fraction) colliders.put(body, new ContactData(body, fraction));
                        } else {
                            colliders.put(body, new ContactData(fixture.getBody(), fraction));
                        }
                        return fraction;
                    },
                    grenade.physicalBody.getPosition(),
                    new Vector2(grenade.physicalBody.getPosition()).add(pos)
            );


            ContactData[] contacts = colliders.values().toArray(new ContactData[0]);
            Arrays.sort(contacts, (o1, o2) -> {
                int ret = 0;
                if ( o1.fraction > o2.fraction)
                    ret = 1;
                else if (o1.fraction < o2.fraction)
                    ret = -1;
                return ret;
            });

            for (int i = 0; i < contacts.length; i++) {
                ContactData contact = contacts[i];
                Fixture fixture = contact.body.getFixtureList().first();
                if (((fixture.getFilterData().categoryBits & (Globals.DEFAULT_CONTACT_FILTER)) == 1) || fixture.isSensor())
                    break;
                if (contact.body.getUserData() instanceof Entity)
                    nearEntities.add(contact);
            }
        }

        for (Entity entity : new ObjectSet.ObjectSetIterator<>(bodiesInEpicenter)) {
            boolean skip = false;
            for (int j = 0; j < nearEntities.size; j++) {
                ContactData cd = nearEntities.get(j);
                if (cd.body == entity.getBody()) {
                    skip = true;
                    break;
                }
            }
            if (skip)
                continue;

            nearEntities.add(new ContactData(entity.getBody(), 0));
        }

        for (int i = 0; i < nearEntities.size; i++) {
            ContactData contact = nearEntities.get(i);
            Entity entity = (Entity) contact.body.getUserData();
            float fract = contact.fraction;
            //float damage = Math.max(0, 1 - (fract + 0.1f * contact.position + (float) Math.random() * 0.3f * contact.position)) * grenade.damage;
            float damage = Math.max(0, 1 - (fract)) * grenade.damage;
            HandyHelper.instance.log("[GrenadeHost:detonation] Damaged " + entity.getName() + " " + Utils.round(damage,1));
            entity.hurt(damage);
            GameState.instance.client.entityHurt(entity, damage);
        }

        GameState.instance.client.localGrenades.remove(grenade);
    }

    public void thrown(float time){
        timeToExplosion = time;
    }
}

class RCCallback implements RayCastCallback {
    @Override
    final public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if (fixture.getBody().getUserData().getClass().equals(SimpleUserData.class) || ((fixture.getFilterData().categoryBits & (Globals.DEFAULT_CONTACT_FILTER)) == 1) || fixture.isSensor())
            return -1;
        Object data = fixture.getBody().getUserData();
        //do some other stuff
        return fraction;
    }
}
