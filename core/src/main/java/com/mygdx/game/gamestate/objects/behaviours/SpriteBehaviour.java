package com.mygdx.game.gamestate.objects.behaviours;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.SecondGDXGame;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;
import lombok.Getter;
import lombok.Setter;

public class SpriteBehaviour extends BehaviourAdapter {
    protected float offsetX;
    protected float offsetY;
    @Getter @Setter protected Sprite sprite;
    protected final Vector2 position = new Vector2();

    public SpriteBehaviour(GameObject gameObject, TextureRegion textureRegion, float renderOrder) {
        super(gameObject);
        setRenderOrder(renderOrder);
        sprite = new Sprite(textureRegion);
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        setOffset(-sprite.getWidth()/2f, -sprite.getHeight()/2f);
    }

    public SpriteBehaviour(GameObject gameObject, float width, float height, TextureRegion textureRegion, float renderOrder) {
        super(gameObject);
        setRenderOrder(-2);
        sprite = new Sprite(textureRegion);
        sprite.setSize(width, height);
        sprite.setOriginCenter();
        setOffset(-width/2f, -height/2f);
    }

    public SpriteBehaviour(GameObject gameObject, Sprite sprite, float renderOrder) {
        super(gameObject);
        setRenderOrder(renderOrder);
        this.sprite = sprite;
        setOffset(-sprite.getWidth()/2f, -sprite.getHeight()/2f);
    }

    public SpriteBehaviour(GameObject gameObject){
        super(gameObject);
    }

    public SpriteBehaviour setOffset(float x, float y){
        offsetX = x;
        offsetY = y;
        return this;
    }

    @Override
    public void fixedUpdate() {
        Box2dBehaviour box2dBehaviour = getGameObject().getBehaviour(Box2dBehaviour.class);
        if (box2dBehaviour != null) {
            if (box2dBehaviour.getBody().getPosition().equals(position)) return;

            position.set(box2dBehaviour.getBody().getPosition());

            position.add(offsetX, offsetY);
            this.sprite.setPosition(position.x, position.y);
        } else {
            SecondGDXGame.instance.helper.log("[SpriteBehaviour:67] No Box2dBehaviour.class for game object " + getGameObject().getName());
        };
    }

    @Override
    public void render(Batch batch) {
        sprite.draw(batch);
    }
}
