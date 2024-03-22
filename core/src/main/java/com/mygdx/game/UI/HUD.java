package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.GameItself;
import com.mygdx.game.Item;

public class HUD extends Stage {
    InventoryHUD invHUD;
    boolean isInventoryShowed;
    GameItself gameItself;
    ObjectMap<Item, ItemInfoPopUp> itemPopups =  new ObjectMap<>();
    public Array<InventoryHUD> esClosablePopups = new Array<>();

    public HUD(GameItself gi, ScreenViewport screenViewport, SpriteBatch batch) {
        super(screenViewport, batch);
        gameItself = gi;

    }

    public void showItemInfoWindow(Item item){
        getActors().removeValue(itemPopups.get(item), true);
        Vector3 mousePosition = getCamera().unproject(new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0));
        Vector3 itemPos = getCamera().unproject(gameItself.gameStage.getCamera().project(new Vector3(item.position, 0)));
        ItemInfoPopUp popup = new ItemInfoPopUp(item,itemPos.x,Gdx.graphics.getHeight()-itemPos.y);
        addActor(popup);
        itemPopups.put(item, popup);
    }

    public void hideItemInfoWindow(Item item){
        getActors().removeValue(itemPopups.get(item), true);
    }

    public void showInventoryHUD(){
        invHUD = new InventoryHUD(gameItself.player, 0,0);
        invHUD.setPosition((Gdx.graphics.getWidth()-invHUD.getWidth())/2f,(Gdx.graphics.getHeight()-invHUD.getHeight())/2f, Align.bottomLeft);
        addActor(invHUD);
        isInventoryShowed = true;
        esClosablePopups.add(invHUD);
    }

    public void closeInventoryHUD(){
        getActors().removeValue(invHUD, true);
        isInventoryShowed = false;
        esClosablePopups.removeValue(invHUD, true);
    }

    public void toggleInventoryHUD(){
        if (isInventoryShowed)
            closeInventoryHUD();
        else
            showInventoryHUD();
    }

    public void updateInvHUD(){
        if (invHUD != null)
            invHUD.refill();
    }

    public void updateOnResize(){

    }
}
