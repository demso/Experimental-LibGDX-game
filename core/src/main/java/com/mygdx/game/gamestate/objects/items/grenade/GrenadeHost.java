package com.mygdx.game.gamestate.objects.items.grenade;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
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
    HashMap<Body, ContactData> nearEntities = new HashMap<>();

    public GrenadeHost(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
        preCol(contact);
        if (thisFixture.isSensor() && otherUserData instanceof Entity entity) {
            nearEntities.put(entity.getBody(), new ContactData(entity.getBody(), 0));
        }
    }

    @Override
    public void onCollisionExit(Behaviour other, Contact contact) {
        preCol(contact);
        if (thisFixture.isSensor() && otherUserData instanceof Entity entity) {
            nearEntities.remove(entity.getBody());
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
        }
    }

    int rayNum = 50;
    public Array<Vector2> rayEnds = new Array<>();
    public void detonation(){
        Grenade grenade = ((Grenade)(getGameObject().getBox2dBehaviour().getBody().getUserData()));
        World world = grenade.physicalBody.getWorld();

        float anglePart = 360f / rayNum;
        for (int i = 0; i < rayNum; i++) {
            Vector2 endVec = new Vector2((float) Math.cos(Math.toRadians(anglePart * i)), (float) Math.sin(Math.toRadians(anglePart * i)));
            rayEnds.add(endVec.scl(grenade.radius));
        }
        GameState.instance.rayEnds = new Array<>(rayEnds);
        GameState.instance.grPos.set(grenade.physicalBody.getPosition());

        HashMap<Body,ContactData> colliders = new HashMap<>();
        for (Vector2 pos : rayEnds) {
            colliders.clear();
            world.rayCast(
                    (fixture, point, normal, fraction) ->
                    {
                        if (fixture.isSensor()) return 1;

                        Body body = fixture.getBody();
                        ContactData contactData = colliders.get(body);
                        if (contactData != null) {
                            if (contactData.fraction > fraction || contactData.fraction == 0) colliders.put(body, new ContactData(body, fraction));
                        } else {
                            colliders.put(body, new ContactData(fixture.getBody(), fraction));
                        }
                        return 1;
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
                if (contact.body.getUserData() instanceof Entity) {
                    ContactData existingData = nearEntities.get(contact.body);
                    if (existingData == null || existingData.fraction > contact.fraction) {
                        contact.position = i;
                        nearEntities.put(contact.body, contact);
                    }
                }
            }
        }

        ContactData[] contacts = nearEntities.values().toArray(new ContactData[0]);
        for (int i = 0; i < contacts.length; i++) {
            ContactData contact = contacts[i];
            Entity entity = (Entity) contact.body.getUserData();
            float fract = contact.fraction;
            float damage = Math.max(0, 1 - (fract + (0.1f  + (float) Math.random() * 0.2f) * contact.position )) * grenade.damage;
            //float damage = Math.max(0, 1 - (fract)) * grenade.damage;
            HandyHelper.instance.log("[GrenadeHost:detonation] n: " + entity.getName() + " d: " + Utils.round(damage,1) + " f: " + Utils.round(1 - fract, 1) + " p: " + contact.position);
            entity.hurt(damage);
        }

        nearEntities.clear();
        //GameState.instance.client.localGrenades.remove(grenade);
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);

    }

    public void thrown(float time){
        timeToExplosion = time;
    }
}


