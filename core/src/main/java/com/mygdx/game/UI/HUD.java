package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.GameItself;
import com.mygdx.game.SecondGDXGame;

public class HUD extends Stage {
    InventoryHUD invHUD;
    boolean isInventoryShowed;
    GameItself gameItself;

    public HUD(GameItself gi, ScreenViewport screenViewport, SpriteBatch batch) {
        super(screenViewport, batch);
        gameItself = gi;
    }

    public void showInfoWindow(){

    }

    public void showInventoryHUD(){
        invHUD = new InventoryHUD(gameItself.player, 0,0);
        invHUD.setPosition((Gdx.graphics.getWidth()-invHUD.getWidth())/2f,(Gdx.graphics.getHeight()-invHUD.getHeight())/2f, Align.bottomLeft);
        addActor(invHUD);
        isInventoryShowed = true;
    }

    public void closeInventoryHUD(){
        getActors().removeValue(invHUD, true);
        isInventoryShowed = false;
    }

    public void toggleInventoryHUD(){
        if (isInventoryShowed)
            closeInventoryHUD();
        else
            showInventoryHUD();
    }
}
