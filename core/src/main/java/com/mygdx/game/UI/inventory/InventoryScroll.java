package com.mygdx.game.UI.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class InventoryScroll extends ScrollPane {
    public InventoryScroll(Actor actor, Skin skin) {
        super(actor, skin);
    }

    @Override
    public void setPosition(float x, float y) {

        super.setPosition(x, y);
    }
}
