package com.mygdx.game.gamestate.objects.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
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
        setRenderOrder(renderOrder);
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
            Body body = box2dBehaviour.getBody();
            if (body.getPosition().equals(position)) return;

            position.set(body.getPosition());

            position.add(offsetX, offsetY);
            this.sprite.setPosition(position.x, position.y);
            sprite.setRotation((float) Math.toDegrees(body.getAngle()));
        } else {
            SecondGDXGame.instance.helper.log("[SpriteBehaviour:67] No Box2dBehaviour.class for game object " + getGameObject().getName());
        };
    }

    @Override
    public void render(Batch batch) {
        Vector3 vec = GameState.instance.camera.project(new Vector3(sprite.getX(), sprite.getY(), 0f));
        if (vec.x < 0 || vec.x > Gdx.graphics.getWidth() || vec.y < 0 || vec.y > Gdx.graphics.getHeight())
            return;
        sprite.draw(batch);
    }


}
