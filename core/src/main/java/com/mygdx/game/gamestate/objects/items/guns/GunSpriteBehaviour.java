package com.mygdx.game.gamestate.objects.items.guns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.gamestate.objects.items.PistolAnimation;
import com.mygdx.game.gamestate.player.Player;
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

    public GunSpriteBehaviour(GameObject gameObject, Gun gun, Sprite sprite, float renderOrder) {
        super(gameObject, sprite, renderOrder);
        setRenderOrder(renderOrder);
        this.sprite = sprite;
        init();
    }

    private void init(){
        setOffset(-sprite.getWidth()/2f, -sprite.getHeight()/2f);
        pistolAnimation = new PistolAnimation();
    }

    public void onFire(){
        pistolAnimation.fire();
    }

    @Override
    public void update(float delta) {
        if (gun.isEquipped()) {
            Player player = gun.getOwnerId();
            float rotation = 0;
            if (!player.getName().equals(SecondGDXGame.instance.name))
                rotation = player.itemRotation;
            else {
                Vector3 mousePos = GameState.instance.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                rotation = Double.valueOf(Math.toDegrees(Math.atan2(mousePos.y - player.getPosition().y, mousePos.x - player.getPosition().x))).floatValue();
                player.itemRotation = rotation;
            }
            pistolAnimation.updateAndTransform(delta, rotation, sprite);
        } else {
            super.update(delta);
        }
    }

    @Override
    public void fixedUpdate() {
        if (gun.isEquipped()) {
            sprite.setPosition(gun.getOwnerId().getPosition().x + (sprite.getWidth() / 2f), gun.getOwnerId().getPosition().y + sprite.getHeight() / 2f);
        } else {
            super.fixedUpdate();
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
