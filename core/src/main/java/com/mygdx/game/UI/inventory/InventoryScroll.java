package com.mygdx.game.UI.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.Player;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.UI.HUD;

public class InventoryScroll extends ScrollPane {
    HUD hud;
    Player player;
    InventoryHUD inventoryHUD;
    Skin skin;
    public InventoryScroll(HUD hud, Player player) {
        super(null, SecondGDXGame.skin);
        skin = SecondGDXGame.skin;
        inventoryHUD = new InventoryHUD(hud, player,0, 0);
        ScrollPane.ScrollPaneStyle sps = this.getStyle();
        sps.background = skin.getDrawable("default-pane");

        setName("InventoryScrollPane");
        setFadeScrollBars(false);
        //invScroll.setBackground("default-pane");
        addListener(new InputListener(){
            @Override
            public boolean handle(Event e){
                super.handle(e);
                return true;
            }
        });
    }

    public void refill(){
        inventoryHUD.refill();
    }

    public void showItemContextMenu(ItemEntry itemEntry){
        inventoryHUD.showItemContextMenu(itemEntry);
    }

    public void removeActor(ContextMenu contextMenu){
        inventoryHUD.removeActor(contextMenu);
    }

    public void putItemFromInventory(ItemEntry itemEntry){
        inventoryHUD.putItemFromInventory(itemEntry);
    }

//    public void contextAction(ContextMenu.ConAction action, ContextMenu contextMenu){
//        inventoryHUD.contextAction();
//    }

    public void closeItemContextMenu(ContextMenu contextMenu){
        inventoryHUD.closeItemContextMenu(contextMenu);
    }

    @Override
    public void setPosition(float x, float y) {

        super.setPosition(x, y);
    }
}
