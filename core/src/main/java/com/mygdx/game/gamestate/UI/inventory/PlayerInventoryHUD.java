package com.mygdx.game.gamestate.UI.inventory;

import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.UI.HUD;

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
                GameState.instance.player.dropItem(contextMenu.itemEntry.item);
                hud.storageInventoryHUD.storage.takeItem(contextMenu.itemEntry.item);
            }
            case Equip -> {
                GameState.instance.player.equipItem(contextMenu.itemEntry.item);
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

    public PlayerInventoryHUD(HUD hud) {
        super(hud,  ContextMenu.ConAction.Description, ContextMenu.ConAction.Put, ContextMenu.ConAction.Equip, ContextMenu.ConAction.Store);
        setName("PlayerInventoryScrollPane");
        contextMenu.disableActions(ContextMenu.ConAction.Store);
    }
}
