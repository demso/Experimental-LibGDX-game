package com.mygdx.game.gamestate.UI.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.Item;
import com.mygdx.game.gamestate.objects.bodies.player.Player;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.UI.HUD;

public class InventoryHUD extends ScrollPane {
    HUD hud;
    public Player player;
    Array<Actor> popups = new Array<>();
    Skin skin;
    Table table;

    public void refill(){
        table.clearChildren();
        ItemEntry itemEntry;
        for (Item curItem : player.getInventoryItems()){
            itemEntry = new ItemEntry(this, curItem);
            table.add(itemEntry).growX().align(Align.left);

            table.row().padTop(2);
        }
    }

    public void showItemContextMenu(ItemEntry itemEntry){
        Vector3 mousePosition = hud.getCamera().unproject(new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0));
        ContextMenu contextMenu = new ContextMenu(hud,this, itemEntry, mousePosition.x, mousePosition.y);

        popups.add(contextMenu);
        hud.esClosablePopups.add(contextMenu);
        hud.addActor(contextMenu);
    }

    public void putItemFromInventory(ItemEntry itemEntry){
        player.removeItemFromInventory(itemEntry.item);
        refill();
        itemEntry.item.allocate(player.getPosition());
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
        popups.removeValue(contextMenu, true);
        hud.getActors().removeValue(contextMenu, true);
        if (contextMenu.hideListener != null)
            hud.removeCaptureListener(contextMenu.hideListener);
        hud.esClosablePopups.removeValue(contextMenu, true);
    }

    public void onClose(){
        for (Actor ac: popups){
            if (ac instanceof ContextMenu){
                closeItemContextMenu( (ContextMenu) ac);
            }
        }
    }

    @Override
    public void setPosition(float x, float y, int align) {
        float offsetX = x-this.getX(), offsetY = y-this.getY();
        for (Actor invPopup : popups){
            invPopup.setPosition(invPopup.getX() + offsetX, invPopup.getY() + offsetY);
        }
        super.setPosition(x, y, align);
    }

    public InventoryHUD(HUD hud, Player player) {
        super(null, SecondGDXGame.skin);
        skin = SecondGDXGame.skin;
        this.player = player;
        this.hud = hud;
        ScrollPane.ScrollPaneStyle sps = this.getStyle();
        sps.background = skin.getDrawable("default-pane");
        setSize(400,300);
        setName("InventoryScrollPane");
        setFadeScrollBars(false);
        addListener(new InputListener(){
            @Override
            public boolean handle(Event e){
                super.handle(e);
                return true;
            }
        });

        table = new Table(SecondGDXGame.skin);

        table.setTouchable(Touchable.enabled);
        table.setBackground("default-pane");

        table.align(Align.top);
        table.pad(5);

        setActor(table);
    }
}
