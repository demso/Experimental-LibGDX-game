package com.mygdx.game.gamestate.objects.items.guns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.gamestate.player.Player;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

public class GunSpriteBehaviour extends SpriteBehaviour {
    public static boolean debug = false;
    Player player;
    Matrix3 translateTransform;
    Matrix3 rotationTransform;
    Matrix3 shakeRotationTransform;
    Matrix3 scaleTransform;
    Matrix3 offsetRotationTransform;
    float recoil = 2f/32f,
        shake = 50f;
    public boolean equipped = false;
    Player playerEquipped;
    //rotate axis in local coordinates
    Vector3 rotateAxis = new Vector3();
    Vector2 tempVec = new Vector2();
    ShapeDrawer shapeDrawer;

    public GunSpriteBehaviour(GameObject gameObject, TextureRegion textureRegion, float renderOrder) {
        super(gameObject, textureRegion, renderOrder);
        init();
    }

    public GunSpriteBehaviour(GameObject gameObject, float width, float height, TextureRegion textureRegion, float renderOrder) {
        super(gameObject);
        setRenderOrder(renderOrder);
        sprite = new Sprite(textureRegion);
        sprite.setSize(width, height);
        sprite.setOriginCenter();
        init();
    }

    public GunSpriteBehaviour(GameObject gameObject, Sprite sprite, float renderOrder) {
        super(gameObject, sprite, renderOrder);
        init();
    }

    private void init(){
        translateTransform = new Matrix3();
        rotationTransform = new Matrix3();
        scaleTransform = new Matrix3();
        shakeRotationTransform = new Matrix3();
        offsetRotationTransform = new Matrix3();
        player = GameState.instance.player;
        shapeDrawer = new ShapeDrawer(GameState.instance.batch, new TextureRegion(GameState.instance.userSelection, 1,1, 1,1));
        shapeDrawer.setDefaultLineWidth(0.5f/GameState.TILE_SIDE);
        recoilInterpolation = Interpolation.pow3InInverse;
        returnInterpolation = Interpolation.pow3;
        shakeInterpolation = Interpolation.elasticOut;
        rotateAxis.set(0f, 0f, 1);

        scaleTransform.scale(0.4f, 0.4f);
    }

    Interpolation recoilInterpolation, returnInterpolation, shakeInterpolation;

    float recoilProgress = 1, returnProgress = 1, shakeProgress = 0.6f, alpha, alphaShake;

    Vector2 translationOnFire = new Vector2();

    float coef = 1f;
    float recoilTime = 0.035f * coef;
    float returnTime = 0.06f * coef;
    float shakeTime = 0.025f * 20;
    float rotationOffset = -32;

    public void fire(){

        //Vector3 mousePos = GameState.instance.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        shakeProgress = 0f;

        if (recoilProgress == 1f){
            recoilProgress = 0f;
            lastValue = 0;
            translateTransform.getTranslation(tempVec);
            distanceFromOrigin.set(-tempVec.x, -tempVec.y);
        }

        translateTransform.getTranslation(translationOnFire);
    }

    //float shakeElapsedTime;

    float lastValue;
    Vector2 distanceFromOrigin = new Vector2();
    float xCoef = 1;
    float yCoef = 1;
    boolean flip = false;

    @Override
    public void update(float delta) {
        Vector3 mousePos = GameState.instance.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        float rotation = Double.valueOf(Math.toDegrees(Math.atan2(mousePos.y - player.getPosition().y, mousePos.x - player.getPosition().x))).floatValue();
        HandyHelper.instance.log(Float.toString(Math.round(rotation)));

        if ((rotation < -90 && rotation >= -180) || rotation <= 180 && rotation > 90) {
            flip = true;
            sprite.setFlip(false, true);
            //xCoef = -1;
            offsetRotationTransform.setToRotation(-rotationOffset);
        }
        else{
            flip = false;
            sprite.setFlip(false, false);
            //xCoef = 1;
            offsetRotationTransform.setToRotation(rotationOffset);
        }
        if (shakeProgress < 0.6f){
            shakeProgress += delta/shakeTime;
            shakeProgress = Math.min(0.6f, shakeProgress);

            alphaShake = (shakeInterpolation.apply(shakeProgress) - 1) * 0.5f;

            if (flip)
                shakeRotationTransform.setToRotation(alphaShake * -shake);
            else
                shakeRotationTransform.setToRotation(alphaShake * shake);
        }

        if (recoilInterpolation != null && recoilProgress < 1f) {
            recoilProgress += delta/recoilTime;
            recoilProgress = Math.min(1f, recoilProgress);

            alpha = recoilInterpolation.apply(recoilProgress);

            translateTransform.getTranslation(tempVec);

            if (sprite.isFlipY())
                translateTransform.translate(-(Math.max(0, recoil - distanceFromOrigin.x)) * (alpha - lastValue), 0);
            else
                translateTransform.translate(-(Math.max(0, recoil - distanceFromOrigin.x)) * (alpha - lastValue), 0);

            //shakeRotationTransform.setToRotation(alphaShake * shake);

            if(recoilProgress == 1f) {
                returnProgress = 0f;
                translateTransform.getTranslation(tempVec);
                distanceFromOrigin.set(-tempVec.x, -tempVec.y);
            }
        }
       else
        if (returnInterpolation != null && returnProgress < 1f){
            returnProgress += delta / returnTime;
            returnProgress = Math.min(1f, returnProgress);
            alpha = 1 - returnInterpolation.apply(returnProgress);
            translateTransform.getTranslation(tempVec);
            if (sprite.isFlipY())
                translateTransform.translate(-(distanceFromOrigin.x) * (alpha - lastValue), -(distanceFromOrigin.y) * (alpha - lastValue));
            else
                translateTransform.translate(-(distanceFromOrigin.x) * (alpha - lastValue), -(distanceFromOrigin.y) * (alpha - lastValue));
        }

        lastValue = alpha;

//        if (rotation < -90 && rotation >= -180) {
//            rotation -= 180;
//        } else if ( rotation <= 180 && rotation > 90){
//            rotation -= 180;
//        }

        rotationTransform.setToRotation(rotation);

        //HandyHelper.instance.log("[GunSprite:169] rot: " + Math.round(rotation));

        transformSprite(translateTransform);
    }

