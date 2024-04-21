package com.mygdx.game.gamestate.UI.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.gamestate.UI.HUD;

public class PlayerInventoryHUD extends StorageInventoryHUD{

    public void storageInventoryNear(){
        contextMenu.enableActions(ContextMenu.ConAction.Store);
        if (contextMenu.isVisible())
            contextMenu.update();
    }

    public void storageInventoryFar(){
        contextMenu.disableActions(ContextMenu.ConAction.Store);
        if (contextMenu.isVisible())
            contextMenu.update();
    }

    public PlayerInventoryHUD(HUD hud) {
        super(hud, ContextMenu.ConAction.Put, ContextMenu.ConAction.Description, ContextMenu.ConAction.Equip, ContextMenu.ConAction.Store);
        contextMenu.disableActions(ContextMenu.ConAction.Store);
    }
}
