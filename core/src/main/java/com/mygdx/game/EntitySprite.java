package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EntitySprite extends CustomBox2DSprite{
    Entity entity;
    public EntitySprite(TextureRegion textureRegion, String name, Entity entity) {
        super(textureRegion, name);
        this.entity = entity;
    }

    public EntitySprite(TextureRegion textureRegion, String name, Entity entity, float width, float height) {
        super(textureRegion, name, width, height);
        this.entity = entity;
    }

    @Override
    public Object getData() {
        return entity;
    }
}
