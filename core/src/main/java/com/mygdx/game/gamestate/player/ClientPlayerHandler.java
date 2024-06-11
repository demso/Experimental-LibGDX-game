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
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.net.messages.client.PlayerMove;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class ClientPlayerHandler extends BehaviourAdapter implements PlayerMoveReceiver {
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

    ClientPlayer player;

    public ClientPlayerHandler(GameObject gameObject, Player player) {
        super(gameObject);
        setRenderOrder(Globals.PLAYER_RENDER_ORDER);
    }

    Vector2 movingVector = new Vector2();
    Vector2 movingImpulse = new Vector2();

    Vector2 zeroVector = new Vector2(0, 0);

    boolean needsUpdate;

    PlayerMove playerMove;

    float accumulator;

    ReloadAnimation reloadAnimation;
    Sprite reloadSprite;

    @Override
    public void start() {
        reloadAnimation = new ReloadAnimation();
        reloadSprite = new Sprite(new Texture(Gdx.files.internal("visual/textures/reload_icon.png")));
        reloadSprite.setSize(0.5f, 0.5f);
        reloadSprite.setOriginCenter();
    }

    @Override
    public void update(float delta) {
        accumulator += delta;

        if (accumulator >= Globals.SERVER_UPDATE_TIME){
            SecondGDXGame.instance.client.sendPlayerMove(GameState.instance.clientPlayer.getId(), player.getPosition(), player.getBody().getLinearVelocity());
            accumulator = 0;
        }

        if (needsUpdate && playerMove != null){
            player.getBody().setTransform(playerMove.x, playerMove.y, player.getBody().getTransform().getRotation());
            player.getBody().setLinearVelocity(playerMove.xSpeed, playerMove.ySpeed);
            needsUpdate = false;
            playerMove = null;
        }

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

        if (player.needsReload && !player.isReloading) {
            player.needsReload = false;
            player.isReloading = true;
            reloadAnimation.start(10f);
        }

        if (player.isReloading){
            Vector2 pos = player.getPosition();
            reloadSprite.setPosition(pos.x + 0.5f, pos.y + 0.5f );
            reloadAnimation.updateAndTransform(delta,reloadSprite);
            if (reloadAnimation.isFinished()){
                player.isReloading = false;
            }
        }

    }

    @Override
    public void render(Batch batch) {
        if (player.isReloading)
            reloadSprite.draw(batch);
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
    }

    byte indexer = 0;

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

//        if (indexer == 2){
//            SecondGDXGame.instance.client.sendPlayerMove(SecondGDXGame.instance.name, player.getPosition(), player.getBody().getLinearVelocity());
//            indexer = 0;
//        }
//        indexer += 1;
    }

    @Override
    public void receivePlayerUpdate(PlayerMove move) {
//        Vector2 pos = player.getBody().getPosition();
//        Vector2 speed = player.getBody().getLinearVelocity();
//        if (Math.abs(pos.x - move.x) < 0.05 && Math.abs(pos.y - move.y) < 0.05
//                && Math.abs(speed.x - move.xSpeed) < 0.05 && Math.abs(speed.y - move.ySpeed) < 0.05)
//            return;
//        needsUpdate = true;
//        playerMove = move;
    }
}
