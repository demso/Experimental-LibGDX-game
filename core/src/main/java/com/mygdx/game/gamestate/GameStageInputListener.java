package com.mygdx.game.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mygdx.game.gamestate.objects.items.guns.Gun;

import static com.mygdx.game.gamestate.GameState.instance;

public class GameStageInputListener extends InputListener {
    public boolean mouseHandled = false;
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (button == Input.Buttons.LEFT)
            if (instance.clientPlayer.equipedItem instanceof Gun gun && gun.getFireType().equals(Gun.FireType.SEMI_AUTO))
                instance.clientPlayer.fire();

        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT || button == Input.Buttons.MIDDLE)
            mouseHandled = true;

        return true;
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT || button == Input.Buttons.MIDDLE)
            mouseHandled = false;
    }


    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {

        return false;
    }



    public void update(float delta){
        if (mouseHandled)
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
                if (instance.clientPlayer.equipedItem instanceof Gun gun)
                    if (gun.getFireType().equals(Gun.FireType.AUTO))
                        synchronized (instance.world) {
                            instance.clientPlayer.fire();
                        }
            }
    }
}
