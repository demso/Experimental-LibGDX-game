package com.mygdx.game.gamestate.UI.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.tiles.Storage;

import static com.mygdx.game.gamestate.GameState.instance;

public class PlayerInventoryHUD extends StorageInventoryHUD{

    public void storageInventoryNear(){
        contextMenu.enableActions(ContextMenu.Action.Store);
        if (contextMenu.isVisible())
            contextMenu.update();
    }

    @Override
    public boolean contextAction(ContextMenu.Action action, ContextMenu contextMenu) {
        boolean handled = true;
        switch (action) {
            case Store -> {
                storeAction(contextMenu.itemEntry.item);
            }
            case Equip -> {
                GameState.instance.clientPlayer.equipItem(contextMenu.itemEntry.item);
            }
            default -> handled = false;
        }
        if (handled){
            closeItemContextMenu(contextMenu);
            return true;
        }
        return super.contextAction(action, contextMenu);
    }

    public void storeAction(Item item){
        if (item == null || !storage.getInventoryItems().contains(item, true))
            return;
        GameState.instance.clientPlayer.removeItem(item);
        hud.storageInventoryHUD.getStorage().takeItem(item);
        instance.hud.updateInvHUDContent();
    }

    public void storageInventoryFar(){
        contextMenu.disableActions(ContextMenu.Action.Store);
        if (contextMenu.isVisible())
            contextMenu.update();
    }

    @Override
    public void onShow(Storage storage) {
        requestShowOnStorage(storage);
        refill();
    }

    @Override
    public void requestShowOnStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void onClose() {
        for (Actor ac: popups){
            if (ac instanceof ContextMenu){
                closeItemContextMenu( (ContextMenu) ac);
            }
        }
    }

    public PlayerInventoryHUD(HUD hud) {
        super(hud,  ContextMenu.Action.Store, ContextMenu.Action.Equip, ContextMenu.Action.Drop, ContextMenu.Action.Description);
        setName("PlayerInventoryScrollPane");
        contextMenu.disableActions(ContextMenu.Action.Store);
    }
}
