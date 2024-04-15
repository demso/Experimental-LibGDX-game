package com.mygdx.game.tiledmap;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.game.BodyData;

public class SimpleUserData implements BodyData {
    Object data;
    public String bodyName;
    public SimpleUserData(Object d, String n){
        data = d;
        setName(n);
    }

    public SimpleUserData(String n){
        setName(n);
    }

    public void setName(String name) {
        bodyName = name;
    }

    @Override
    public String getName() {
        return bodyName;
    }

    @Override
    public Object getData() {
        return data;
    }
}
