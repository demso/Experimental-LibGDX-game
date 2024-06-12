package com.mygdx.game.gamestate.UI.inventory;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.objects.tiles.Storage;

import static com.mygdx.game.gamestate.GameState.instance;

public class PlayerInventoryHUD extends StorageInventoryHUD{

    public void storageInventoryNear(){
        contextMenu.enableActions(ContextMenu.ConAction.Store);
        if (contextMenu.isVisible())
            contextMenu.update();
    }

    @Override
    public boolean contextAction(ContextMenu.ConAction action, ContextMenu contextMenu) {
        boolean handled = true;
        switch (action) {
            case Store -> {
                GameState.instance.clientPlayer.removeItem(contextMenu.itemEntry.item);
                hud.storageInventoryHUD.getStorage().takeItem(contextMenu.itemEntry.item);
                Vector2 pos = hud.storageInventoryHUD.getStorage().getPosition();
                instance.client.storedItem(contextMenu.itemEntry.item,(int)Math.floor(pos.x), (int)Math.floor(pos.y));
                instance.hud.updateInvHUDContent();
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

    public void storageInventoryFar(){
        contextMenu.disableActions(ContextMenu.ConAction.Store);
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
        super(hud,  ContextMenu.ConAction.Store, ContextMenu.ConAction.Equip, ContextMenu.ConAction.Drop, ContextMenu.ConAction.Description);
        setName("PlayerInventoryScrollPane");
        contextMenu.disableActions(ContextMenu.ConAction.Store);
    }
}
