package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.tiledmap.BodyUserName;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

import static net.dermetfan.gdx.physics.box2d.Box2DUtils.*;

public class CustomBox2DSprite extends Box2DSprite implements BodyUserName {
    String name;
    boolean originalDrawOnBody = false;

    public CustomBox2DSprite(TextureRegion textureRegion, String name){
        super(textureRegion);
        this.name = name;
    }

    @Override
    public void draw(Batch batch, Body body) {
        if (originalDrawOnBody)
            super.draw(batch, body);
        else {
            setCenter(body.getPosition().x, body.getPosition().y);
            draw(batch);
        }
    }
    @Override
    public void setName(String name){
        this.name = name;
    };
    @Override
    public String getName(){
        return name;
    };
}
