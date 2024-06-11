package com.mygdx.game.gamestate.objects.items.guns;

import com.mygdx.game.gamestate.objects.items.Item;
import lombok.Getter;

public class GunMagazine extends Item {
    protected int capacity = 10;
    @Getter
    protected int currentAmount;
    protected Gun insertedIn; // null if not inserted in

    public GunMagazine(long uid, String iId, String itemName) {
        super(uid, iId, itemName);
        currentAmount = capacity;
    }

    public void onInsert(Gun gun){
        insertedIn = gun;
        owner.removeItem(this);
    }

    public void onUnInsert(){
        if (currentAmount == 0) {
            dispose();
        } else {
            getGun().getOwner().takeItem(this);
        }
        insertedIn = null;
    }

    public boolean isInserted(){
        return insertedIn != null;
    }

    public Gun getGun(){
        return insertedIn;
    }

    public int onFire(){
        if (currentAmount > 0)
            currentAmount--;
        return currentAmount;
    }
}
