package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class CoolCell extends TiledMapTileLayer.Cell {
    Body physicalBody;
    Button actor;
    GameItself gameItself;

    public Button getActor() {
        return actor;
    }

    public void setActor(Button actor) {
        this.actor = actor;
    }

    public Body getPhysicalBody() {
        return physicalBody;
    }

    public void setPhysicalBody(Body physicalBody) {
        this.physicalBody = physicalBody;
    }

}
