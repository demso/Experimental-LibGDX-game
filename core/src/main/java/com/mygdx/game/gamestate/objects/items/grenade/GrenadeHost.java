package com.mygdx.game.gamestate.objects.items.grenade;

import com.badlogic.gdx.physics.box2d.Contact;
import com.mygdx.game.gamestate.objects.bodies.CollisionBehaviour;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class GrenadeHost extends CollisionBehaviour<Grenade> {
    float timeToExplosion;

    public GrenadeHost(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void onCollisionEnter(Behaviour other, Contact contact) {
        preCol(contact);
        if (thisFixture.isSensor() && otherUserData instanceof Entity entity) {
            ((Grenade)getGameObject().getBox2dBehaviour().getBody().getUserData()).nearEntities.add(entity);
        }
    }

    @Override
    public void onCollisionExit(Behaviour other, Contact contact) {
        preCol(contact);
        if (thisFixture.isSensor() && otherUserData instanceof Entity entity) {
            ((Grenade)getGameObject().getBox2dBehaviour().getBody().getUserData()).nearEntities.remove(entity);
        }
    }

    @Override
    public void fixedUpdate() {
        super.fixedUpdate();
        timeToExplosion -= getUnBox().getOptions().getTimeStep();
        Grenade grenade = ((Grenade)(getGameObject().getBox2dBehaviour().getBody().getUserData()));
        grenade.timeToDetonation = timeToExplosion;

        if (timeToExplosion <= 0) {
            grenade.detonation();
        }
    }

    public void thrown(float time){
        timeToExplosion = time;
    }
}
