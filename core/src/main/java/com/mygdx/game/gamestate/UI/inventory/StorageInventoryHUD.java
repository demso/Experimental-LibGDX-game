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
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.objects.tiles.Storage;

import static com.mygdx.game.gamestate.GameState.instance;

public class StorageInventoryHUD extends ScrollPane implements InventoryHUD{
    HUD hud;
    protected Storage storage;
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
            case Drop -> {
                putItemFromInventory(contextMenu.itemEntry);
                Vector2 pos = GameState.instance.clientPlayer.getPosition();
                instance.client.droppedItem(contextMenu.itemEntry.item, (int)Math.floor(pos.x), (int)Math.floor(pos.y));
            }
            case Equip -> {
                contextAction(ContextMenu.ConAction.Take, contextMenu);
                instance.clientPlayer.equipItem(contextMenu.itemEntry.item);
            }
            case Description -> {}
            case Take -> {
                GameState.instance.clientPlayer.takeItem(contextMenu.itemEntry.item);
                storage.removeItem(contextMenu.itemEntry.item);
                Vector2 pos = storage.getPosition();
                instance.client.tookItemFromStorage(contextMenu.itemEntry.item, (int)Math.floor(pos.x), (int)Math.floor(pos.y));
            }
            default -> handled = false;
        }
        closeItemContextMenu(contextMenu);
        return handled;
    }

    public void putItemFromInventory(ItemEntry itemEntry){
        storage.removeItem(itemEntry.item);
        instance.hud.updateInvHUDContent();
        itemEntry.item.allocate(GameState.instance.clientPlayer.getPosition());
    }

    public void onClose(){
        for (Actor ac: popups){
            if (ac instanceof ContextMenu){
                closeItemContextMenu( (ContextMenu) ac);
            }
        }
        hud.gameState.client.stopStorageUpdate(storage.getPosition().x, storage.getPosition().y);
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
        //setStorage(storage);
        refill();
        //hud.gameState.client.getStoredItems(storage.getPosition().x, storage.getPosition().y);
    }

    public void requestShowOnStorage(Storage storage){
        if (this.storage != null){
            hud.closeStorageInventoryHUD(false);
        }
        this.storage = storage;
        instance.client.needsStorageUpdate(storage.getPosition().x, storage.getPosition().y);
    }

    public Storage getStorage(){
        return storage;
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
