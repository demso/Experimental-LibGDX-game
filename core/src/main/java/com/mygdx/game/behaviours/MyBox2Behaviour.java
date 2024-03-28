package com.mygdx.game.behaviours;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import dev.lyze.gdxUnBox2d.BodyDefType;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class MyBox2Behaviour extends Box2dBehaviour {
    public MyBox2Behaviour(BodyDefType type, GameObject gameObject) {
        super(type, gameObject);
    }

    public MyBox2Behaviour(BodyDef bodyDef, GameObject gameObject) {
        super(bodyDef, gameObject);
    }

    public MyBox2Behaviour(Body body, GameObject gameObject) {
        super(body, gameObject);
    }

    @Override
    public void awake() {
        super.awake();
        getBody().setActive(true);
    }
}
