package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.tiledmap.SimpleUserData;
import com.mygdx.game.tiledmap.UserData;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

public class CustomBox2DSprite extends Box2DSprite implements UserData {
    String name;
    Object data;
    boolean originalDrawOnBody = false;

    public CustomBox2DSprite(TextureRegion textureRegion, String name){
        super(textureRegion);
        this.name = name;
    }

    public CustomBox2DSprite(TextureRegion textureRegion, String name, float width, float height){
        super(textureRegion);
        this.name = name;
        setSize(width, height);
    }

    public CustomBox2DSprite(TextureRegion textureRegion, String name, Object data){
        super(textureRegion);
        this.name = name;
        this.data = data;
    }

    public CustomBox2DSprite(TextureRegion textureRegion, String name, Object data, float width, float height){
        super(textureRegion);
        this.name = name;
        this.data = data;
        setSize(width, height);
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

    public void setName(String name){
        this.name = name;
    };
    @Override
    public String getName(){
        return name;
    }

    @Override
    public Object getData() {
        return data;
    }
}
