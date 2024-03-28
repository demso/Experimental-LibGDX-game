package com.mygdx.game.behaviours;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.mygdx.game.UserName;
import dev.lyze.gdxUnBox2d.Behaviour;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class AdditionalBodyInfoBehaviour extends BehaviourAdapter {
    boolean isBullet, isRotating;
    MassData massData;
    UserName userData;
    public AdditionalBodyInfoBehaviour(GameObject gameObject, boolean isBullet, boolean isRotating, MassData massData, UserName userData){
        super(gameObject);
        this.isBullet = isBullet;
        this.isRotating = isRotating;
        this.massData = massData;
        this.userData = userData;
    }

    @Override
    public void awake() {
        super.awake();
        Body body = getGameObject().getBox2dBehaviour().getBody();
        body.setBullet(isBullet);
        body.setFixedRotation(isRotating);
        body.setUserData(userData);
        body.setMassData(massData);
    }
}
