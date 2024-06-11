package com.mygdx.game.gamestate.objects.items.guns;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dev.lyze.gdxUnBox2d.GameObject;

public class PistolSprite extends GunSpriteBehaviour {
    //PistolAnimation pistolAnimation;

    public PistolSprite(GameObject gameObject, Gun gun, float width, float height, TextureRegion textureRegion, float renderOrder) {
        super(gameObject);
        this.gun = gun;
        setRenderOrder(renderOrder);
        sprite = new Sprite(textureRegion);
        sprite.setSize(width, height);
        sprite.setOriginCenter();
        init();
    }

//    private void init(){
//        setOffset(-sprite.getWidth()/2f, -sprite.getHeight()/2f);
//        pistolAnimation = new PistolAnimation();
//    }
//
//    public void onFire(){
//        pistolAnimation.fire();
//    }
//
//    @Override
//    public void update(float delta) {
//        if (gun.isEquipped()) {
//            Player player = (Player) gun.getOwner();
//            float rotation = 0;
//            if (!player.getName().equals(SecondGDXGame.instance.name))
//                rotation = player.itemRotation;
//            else {
//                Vector3 mousePos = GameState.instance.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
//                rotation = Double.valueOf(Math.toDegrees(Math.atan2(mousePos.y - player.getPosition().y, mousePos.x - player.getPosition().x))).floatValue();
//                player.itemRotation = rotation;
//            }
//            pistolAnimation.updateAndTransform(delta, rotation, sprite);
//        }
//    }
//
//    @Override
//    public void fixedUpdate() {
//        if (gun.isEquipped()) {
//            sprite.setPosition(gun.getOwner().getPosition().x + (sprite.getWidth() / 2f), gun.getOwner().getPosition().y + sprite.getHeight() / 2f);
//        } else {
//            super.fixedUpdate();
//        }
//    }
//
//    @Override
//    public void render(Batch batch) {
//        super.render(batch);
//        if (debug) {
//            shapeDrawer.setColor(Color.CYAN);
//            float[] vertices = sprite.getVertices();
//            shapeDrawer.polygon(new float[]{vertices[X1], vertices[Y1], vertices[X2], vertices[Y2], vertices[X3], vertices[Y3], vertices[X4], vertices[Y4]});
//        }
//    }
}
