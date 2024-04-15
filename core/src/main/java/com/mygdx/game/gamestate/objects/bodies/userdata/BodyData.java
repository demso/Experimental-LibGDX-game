package com.mygdx.game.gamestate.objects.bodies.userdata;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

public interface BodyData {
    public String getName();
    public Object getData();
}
