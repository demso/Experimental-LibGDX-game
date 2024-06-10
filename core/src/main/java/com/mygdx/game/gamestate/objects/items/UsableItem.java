package com.mygdx.game.gamestate.objects.items;

public abstract class UsableItem extends Item{
    public UsableItem(long uid, String iId, String itemName) {
        super(uid, iId, itemName);
    }

    public abstract boolean use();//returns true if used
}
