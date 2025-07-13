package com.mygdx.game.gamestate.objects.items;

import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.objects.tiles.Storage;
import com.mygdx.game.gamestate.player.Player;
import lombok.Getter;

public class Meds extends UsableItem{
    @Getter
    float healAmount = 5;

    public Meds(long uid, String iId, String itemName) {
        super(uid, iId, itemName);
    }

    @Override
    public boolean use() {
        Storage own = getOwner();
        if (own instanceof Player player) {
            player.heal(healAmount);
            dispose();
            return true;
        } else {
            HandyHelper.instance.log("[Meds:use] Can only heal players, owner: " + own);
        }
        return false;
    }
}
