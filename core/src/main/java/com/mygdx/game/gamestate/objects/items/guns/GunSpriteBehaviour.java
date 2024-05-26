package com.mygdx.game.gamestate.objects.items.guns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.gamestate.objects.items.PistolAnimation;
import com.mygdx.game.gamestate.player.Player;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

public class GunSpriteBehaviour extends SpriteBehaviour {
    public static boolean debug = false;
    Gun gun;
    ShapeDrawer shapeDrawer;
    PistolAnimation pistolAnimation;

    public GunSpriteBehaviour(GameObject gameObject, Gun gun, TextureRegion textureRegion, float renderOrder) {
        super(gameObject, textureRegion, renderOrder);
        this.gun = gun;
        setRenderOrder(renderOrder);
        sprite = new Sprite(textureRegion);
        sprite.setSize(1, 1);
        sprite.setOriginCenter();
        init();
    }

    public GunSpriteBehaviour(GameObject gameObject, Gun gun, float width, float height, TextureRegion textureRegion, float renderOrder) {
        super(gameObject);
        this.gun = gun;
        setRenderOrder(renderOrder);
        sprite = new Sprite(textureRegion);
        sprite.setSize(width, height);
        sprite.setOriginCenter();
        init();
    }

//    public GunSpriteBehaviour(GameObject gameObject, Gun gun, Sprite sprite, float renderOrder) {
//        super(gameObject, sprite, renderOrder);
//
//        init();
//    }

    private void init(){
        setOffset(-sprite.getWidth()/2f, -sprite.getHeight()/2f);
        pistolAnimation = new PistolAnimation();

        //scaleTransform.scale(0.4f, 0.4f);
    }

    Interpolation recoilInterpolation, returnInterpolation, shakeInterpolation;

    public void onFire(){
        pistolAnimation.fire();
    }

    @Override
    public void update(float delta) {
        if (gun.isEquipped()) {
            Player player = gun.getOwner();
            Vector3 mousePos = GameState.instance.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            float rotation = Double.valueOf(Math.toDegrees(Math.atan2(mousePos.y - player.getPosition().y, mousePos.x - player.getPosition().x))).floatValue();

            pistolAnimation.updateAndTransform(delta, rotation, sprite);
        } else {
            sprite.setOriginCenter();
        }
    }

    @Override
    public void fixedUpdate() {
        Box2dBehaviour box2dBehaviour = getGameObject().getBehaviour(Box2dBehaviour.class);
        if (box2dBehaviour != null && !gun.isEquipped()) {
            if (box2dBehaviour.getBody().getPosition().equals(position)) return;
            sprite.setFlip(false, false);
            position.set(box2dBehaviour.getBody().getPosition());

            position.add(offsetX, offsetY);
            this.sprite.setPosition(position.x, position.y);
        }
        if (gun.isEquipped()){
            sprite.setPosition(gun.getOwner().getPosition().x + (sprite.getWidth() / 2f), gun.getOwner().getPosition().y + sprite.getHeight() / 2f);
        }
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
        if (debug) {
            shapeDrawer.setColor(Color.CYAN);
            float[] vertices = sprite.getVertices();
            shapeDrawer.polygon(new float[]{vertices[X1], vertices[Y1], vertices[X2], vertices[Y2], vertices[X3], vertices[Y3], vertices[X4], vertices[Y4]});
        }
    }
}
