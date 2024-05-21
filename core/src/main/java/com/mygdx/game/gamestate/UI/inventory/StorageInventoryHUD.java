package com.mygdx.game.gamestate.UI.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
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
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.objects.tiles.Storage;

public class StorageInventoryHUD extends ScrollPane implements InventoryHUD{
    HUD hud;
    public Storage storage;
    Array<Actor> popups = new Array<>();
    Skin skin;
    Table table;
    public ContextMenu contextMenu;

    public void refill(){
        table.clearChildren();
        ItemEntry itemEntry;
        for (Item curItem : storage.getInventoryItems()){
            itemEntry = new ItemEntry(this, curItem);
            table.add(itemEntry).growX().align(Align.left);

            table.row().padTop(2);
        }
    }

    public void showItemContextMenu(ItemEntry itemEntry){
        Vector3 mousePosition = hud.getCamera().unproject(new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0));
        contextMenu.update();
        contextMenu.setPosition(itemEntry, mousePosition.x, mousePosition.y);
        contextMenu.setVisible(true);
        contextMenu.setZIndex(hud.getActors().size+1);

        popups.add(contextMenu);
        hud.showPopup(contextMenu);
    }

    public void closeItemContextMenu(ContextMenu contextMenu){
        popups.removeValue(contextMenu, true);
        contextMenu.setVisible(false);
        hud.closePopup(contextMenu);
    }

    public boolean contextAction(ContextMenu.ConAction action, ContextMenu contextMenu){
        boolean handled = true;
        switch (action){
            case Put -> {
                putItemFromInventory(contextMenu.itemEntry);
            }
            case Equip -> {
                contextAction(ContextMenu.ConAction.Take, contextMenu);
                GameState.instance.player.equipItem(contextMenu.itemEntry.item);
            }
            case Description -> {}
            case Take -> {
                GameState.instance.player.takeItem(contextMenu.itemEntry.item);
                storage.dropItem(contextMenu.itemEntry.item);
            }
            default -> handled = false;
        }
        closeItemContextMenu(contextMenu);
        return handled;
    }

    public void putItemFromInventory(ItemEntry itemEntry){
        storage.dropItem(itemEntry.item);
        itemEntry.item.allocate(GameState.instance.player.getPosition());
    }

    public void onClose(){
        for (Actor ac: popups){
            if (ac instanceof ContextMenu){
                closeItemContextMenu( (ContextMenu) ac);
            }
        }
    }

    public void onPositionChanged(Vector2 offset){
        for (Actor invPopup : popups){
            invPopup.setPosition(invPopup.getX() + offset.x, invPopup.getY() + offset.y);
        }
    }

    @Override
    public void setPosition(float x, float y, int align) {
        float offsetX = x-this.getX(), offsetY = y-this.getY();
        onPositionChanged(new Vector2(offsetX, offsetY));
        super.setPosition(x, y, align);
    }

    public void onShow(Storage storage){
        this.storage = storage;
        refill();
    }

    public StorageInventoryHUD(HUD hud, ContextMenu.ConAction... actions) {
        super(null, SecondGDXGame.skin);
        skin = SecondGDXGame.skin;
        this.hud = hud;
        ScrollPane.ScrollPaneStyle sps = this.getStyle();
        sps.background = skin.getDrawable("default-pane");
        setSize(400,300);
        setName("StorageInventoryScrollPane");
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

        contextMenu = new ContextMenu(hud, this, actions);
        contextMenu.setVisible(false);
        hud.addActor(contextMenu);

        setActor(table);
    }

    @Override
    public float getPrefWidth() {
        return getWidth();
    }

    @Override
    public float getPrefHeight() {
        return getHeight();
    }
}
