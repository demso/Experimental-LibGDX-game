package com.mygdx.game.gamestate.UI.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.UI.HUD;

public class InventoryTools extends Table {
    HUD hud;
    ButtonsPanel buttons;
    public InventoryTools(HUD hud) {
        super(SecondGDXGame.skin);
        this.hud = hud;
        buttons = new ButtonsPanel(hud, ButtonsPanel.Action.StoreAll, ButtonsPanel.Action.TakeAll);

        add(buttons).align(Align.topLeft);
        align(Align.topLeft);
        pack();
    }

    public void update() {
        buttons.update();
    }

    float minHeight;

    @Override
    public void setHeight(float height) {
        minHeight = height;
    }

    @Override
    public float getMinHeight() {
        return minHeight;
    }
}
