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
    Matrix3 translateScaleTransform;
    Matrix3 rotationTransform;
    float recoil = 10f/32f;
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
        init();

        setRenderOrder(renderOrder);
        sprite = new Sprite(textureRegion);
        sprite.setSize(width, height);
        sprite.setOriginCenter();
        //setOffset(-width/2f, -height/2f);
    }

    public GunSpriteBehaviour(GameObject gameObject, Sprite sprite, float renderOrder) {
        super(gameObject, sprite, renderOrder);
        init();
    }

    private void init(){
        translateScaleTransform = new Matrix3();
        rotationTransform = new Matrix3();
        player = GameState.instance.player;
        shapeDrawer = new ShapeDrawer(GameState.instance.batch, new TextureRegion(GameState.instance.userSelection, 1,1, 1,1));
        shapeDrawer.setDefaultLineWidth(0.5f/GameState.TILE_SIDE);
        recoilInterpolation = Interpolation.pow3InInverse;
        returnInterpolation = Interpolation.pow3;
        rotateAxis.set(0f, 0f, 1);

        translateScaleTransform.scale(0.5f, 0.5f);
    }

    Interpolation recoilInterpolation, returnInterpolation;

    float recoilProgress = 1, returnProgress = 1, commonProgress, alpha;

    Vector2 recPos = new Vector2();

    float recoilTime = 0.5f;
    float returnTime = 1f;

    public void fire(){

        //Vector3 mousePos = GameState.instance.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        if (recoilProgress == 1f)
            recoilProgress = 0f;
    }

    //float elapsedTime;

    float lastValue;
    Vector2 distanceFromOrigin = new Vector2();

    @Override
    public void update(float delta) {
        Vector3 mousePos = GameState.instance.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        float rotation = Double.valueOf(Math.toDegrees(Math.atan2(mousePos.y - player.getPosition().y, mousePos.x - player.getPosition().x))).floatValue()-34;

       if (recoilInterpolation != null && recoilProgress < 1f){
            recoilProgress += delta/ recoilTime;
            recoilProgress = Math.min(1f, recoilProgress);
            if(recoilProgress == 1f)
                returnProgress = 0f;
            alpha = recoilInterpolation.apply(recoilProgress);
            translateScaleTransform.getTranslation(tempVec);
            commonProgress = Math.abs(tempVec.len() / recoil);
            translateScaleTransform.translate(-recoil * (alpha - lastValue) , -recoil * (alpha - lastValue));
       } else if (returnInterpolation != null && returnProgress < 1f){
            returnProgress += delta / returnTime;
            returnProgress = Math.min(1f, returnProgress);
            alpha = 1 - returnInterpolation.apply(returnProgress);
            translateScaleTransform.getTranslation(tempVec);
            commonProgress = Math.abs(tempVec.len() / recoil);
            translateScaleTransform.translate(-recoil * (alpha - lastValue), -recoil * (alpha - lastValue));
        }

        lastValue = alpha;

        rotationTransform.setToRotation(rotation);

        transformSprite(translateScaleTransform);

        HandyHelper.instance.log("[GunSpriteBehaviour:113] recoilProgress: " + recoilProgress + " returnProgress: " + returnProgress);
    }

    public void transformSprite(Matrix3 mat){
        float[] vertices = sprite.getVertices();

        Vector2 vectorXY1 = new Vector2(-sprite.getOriginX(), -sprite.getOriginY());
        Vector2 vectorXY3 = new Vector2(vectorXY1.x + sprite.getWidth(), vectorXY1.y + sprite.getHeight());
        Vector2 vectorXY2 = new Vector2(vectorXY1.x, vectorXY3.y);
        Vector2 vectorXY4 = new Vector2(vectorXY3.x, vectorXY1.y);
        float worldOriginX = vectorXY1.x + sprite.getX();
        float worldOriginY = vectorXY1.y + sprite.getY();

        vectorXY1.mul(mat).mul(rotationTransform);
        vectorXY2.mul(mat).mul(rotationTransform);
        vectorXY3.mul(mat).mul(rotationTransform);
        vectorXY4.mul(mat).mul(rotationTransform);

        final float x1 = vectorXY1.x + worldOriginX;
        final float y1 = vectorXY1.y + worldOriginY;
        final float x2 = vectorXY2.x + worldOriginX;
        final float y2 = vectorXY2.y + worldOriginY;
        final float x3 = vectorXY3.x + worldOriginX;
        final float y3 = vectorXY3.y + worldOriginY;
        final float x4 = vectorXY4.x + worldOriginX;
        final float y4 = vectorXY4.y + worldOriginY;

        vertices[X1] = x1;
        vertices[Y1] = y1;

        vertices[X2] = x2;
        vertices[Y2] = y2;

        vertices[X3] = x3;
        vertices[Y3] = y3;

        vertices[X4] = x4;
        vertices[Y4] = y4;
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
