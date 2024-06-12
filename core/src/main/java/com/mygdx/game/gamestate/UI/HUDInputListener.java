package com.mygdx.game.gamestate.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.gamestate.player.ClientPlayerHandler;

import static com.mygdx.game.gamestate.GameState.instance;

public class HUDInputListener extends InputListener {
//    @Override
//    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//        if (button == Input.Buttons.LEFT)
//            if (instance.player.equipedItem != null && instance.player.equipedItem.itemName.equals("Deagle .44"))
//                instance.player.fire();
//        return false;
//    }

    @Override
    public boolean keyUp (InputEvent event, int keycode) {
        if (keycode == Input.Keys.ESCAPE){
            if (!instance.hud.closeTopPopup())
                Gdx.app.exit();
        }
        if (keycode == Input.Keys.B){
            instance.debug = !instance.debug;
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
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
            if (!RLongPressed){
                instance.clientPlayer.reload();
            }
            RLongPressed = false;
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                instance.clientPlayer.revive();
                return false;
            }
        }
        if (keycode == Input.Keys.E){
            instance.clientPlayer.interact();
        }
        if (keycode == Input.Keys.I){
            instance.hud.togglePlayerInventoryHUD();
        }
        if (keycode == Input.Keys.T){
            if (instance.clientPlayer.equipedItem instanceof Gun gun)
                if (gun.getFireType().equals(Gun.FireType.SEMI_AUTO))
                    instance.clientPlayer.fire();
        }
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP){
            instance.clientPlayer.playerObject.getBehaviour(ClientPlayerHandler.class).moveUp = false;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN){
            instance.clientPlayer.playerObject.getBehaviour(ClientPlayerHandler.class).moveDown = false;
        }
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT){
            instance.clientPlayer.playerObject.getBehaviour(ClientPlayerHandler.class).moveToTheLeft = false;
        }
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT){
            instance.clientPlayer.playerObject.getBehaviour(ClientPlayerHandler.class).moveToTheRight = false;
        }
        if (keycode == Input.Keys.H){
            instance.clientPlayer.autoHeal();
        }
        if (keycode == Input.Keys.G){
            instance.clientPlayer.throwGrenade(System.currentTimeMillis() - GPressedTime);
        }
        return false;
    }
    long RPressedTime = 0;
    boolean RLongPressed;
    long GPressedTime = 0;
    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP){
            instance.clientPlayer.playerObject.getBehaviour(ClientPlayerHandler.class).moveUp = true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN){
            instance.clientPlayer.playerObject.getBehaviour(ClientPlayerHandler.class).moveDown = true;
        }
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT){
            instance.clientPlayer.playerObject.getBehaviour(ClientPlayerHandler.class).moveToTheLeft = true;
        }
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT){
            instance.clientPlayer.playerObject.getBehaviour(ClientPlayerHandler.class).moveToTheRight = true;
        }
        if (keycode == Input.Keys.R){
            if (Gdx.input.isKeyPressed(Input.Keys.R)) {
                RPressedTime = System.nanoTime();
                RLongPressed = false;
            }
            //HandyHelper.instance.log(System.currentTimeMillis() + " ", false);
        }
        if (keycode == Input.Keys.G){
            GPressedTime = System.currentTimeMillis();
        }

        return false;
    }

    public void update(){
        if (Gdx.input.isKeyPressed(Input.Keys.R) && (System.nanoTime() - RPressedTime > 400000000L) && !RLongPressed) {
            if (instance.clientPlayer.equipedItem != null)
                instance.clientPlayer.uneqipItem();
            RLongPressed = true;
        }
        //HandyHelper.instance.log(System.nanoTime() + " " + RPressedTime + " " + (System.nanoTime() - RPressedTime), false);

    }

}
