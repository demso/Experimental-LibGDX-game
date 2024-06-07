package com.mygdx.game.gamestate;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import static com.mygdx.game.gamestate.GameState.instance;

public class GameStageInputListener extends InputListener {
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (button == Input.Buttons.LEFT)
            if (instance.clientPlayer.equipedItem != null &&  instance.clientPlayer.equipedItem.getName().equals("Deagle .44"))
                instance.clientPlayer.fire();
        return false;
    }
}
