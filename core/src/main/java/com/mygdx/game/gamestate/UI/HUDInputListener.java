package com.mygdx.game.gamestate.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.player.PlayerHandler;

import static com.mygdx.game.gamestate.GameState.instance;

public class HUDInputListener extends InputListener {
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        super.touchDown(event, x, y, pointer, button);
        return false;
    }

    @Override
    public boolean keyUp (InputEvent event, int keycode) {
        if (keycode == Input.Keys.ESCAPE)
            if (!instance.hud.closeTopPopup())
                Gdx.app.exit();
        if (keycode == Input.Keys.B){
            instance.debug = !instance.debug;
            instance.hud.setDebugAll(instance.debug);
            instance.gameStage.setDebugAll(instance.debug);
        }
        if (keycode == Input.Keys.EQUALS){
            instance.zoom += 0.3f;
            instance.camera.setToOrtho(false, Gdx.graphics.getWidth() * (1f/ instance.TILE_SIDE) * (1/ instance.zoom),
                    Gdx.graphics.getHeight() * (1f / instance.TILE_SIDE) * (1 / instance.zoom));
        }
        if (keycode == Input.Keys.MINUS){
            instance.zoom -= 0.3f;
            instance.camera.setToOrtho(false, Gdx.graphics.getWidth() * (1f/ instance.TILE_SIDE) * (1/ instance.zoom),
                    Gdx.graphics.getHeight() * (1f/ instance.TILE_SIDE) * (1/ instance.zoom));
        }
        if (keycode == Input.Keys.R){
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                instance.player.revive();
                return false;
            }
        }
        if (keycode == Input.Keys.E){
            if (instance.player.closestObject != null) {
                var obj = (Interactable) instance.player.closestObject.getUserData();
                obj.interact(instance.player);
            }
        }
        if (keycode == Input.Keys.I){
            instance.hud.togglePlayerInventoryHUD();
        }
        if (keycode == Input.Keys.H){
            instance.player.freeHands();
        }
        if (keycode == Input.Keys.T){
            if (instance.player.equipedItem != null && instance.player.equipedItem.itemName.equals("Deagle .44"))
                instance.fireBullet(instance.player);
        }
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP){
            instance.player.playerObject.getBehaviour(PlayerHandler.class).moveUp = false;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN){
            instance.player.playerObject.getBehaviour(PlayerHandler.class).moveDown = false;
        }
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT){
            instance.player.playerObject.getBehaviour(PlayerHandler.class).moveToTheLeft = false;
        }
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT){
            instance.player.playerObject.getBehaviour(PlayerHandler.class).moveToTheRight = false;
        }
        return false;
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP){
            instance.player.playerObject.getBehaviour(PlayerHandler.class).moveUp = true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN){
            instance.player.playerObject.getBehaviour(PlayerHandler.class).moveDown = true;
        }
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT){
            instance.player.playerObject.getBehaviour(PlayerHandler.class).moveToTheLeft = true;
        }
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT){
            instance.player.playerObject.getBehaviour(PlayerHandler.class).moveToTheRight = true;
        }
        return false;
    }
}
