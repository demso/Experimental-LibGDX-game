package com.mygdx.game.gamestate;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import static com.mygdx.game.gamestate.GameState.instance;

public class GameStageInputListener extends InputListener {
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (button == Input.Buttons.LEFT)
            if (instance.player.equipedItem != null &&  instance.player.equipedItem.getName().equals("Deagle .44"))
                instance.player.fire();
        return false;
    }
}
