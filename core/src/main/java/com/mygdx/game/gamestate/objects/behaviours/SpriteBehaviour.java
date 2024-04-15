package com.mygdx.game.gamestate.objects.behaviours;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.SecondGDXGame;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;
import lombok.Getter;
import lombok.Setter;

public class SpriteBehaviour extends BehaviourAdapter {
    private float offsetX;
    private float offsetY;
    @Getter @Setter private Sprite sprite;
    private final Vector2 position = new Vector2();

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

    public SpriteBehaviour setOffset(float x, float y){
        offsetX = x;
        offsetY = y;
        return this;
    }

    @Override
    public void fixedUpdate() {
        Box2dBehaviour box2dBehaviour = getGameObject().getBehaviour(Box2dBehaviour.class);
        if (box2dBehaviour != null)
            position.set(box2dBehaviour.getBody().getPosition());
        else {
            SecondGDXGame.helper.log("[SpriteBehaviour] No Box2dBehaviour.class");
        };

        position.add(offsetX, offsetY);
        this.sprite.setPosition(position.x, position.y);
    }

    @Override
    public void render(Batch batch) {
        sprite.draw(batch);
    }
}
