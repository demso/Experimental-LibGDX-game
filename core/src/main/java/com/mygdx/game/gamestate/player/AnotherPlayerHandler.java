package com.mygdx.game.gamestate.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.client.PlayerMove;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;

public class AnotherPlayerHandler extends BehaviourAdapter implements PlayerMoveReceiver {

    float stateTime = 0;

    Animation<TextureRegion> walkSide;
    Animation<TextureRegion> walkUp;
    Animation<TextureRegion> walkDown;

    TextureRegion currentFrame;

    float frameDuration = 0.1f;

    Player player;

    boolean needsUpdate;

    PlayerMove playerMove;

    public AnotherPlayerHandler(Player player) {
        super(player.playerObject);
        setRenderOrder(Globals.ANOTHER_PLAYER_RENDER_ORDER);
    }

    Vector2 tempVec = new Vector2();
    Vector2 velocity = new Vector2();

    @Override
    public void update(float delta) {
        Vector2 pos = player.getBody().getPosition();
        if (playerMove != null) {
            float offsetX = Math.abs(playerMove.x - pos.x), offsetY = Math.abs(playerMove.y - pos.y);
            if (needsUpdate) {
                tempVec.set(playerMove.xSpeed, playerMove.ySpeed);
                velocity.set(tempVec);

                player.itemRotation = playerMove.rotation;

                needsUpdate = false;
            }

            if (offsetX > 0.5 || offsetY > 0.5) player.setPosition(playerMove.x, playerMove.y);
            else if (offsetX >= 0.035f || offsetY >= 0.035f)
                tempVec.add(Math.signum(playerMove.x - pos.x) / 2, Math.signum(playerMove.y - pos.y) / 2);
            else if (offsetX >= 0.01f || offsetY >= 0.01f) tempVec.add((playerMove.x - pos.x), (playerMove.y - pos.y));

            player.getBody().setLinearVelocity(tempVec);
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


    }

    @Override
    public void render(Batch batch) {
        if (Math.abs(player.getVelocity().len2()) < 0.5f) {
            player.state = Player.State.Standing;
        } else {
            Vector2 vel = player.getVelocity();
            float velX = Math.abs(vel.x);
            float velY = Math.abs(vel.y);
            player.state = Player.State.Walking;
            if (velX > velY && Math.abs(velX - velY) > 0.3f)
                if (vel.x > 0.3f)
                    player.setFacing(Player.Facing.Right);
                else
                    player.setFacing(Player.Facing.Left);
            else
                if (vel.y > 0.3f)
                    player.setFacing(Player.Facing.Up);
                else
                    player.setFacing(Player.Facing.Down);
        }

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

    @Override
    public void receivePlayerUpdate(PlayerMove move) {
        needsUpdate = true;
        playerMove = move;
    }
}
