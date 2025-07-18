package com.mygdx.game.gamestate.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Utils;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.gamestate.objects.items.guns.GunMagazine;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

import java.util.Arrays;

public class ClientPlayerHandler extends BehaviourAdapter{
    public boolean moveUp,
            moveDown,
            moveToTheRight,
            moveToTheLeft;

    float stateTime = 0;

    Animation<TextureRegion> walkSide;
    Animation<TextureRegion> walkUp;
    Animation<TextureRegion> walkDown;

    TextureRegion currentFrame;

    float frameDuration = 0.1f;

    Player player;

    public ClientPlayerHandler(GameObject gameObject, Player player) {
        super(gameObject);
        this.player = player;
        setRenderOrder(Globals.PLAYER_RENDER_ORDER);
    }

    Vector2 movingVector = new Vector2();
    Vector2 movingImpulse = new Vector2();

    Vector2 zeroVector = new Vector2(0, 0);

    ReloadAnimation reloadAnimation;
    Sprite reloadSprite;
    Gun gunReloading;
    GunMagazine magReloading;

    @Override
    public void start() {
        reloadAnimation = new ReloadAnimation();
        reloadSprite = new Sprite(new Texture(Gdx.files.internal("visual/textures/reload_icon.png")));
        reloadSprite.setSize(0.3f, 0.3f);
        reloadSprite.setOriginCenter();
    }

    @Override
    public void update(float delta) {
        if (delta > 0.1f) delta = 0.1f;
        stateTime += delta;
        switch (player.getState()) {
            case Standing:
                currentFrame = walkDown.getKeyFrame(1);
                break;
            case Walking:
            case Running:
            case Sneaking:
                switch (player.getFacing()) {
                    case Right:
                    case Left:
                        currentFrame = walkSide.getKeyFrame(stateTime, true);
                        break;
                    case Up:
                        currentFrame = walkUp.getKeyFrame(stateTime, true);
                        break;
                    case Down:
                        currentFrame = walkDown.getKeyFrame(stateTime, true);
                        break;
                }
                break;
        }
        if (player.needsReload && player.isReloading) {
            player.needsReload = false;
        }

        if (player.needsReload) {
            if (player.equipedItem instanceof Gun gun) {
                Array<GunMagazine> magazs = player.getItemsOfType(GunMagazine.class);
                if (magazs == null){
                    player.needsReload = false;
                    return;
                }
                var magaz = Arrays.stream(magazs.toArray(GunMagazine.class)).filter(magaz0 -> magaz0.getGunTypes().contains(gun.stringID, false)).findFirst();
                if (magaz.isPresent()) {
                    gunReloading = gun;
                    magReloading = magaz.get();
                    player.needsReload = false;
                    player.isReloading = true;
                    gun.reload(null);
                    reloadAnimation.start(((Gun)player.equipedItem).getReloadTime() * player.reloadFactor);
                }else{
                    player.needsReload = false;
                }
            }
            player.needsReload = false;
        }

        if (player.isReloading){
            if (player.equipedItem != gunReloading) {
                player.isReloading = false;
                return;
            }
            player.reloadProgress = Utils.round(1 - reloadAnimation.getProgress(), 1);
            Vector2 pos = player.getPosition();
            reloadSprite.setPosition(pos.x + 0.2f, pos.y + 0.2f);
            reloadAnimation.updateAndTransform(delta,reloadSprite);
            if (reloadAnimation.isFinished()){
                player.isReloading = false;
                gunReloading.reload(magReloading);
            }
        }
        Vector3 mousePos = GameState.instance.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        player.itemRotation = Double.valueOf(Math.toDegrees(Math.atan2(mousePos.y - player.getPosition().y, mousePos.x - player.getPosition().x))).floatValue();
    }

