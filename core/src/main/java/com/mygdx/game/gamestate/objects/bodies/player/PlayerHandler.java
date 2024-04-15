package com.mygdx.game.gamestate.objects.bodies.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.gamestate.GameState;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class PlayerHandler extends BehaviourAdapter {
    public enum State {
        Standing, Walking
    }
    public enum Facing {
        RIGHT, LEFT, UP, DOWN
    }

    public boolean moveUp,
            moveDown,
            moveToTheRight,
            moveToTheLeft;

    float stateTime = 0;

    Animation<TextureRegion> walkSide;
    Animation<TextureRegion> walkUp;
    Animation<TextureRegion> walkDown;

    public State state =  State.Walking;
    public Facing facing = Facing.DOWN;

    TextureRegion currentFrame;

    float frameDuration = 0.1f;

    Player player;

    public PlayerHandler(GameObject gameObject, Player player) {
        super(gameObject);
        this.player = player;

        Texture textureSheet = new Texture(Gdx.files.internal("ClassicRPG_Sheet.png"));
        TextureRegion[][] textureRegions = TextureRegion.split(textureSheet, 16, 16);

        TextureRegion[] walkFrames = new TextureRegion[4];
        int index = 0;
        for (int i = 0; i < 4; i++)
            walkFrames[index++] = textureRegions[0][i];
        walkDown = new Animation<TextureRegion>(frameDuration, walkFrames);

        walkFrames = new TextureRegion[4];
        index = 0;
        for (int i = 0; i < 4; i++)
            walkFrames[index++] = textureRegions[1][i];
        walkSide = new Animation<TextureRegion>(frameDuration, walkFrames);

        walkFrames = new TextureRegion[4];
        index = 0;
        for (int i = 0; i < 4; i++)
            walkFrames[index++] = textureRegions[3][i];
        walkUp = new Animation<TextureRegion>(frameDuration, walkFrames);
    }

    Vector2 movingVector = new Vector2();
    Vector2 vel = new Vector2();
    Vector2 zeroVector = new Vector2(0, 0);

    @Override
    public void update(float delta) {
        if (delta > 0.1f) delta = 0.1f;
        stateTime += delta;
        switch (state) {
            case Standing:
                currentFrame = walkDown.getKeyFrame(1);
                break;
            case Walking:
                switch (facing) {
                    case RIGHT:
                    case LEFT:
                        currentFrame = walkSide.getKeyFrame(stateTime, true);
                        break;
                    case UP:
                        currentFrame = walkUp.getKeyFrame(stateTime, true);
                        break;
                    case DOWN:
                        currentFrame = walkDown.getKeyFrame(stateTime, true);
                        break;
                }
                break;
        }
    }

    @Override
    public void render(Batch batch) {
       // batch.draw(walkDown.getKeyFrame(2), player.getPosition().x - player.WIDTH/2 + player.WIDTH, player.getPosition().y - player.WIDTH * 1/4, -player.WIDTH, player.HEIGHT);
        if (facing == Facing.RIGHT)
            batch.draw(currentFrame, player.getPosition().x - player.WIDTH/2 + player.WIDTH, player.getPosition().y - player.WIDTH * 1/4, -player.WIDTH, player.HEIGHT);
        else
            batch.draw(currentFrame, player.getPosition().x - player.WIDTH/2, player.getPosition().y - player.WIDTH * 1/4, player.WIDTH, player.HEIGHT);
        if (player.equipedItem != null){
            TextureRegion tileTextureRegion = player.equipedItem.item.tile.getTextureRegion();
            float width = 0.5f;
            float height = 0.5f;
            float offsetX = 0;
            float offsetY = 0f;
            Vector3 mousePos = GameState.Instance.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            float rotation = Double.valueOf(Math.toDegrees(Math.atan2(mousePos.y - player.getPosition().y, mousePos.x - player.getPosition().x))).floatValue()-34;
            batch.draw(tileTextureRegion, player.getPosition().x - (tileTextureRegion.getRegionWidth()*width /2f + offsetX)/(float) GameState.TILE_SIDE, player.getPosition().y -
                    (tileTextureRegion.getRegionHeight()*height/2f + offsetY)/(float) GameState.TILE_SIDE,
                    tileTextureRegion.getRegionWidth()*width/2f/(float) GameState.TILE_SIDE,
                    tileTextureRegion.getRegionHeight()*height/2f/ (float) GameState.TILE_SIDE,
                    width, height, 1,1,rotation);
        }
    }

    @Override
    public void fixedUpdate() {
        if (player.isAlive() && (moveUp || moveDown || moveToTheRight || moveToTheLeft)){
            vel.set(0,0);
            movingVector.set(0,0);
            if (!(moveToTheRight && moveToTheLeft)) {
                if (moveToTheLeft) {
                    movingVector.set(-player.maxVelocity, movingVector.y);
                    state = State.Walking;
                    facing = Facing.LEFT;
                }

                if (moveToTheRight) {
                    movingVector.set(player.maxVelocity, movingVector.y);
                    state = State.Walking;
                    facing = Facing.RIGHT;
                }
            }
            if (!(moveUp && moveDown)){
                if (moveUp) {
                    movingVector.set(movingVector.x, player.maxVelocity);
                    state = State.Walking;
                    facing = Facing.UP;
                }

                if (moveDown) {
                    movingVector.set(movingVector.x, -player.maxVelocity);
                    state = State.Walking;
                    facing = Facing.DOWN;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.C))
                vel.set(movingVector.clamp(0, player.maxVelocity).scl(0.5f));
            else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                vel.set(movingVector.clamp(0, player.maxVelocity).scl(1.5f));
            else vel.set(movingVector.clamp(0, player.maxVelocity));
            player.getBody().applyLinearImpulse(vel, zeroVector, true);
            player.velocity.set(vel);
        } if (Math.abs(player.getBody().getLinearVelocity().len2()) < 0.5f) {
            state = State.Standing;
        }
    }
}
