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
import lombok.Getter;

import static com.mygdx.game.gamestate.GameState.instance;

public class StorageInventoryHUD extends ScrollPane implements InventoryHUD{
    HUD hud;
    @Getter
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

    public boolean contextAction(ContextMenu.Action action, ContextMenu contextMenu){
        boolean handled = true;
        switch (action){
            case Drop -> {
                putItemFromInventory(contextMenu.itemEntry);
                Vector2 pos = GameState.instance.clientPlayer.getPosition();
                instance.client.droppedItem(contextMenu.itemEntry.item, pos);
            }
            case Equip -> {
                contextAction(ContextMenu.Action.Take, contextMenu);
                instance.clientPlayer.equipItem(contextMenu.itemEntry.item);
            }
            case Description -> {}
            case Take -> {
                takeAction(contextMenu.itemEntry.item);
            }
            default -> handled = false;
        }
        closeItemContextMenu(contextMenu);
        return handled;
    }

    public void takeAction(Item item) {
        if (item == null || !storage.getInventoryItems().contains(item, true))
            return;
        storage.removeItem(item);
        GameState.instance.clientPlayer.takeItem(item);

        Vector2 pos = storage.getPosition();
        instance.hud.updateInvHUDContent();//todo update when items are taken or stored from server not on client
        instance.client.tookItemFromStorage(item, (int)Math.floor(pos.x), (int)Math.floor(pos.y));
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
        for (Item item : storage.getInventoryItems()){
            item.dispose();
        }
        storage.getInventoryItems().clear();
        storage.setInventoryItems((Item[]) null);
        hud.gameState.client.stopStorageUpdate(storage.getPosition().x, storage.getPosition().y);
    }
    public void onPositionChanged(Vector2 offset){
        for (Actor invPopup : popups){
            invPopup.setPosition(invPopup.getX() + offset.x, invPopup.getY() + offset.y);
        }
    }

    public void onPositionChanging(){
        setSize(400,Gdx.graphics.getHeight() * 0.7f);
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

    public StorageInventoryHUD(HUD hud, ContextMenu.Action... actions) {
        super(null, SecondGDXGame.skin);
        skin = SecondGDXGame.skin;
        this.hud = hud;
        ScrollPane.ScrollPaneStyle sps = this.getStyle();
        sps.background = skin.getDrawable("default-pane");
         ;
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
