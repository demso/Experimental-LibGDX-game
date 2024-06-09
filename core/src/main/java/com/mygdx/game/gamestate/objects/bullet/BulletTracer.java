package com.mygdx.game.gamestate.objects.bullet;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.GameObjectState;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;
import lombok.Getter;
import lombok.Setter;

public class BulletTracer extends BehaviourAdapter {
    protected float offsetX;
    protected float offsetY;
    @Getter
    @Setter
    protected Sprite sprite;
    protected final Vector2 position = new Vector2();
    Box2dBehaviour box2dBehaviour;
    public void setPosition(Vector2 pos){
        position.set(pos.x, pos.y);
        this.sprite.setPosition(position.x - sprite.getWidth()/2, position.y);
    }
    public BulletTracer(GameObject gameObject, TextureRegion textureRegion, float renderOrder) {
        super(gameObject);
        setRenderOrder(renderOrder);
        sprite = new Sprite(textureRegion);
    }

    public BulletTracer(GameObject gameObject, float width, float height, TextureRegion textureRegion, float renderOrder) {
        super(gameObject);
        setRenderOrder(renderOrder);
        sprite = new Sprite(textureRegion);
    }

    public BulletTracer(GameObject gameObject, Sprite sprite, float renderOrder) {
        super(gameObject);
        setRenderOrder(renderOrder);
        this.sprite = sprite;
    }

    public BulletTracer(GameObject gameObject){
        super(gameObject);
    }

    public BulletTracer setOffset(float x, float y){
        offsetX = x;
        offsetY = y;
        return this;
    }

    @Override
    public void onEnable() {
        box2dBehaviour = getGameObject().getBox2dBehaviour();
        position.set(box2dBehaviour.getBody().getPosition());
        setOffset(0f, 0f);
        sprite.setSize(0.1f,1);
        sprite.setOrigin(sprite.getWidth() / 2f, 0);
    }

    @Override
    public void update(float delta) {
        if (box2dBehaviour != null && getGameObject().getState() != GameObjectState.DESTROYING) {
            if (box2dBehaviour.getBody().getPosition().equals(position)) return;
            Vector2 vel = box2dBehaviour.getBody().getLinearVelocity();
            position.add(vel.x * delta, vel.y * delta);
            this.sprite.setPosition(position.x - sprite.getWidth()/2, position.y);
        } else {
            //SecondGDXGame.instance.helper.log("[BulletTracer:67] No Box2dBehaviour.class for game object " + getGameObject().getName());
        };
    }

    @Override
    public void fixedUpdate() {

    }

    @Override
    public void render(Batch batch) {
        sprite.draw(batch);
    }
}
