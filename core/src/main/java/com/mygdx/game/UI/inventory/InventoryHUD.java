package com.mygdx.game.UI.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.*;
import com.mygdx.game.UI.HUD;

public class InventoryHUD extends Table {
    HUD hud;
    Player player;
    Array<Actor> invPopups = new Array<>();
    public InventoryHUD(HUD hud, Player player, float x, float y){
        super(SecondGDXGame.skin);
        this.player = player;
        this.hud = hud;
        //this.setFillParent(true);
        this.setTouchable(Touchable.enabled);
        this.setBackground("default-pane");
        //this.setSize(400,300);

        refill();

        this.align(Align.top);
        this.pad(5);
        this.setPosition(x,y-getHeight());
    }

    public void refill(){
        this.clearChildren();
        ItemEntry itemEntry;
        for (Item curItem : player.getInventoryItems()){
            itemEntry = new ItemEntry(this, curItem);
            add(itemEntry).growX().align(Align.left);

            row().padTop(2);
        }
    }

    public void showItemContextMenu(ItemEntry itemEntry){
        Vector3 mousePosition = hud.getCamera().unproject(new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0));
        ContextMenu contextMenu = new ContextMenu(hud,this, itemEntry, mousePosition.x, mousePosition.y);
        hud.esClosablePopups.add(contextMenu);
        hud.addActor(contextMenu);
    }

    public void removeActor(ContextMenu contextMenu){
        contextMenu.remove();
        hud.esClosablePopups.removeValue(contextMenu, true);
    }

    public void putItemFromInventory(ItemEntry itemEntry){
        player.getInventoryItems().removeValue(itemEntry.item, true);
        refill();
        itemEntry.item.allocate(hud.gameItself.world, player.body.getPosition());
    }

    public void contextAction(ContextMenu.ConAction action, ContextMenu contextMenu){
        switch (action){
            case PUT -> {
                putItemFromInventory(contextMenu.itemEntry);
                closeItemContextMenu(contextMenu);
            }
        }
    }

    public void closeItemContextMenu(ContextMenu contextMenu){
        hud.getActors().removeValue(contextMenu, true);
        if (contextMenu.hideListener != null)
            hud.removeCaptureListener(contextMenu.hideListener);
        hud.esClosablePopups.removeValue(contextMenu, true);
    }

    @Override
    public void setPosition(float x, float y) {
        for (Actor invPopup : invPopups){
            invPopup.setPosition(x-this.getX(), y-this.getY());
        }
        super.setPosition(x, y);
    }
}
