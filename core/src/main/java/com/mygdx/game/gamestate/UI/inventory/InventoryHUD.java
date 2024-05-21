package com.mygdx.game.gamestate.UI.inventory;

public interface InventoryHUD {
    public void showItemContextMenu(ItemEntry itemEntry);
    public boolean contextAction(ContextMenu.ConAction action, ContextMenu menu);
    public void closeItemContextMenu(ContextMenu menu);
}
