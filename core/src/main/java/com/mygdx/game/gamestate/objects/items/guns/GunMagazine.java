package com.mygdx.game.gamestate.objects.items.guns;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.objects.items.Item;
import lombok.Getter;
import lombok.Setter;

public class GunMagazine extends Item {
    @Getter @Setter
    protected int capacity = 10;
    @Getter
    protected int currentAmount;
    @Getter
    Array<String> gunTypes;
    protected Gun insertedIn; // null if not inserted in

    public GunMagazine(long uid, String iId, String itemName) {
        super(uid, iId, itemName);
        gunTypes = new Array<>();
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

    public void addGunTypes(String... gunTypes){
        this.gunTypes.addAll(gunTypes);
    }

    public int onFire(){
        if (currentAmount > 0)
            currentAmount--;
        return currentAmount;
    }
}