    @Override
    public void render(Batch batch) {
       // batch.draw(walkDown.getKeyFrame(2), player.getPosition().x - player.WIDTH/2 + player.WIDTH, player.getPosition().y - player.WIDTH * 1/4, -player.WIDTH, player.HEIGHT);
        if (player.facing == Player.Facing.Right)
            batch.draw(currentFrame, player.getPosition().x - player.WIDTH/2 + player.WIDTH, player.getPosition().y - player.WIDTH * 1/4, -player.WIDTH, player.HEIGHT);
        else
            batch.draw(currentFrame, player.getPosition().x - player.WIDTH/2, player.getPosition().y - player.WIDTH * 1/4, player.WIDTH, player.HEIGHT);
        if (player.equipedItem != null){
            if (player.equipedItem instanceof Gun gun) {

            } else {
                TextureRegion tileTextureRegion = player.equipedItem.tile.getTextureRegion();
                float width = 0.5f;
                float height = 0.5f;
                float offsetX = 0;
                float offsetY = 0f;
                Vector3 mousePos = GameState.instance.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                float rotation = Double.valueOf(Math.toDegrees(Math.atan2(mousePos.y - player.getPosition().y, mousePos.x - player.getPosition().x))).floatValue() - 34;
                batch.draw(tileTextureRegion, player.getPosition().x - (tileTextureRegion.getRegionWidth() * width / 2f + offsetX) / (float) GameState.TILE_SIDE,
                        player.getPosition().y - (tileTextureRegion.getRegionHeight() * height / 2f + offsetY) / (float) GameState.TILE_SIDE,
                        tileTextureRegion.getRegionWidth() * width / 2f / (float) GameState.TILE_SIDE,
                        tileTextureRegion.getRegionHeight() * height / 2f / (float) GameState.TILE_SIDE, width, height,
                        1, 1, rotation);
            }
        }
        if (player.isReloading)
            reloadSprite.draw(batch);
    }

    @Override
    public void fixedUpdate()  {
        if (player.isAlive() && (moveUp || moveDown || moveToTheRight || moveToTheLeft)){
            movingImpulse.set(0,0);
            movingVector.set(0,0);
            if (!(moveToTheRight && moveToTheLeft)) {
                if (moveToTheLeft) {
                    movingVector.x = -1;
                    player.state = Player.State.Walking;
                    player.facing = Player.Facing.Left;
                }

                if (moveToTheRight) {
                    movingVector.x = 1;
                    player.state = Player.State.Walking;
                    player.facing = Player.Facing.Right;
                }
            }
            if (!(moveUp && moveDown)){
                if (moveUp) {
                    movingVector.y = 1;
                    player.state = Player.State.Walking;
                    player.facing = Player.Facing.Up;
                }

                if (moveDown) {
                    movingVector.y = -1;
                    player.state = Player.State.Walking;
                    player.facing = Player.Facing.Down;
                }
            }
            movingVector.nor();

            if (Gdx.input.isKeyPressed(Input.Keys.C)) {
                player.state = Player.State.Sneaking;
                player.currentSpeedMultiplier = player.sneakMultiplier;
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
                player.state =  Player.State.Running;
                player.currentSpeedMultiplier = player.runMultiplier;
            } else {
                player.currentSpeedMultiplier = player.normalSpeedMultiplier;
            }

            movingImpulse = movingVector.scl(player.currentSpeedMultiplier * player.normalSpeed * player.getBody().getMass() *  player.getBody().getLinearDamping() * GameState.instance.physicsStep);
            player.getBody().applyLinearImpulse(movingImpulse, zeroVector, true);

        }
        if (Math.abs(player.getBody().getLinearVelocity().len2()) < 0.5f) {
            player.state = Player.State.Standing;
        }
        //update selection of object to interact with
        if (player.closestObject != null){
            player.playerObject.getBehaviour(PlayerCollisionBehaviour.class).updatePlayerClosestObject();
        }
    }

    public void cancelReload(){
        player.isReloading = false;
    }
}