    public void transformSprite(Matrix3 mat){
        float[] vertices = sprite.getVertices();

        Vector2 vectorXY1 = new Vector2(-sprite.getOriginX(), -sprite.getOriginY());
        Vector2 vectorXY3 = new Vector2(vectorXY1.x + sprite.getWidth(), vectorXY1.y + sprite.getHeight());
        Vector2 vectorXY2 = new Vector2(vectorXY1.x, vectorXY3.y);
        Vector2 vectorXY4 = new Vector2(vectorXY3.x, vectorXY1.y);
        float worldOriginX = vectorXY1.x + sprite.getX();
        float worldOriginY = vectorXY1.y + sprite.getY();

        vectorXY1.mul(scaleTransform).mul(offsetRotationTransform).mul(shakeRotationTransform).mul(mat).mul(rotationTransform);
        vectorXY2.mul(scaleTransform).mul(offsetRotationTransform).mul(shakeRotationTransform).mul(mat).mul(rotationTransform);
        vectorXY3.mul(scaleTransform).mul(offsetRotationTransform).mul(shakeRotationTransform).mul(mat).mul(rotationTransform);
        vectorXY4.mul(scaleTransform).mul(offsetRotationTransform).mul(shakeRotationTransform).mul(mat).mul(rotationTransform);

//        if (flip) {
//            vertices[U1] = vertices[U3];
//            vertices[U2] = vertices[U4];
//            vertices[U3] = vertices[U1];
//            vertices[U4] = vertices[U2];
//        }

        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 1;
        float x3 = 1;
        float y3 = 1;
        float x4 = 1;
        float y4 = 0;

//        if (sprite.isFlipY()){
//            x1 = vectorXY4.x + worldOriginX;
//            y1 = vectorXY4.y + worldOriginY;
//            x2 = vectorXY3.x + worldOriginX;
//            y2 = vectorXY3.y + worldOriginY;
//            x3 = vectorXY2.x + worldOriginX;
//            y3 = vectorXY2.y + worldOriginY;
//            x4 = vectorXY1.x + worldOriginX;
//            y4 = vectorXY1.y + worldOriginY;
//        } else {
            x1 = vectorXY1.x * xCoef + worldOriginX;
            y1 = vectorXY1.y * yCoef + worldOriginY;
            x2 = vectorXY2.x * xCoef + worldOriginX;
            y2 = vectorXY2.y * yCoef + worldOriginY;
            x3 = vectorXY3.x * xCoef + worldOriginX;
            y3 = vectorXY3.y * yCoef + worldOriginY;
            x4 = vectorXY4.x * xCoef + worldOriginX;
            y4 = vectorXY4.y * yCoef + worldOriginY;
        //}

//        if (flip){
//            vertices[X1] = x2;
//            vertices[Y1] = y2;
//
//            vertices[X2] = x1;
//            vertices[Y2] = y1;
//
//            vertices[X3] = x4;
//            vertices[Y3] = y4;
//
//            vertices[X4] = x3;
//            vertices[Y4] = y3;
//        } else {
            vertices[X1] = x1;
            vertices[Y1] = y1;

            vertices[X2] = x2;
            vertices[Y2] = y2;

            vertices[X3] = x3;
            vertices[Y3] = y3;

            vertices[X4] = x4;
            vertices[Y4] = y4;
        //}
    }

    public void equip(Player player){
        equipped = true;
        playerEquipped = player;
    }

    @Override
    public void fixedUpdate() {
        Box2dBehaviour box2dBehaviour = getGameObject().getBehaviour(Box2dBehaviour.class);
        if (box2dBehaviour != null) {
            if (box2dBehaviour.getBody().getPosition().equals(position)) return;

            position.set(box2dBehaviour.getBody().getPosition());

            position.add(offsetX, offsetY);
            this.sprite.setPosition(position.x, position.y);
        }
        if (equipped){
            sprite.setPosition(player.getPosition().x + (sprite.getWidth() / 2f), player.getPosition().y + sprite.getHeight() / 2f);
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
