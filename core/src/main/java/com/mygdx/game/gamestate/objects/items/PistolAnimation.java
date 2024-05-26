package com.mygdx.game.gamestate.objects.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.gamestate.player.Player;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.badlogic.gdx.graphics.g2d.Batch.*;
import static com.badlogic.gdx.graphics.g2d.Batch.Y4;

public class PistolAnimation {
    Matrix3 translateTransform;
    Matrix3 rotationTransform;
    Matrix3 shakeRotationTransform;
    Matrix3 scaleTransform;
    Matrix3 offsetRotationTransform;
    float recoil = 2f/32f,
            shake = 50f;
    Vector3 rotateAxis = new Vector3();
    Vector2 tempVec = new Vector2();

    public PistolAnimation(){
        translateTransform = new Matrix3();
        rotationTransform = new Matrix3();
        scaleTransform = new Matrix3();
        shakeRotationTransform = new Matrix3();
        offsetRotationTransform = new Matrix3();
        recoilInterpolation = Interpolation.pow3InInverse;
        returnInterpolation = Interpolation.pow3;
        shakeInterpolation = Interpolation.elasticOut;
        rotateAxis.set(0f, 0f, 1);
    }

//    private void init(){
//        translateTransform = new Matrix3();
//        rotationTransform = new Matrix3();
//        scaleTransform = new Matrix3();
//        shakeRotationTransform = new Matrix3();
//        offsetRotationTransform = new Matrix3();
//        recoilInterpolation = Interpolation.pow3InInverse;
//        returnInterpolation = Interpolation.pow3;
//        shakeInterpolation = Interpolation.elasticOut;
//        rotateAxis.set(0f, 0f, 1);
//        //scaleTransform.scale(0.4f, 0.4f);
//    }

    Interpolation recoilInterpolation, returnInterpolation, shakeInterpolation;

    float recoilProgress = 1, returnProgress = 1, shakeProgress = 0.6f, alpha, alphaShake;

    float coef = 1f;
    float recoilTime = 0.035f * coef;
    float returnTime = 0.06f * coef;
    float shakeTime = 0.025f * 20;
    float rotationOffset = -32;

    float lastValue;
    Vector2 distanceFromOrigin = new Vector2();
    float xCoef = 1;
    float yCoef = 1;
    boolean flip = false;

    public void update(float delta, float rotation) {
        //Player player = gun.getOwner();
        Vector3 mousePos = GameState.instance.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        //float rotation = Double.valueOf(Math.toDegrees(Math.atan2(mousePos.y - player.getPosition().y, mousePos.x - player.getPosition().x))).floatValue();
        //HandyHelper.instance.log(Float.toString(Math.round(rotation)));

        if ((rotation < -90 && rotation >= -180) || rotation <= 180 && rotation > 90) {
            flip = true;
            offsetRotationTransform.setToRotation(-rotationOffset);
        } else {
            flip = false;
            offsetRotationTransform.setToRotation(rotationOffset);
        }
        if (shakeProgress < 0.6f) {
            shakeProgress += delta / shakeTime;
            shakeProgress = Math.min(0.6f, shakeProgress);

            alphaShake = (shakeInterpolation.apply(shakeProgress) - 1) * 0.5f;

            if (flip) shakeRotationTransform.setToRotation(alphaShake * -shake);
            else shakeRotationTransform.setToRotation(alphaShake * shake);
        }

        if (recoilInterpolation != null && recoilProgress < 1f) {
            recoilProgress += delta / recoilTime;
            recoilProgress = Math.min(1f, recoilProgress);

            alpha = recoilInterpolation.apply(recoilProgress);

            translateTransform.getTranslation(tempVec);

            if (flip)
                translateTransform.translate(-(Math.max(0, recoil - distanceFromOrigin.x)) * (alpha - lastValue), 0);
            else
                translateTransform.translate(-(Math.max(0, recoil - distanceFromOrigin.x)) * (alpha - lastValue), 0);

            if (recoilProgress == 1f) {
                returnProgress = 0f;
                translateTransform.getTranslation(tempVec);
                distanceFromOrigin.set(-tempVec.x, -tempVec.y);
            }
        } else if (returnInterpolation != null && returnProgress < 1f) {
            returnProgress += delta / returnTime;
            returnProgress = Math.min(1f, returnProgress);
            alpha = 1 - returnInterpolation.apply(returnProgress);
            translateTransform.getTranslation(tempVec);
            if (flip)
                translateTransform.translate(-(distanceFromOrigin.x) * (alpha - lastValue), -(distanceFromOrigin.y) * (alpha - lastValue));
            else
                translateTransform.translate(-(distanceFromOrigin.x) * (alpha - lastValue), -(distanceFromOrigin.y) * (alpha - lastValue));
        }

        lastValue = alpha;

        rotationTransform.setToRotation(rotation);

        //HandyHelper.instance.log("[GunSprite:169] rot: " + Math.round(rotation));

        //transformSprite(translateTransform);
    }

    public void updateAndTransform(float delta, float rotation, Sprite sprite){
        update(delta, rotation);
        transformSprite(sprite);
    }

    public void transformSprite(Sprite sprite){
        float[] vertices = sprite.getVertices();

        Vector2 vectorXY1 = new Vector2(-sprite.getOriginX(), -sprite.getOriginY());
        Vector2 vectorXY3 = new Vector2(vectorXY1.x + sprite.getWidth(), vectorXY1.y + sprite.getHeight());
        Vector2 vectorXY2 = new Vector2(vectorXY1.x, vectorXY3.y);
        Vector2 vectorXY4 = new Vector2(vectorXY3.x, vectorXY1.y);
        float worldOriginX = vectorXY1.x + sprite.getX();
        float worldOriginY = vectorXY1.y + sprite.getY();

        vectorXY1.mul(scaleTransform).mul(offsetRotationTransform).mul(shakeRotationTransform).mul(translateTransform).mul(rotationTransform);
        vectorXY2.mul(scaleTransform).mul(offsetRotationTransform).mul(shakeRotationTransform).mul(translateTransform).mul(rotationTransform);
        vectorXY3.mul(scaleTransform).mul(offsetRotationTransform).mul(shakeRotationTransform).mul(translateTransform).mul(rotationTransform);
        vectorXY4.mul(scaleTransform).mul(offsetRotationTransform).mul(shakeRotationTransform).mul(translateTransform).mul(rotationTransform);

        float x1 = vectorXY1.x * xCoef + worldOriginX;
        float y1 = vectorXY1.y * yCoef + worldOriginY;
        float x2 = vectorXY2.x * xCoef + worldOriginX;
        float y2 = vectorXY2.y * yCoef + worldOriginY;
        float x3 = vectorXY3.x * xCoef + worldOriginX;
        float y3 = vectorXY3.y * yCoef + worldOriginY;
        float x4 = vectorXY4.x * xCoef + worldOriginX;
        float y4 = vectorXY4.y * yCoef + worldOriginY;

        if (flip) {
            vertices[X1] = x2;
            vertices[Y1] = y2;

            vertices[X2] = x1;
            vertices[Y2] = y1;

            vertices[X3] = x4;
            vertices[Y3] = y4;

            vertices[X4] = x3;
            vertices[Y4] = y3;
        } else {
            vertices[X1] = x1;
            vertices[Y1] = y1;

            vertices[X2] = x2;
            vertices[Y2] = y2;

            vertices[X3] = x3;
            vertices[Y3] = y3;

            vertices[X4] = x4;
            vertices[Y4] = y4;
        }
    }
}
