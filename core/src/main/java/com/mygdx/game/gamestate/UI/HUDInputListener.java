package com.mygdx.game.gamestate.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mygdx.game.gamestate.objects.Item;
import com.mygdx.game.gamestate.objects.bodies.player.PlayerHandler;
import com.mygdx.game.gamestate.tiledmap.Door;

import static com.mygdx.game.gamestate.GameState.Instance;

public class HUDInputListener extends InputListener {
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        super.touchDown(event, x, y, pointer, button);
        return false;
    }

    @Override
    public boolean keyUp (InputEvent event, int keycode) {
        if (keycode == Input.Keys.ESCAPE)
            if (Instance.hudStage.esClosablePopups.notEmpty()){
                Instance.hudStage.closeTopPopup();
            }
            else Instance.game.setScreen(Instance.game.menuScreen);
        if (keycode == Input.Keys.B){
            Instance.debug = !Instance.debug;
            Instance.hudStage.setDebugAll(Instance.debug);
            Instance.gameStage.setDebugAll(Instance.debug);
        }
        if (keycode == Input.Keys.EQUALS){
            Instance.zoom += 0.3f;
            Instance.camera.setToOrtho(false, Gdx.graphics.getWidth() * (1f/ Instance.TILE_SIDE) * (1/Instance.zoom),
                    Gdx.graphics.getHeight() * (1f / Instance.TILE_SIDE) * (1 / Instance.zoom));
        }
        if (keycode == Input.Keys.MINUS){
            Instance.zoom -= 0.3f;
            Instance.camera.setToOrtho(false, Gdx.graphics.getWidth() * (1f/ Instance.TILE_SIDE) * (1/Instance.zoom),
                    Gdx.graphics.getHeight() * (1f/ Instance.TILE_SIDE) * (1/Instance.zoom));
        }
        if (keycode == Input.Keys.R){
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                Instance.player.revive();
                return false;
            }
        }
        if (keycode == Input.Keys.E){
            if (Instance.player.closestObject != null) {
                var obj = Instance.player.closestObject.getUserData();
                if (obj instanceof Door) {
                    ((Door) obj).doAction();
                }
                if (obj instanceof Item){
                    Instance.player.pickupItem((Item) obj);
                    Instance.hudStage.updateInvHUDContent();
                }
            }
        }
        if (keycode == Input.Keys.I){
            Instance.hudStage.toggleInventoryHUD();
        }
        if (keycode == Input.Keys.H){
            Instance.player.freeHands();
        }
        if (keycode == Input.Keys.T){
            if (Instance.player.equipedItem != null && Instance.player.equipedItem.itemName.equals("Deagle .44"))
                Instance.fireBullet(Instance.player);
        }
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP){
            Instance.player.playerObject.getBehaviour(PlayerHandler.class).moveUp = false;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN){
            Instance.player.playerObject.getBehaviour(PlayerHandler.class).moveDown = false;
        }
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT){
            Instance.player.playerObject.getBehaviour(PlayerHandler.class).moveToTheLeft = false;
        }
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT){
            Instance.player.playerObject.getBehaviour(PlayerHandler.class).moveToTheRight = false;
        }
        return false;
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP){
            Instance.player.playerObject.getBehaviour(PlayerHandler.class).moveUp = true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN){
            Instance.player.playerObject.getBehaviour(PlayerHandler.class).moveDown = true;
        }
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT){
            Instance.player.playerObject.getBehaviour(PlayerHandler.class).moveToTheLeft = true;
        }
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT){
            Instance.player.playerObject.getBehaviour(PlayerHandler.class).moveToTheRight = true;
        }
        return false;
    }
}
