package com.mygdx.game.gamestate.objects.items.guns;

import com.mygdx.game.gamestate.Globals;

public class AutoRifle extends Gun{
    int RPM = 600;

    long lastShotTime = 0;
    long timeBetweenShots = 60000/RPM;

    public AutoRifle(long uid, String tileName, String itemName) {
        super(uid, tileName, itemName);
        reloadTime = 5;
        setFireType(FireType.AUTO);
    }

    @Override
    public void fireBullet() {
        long curTime = System.currentTimeMillis();
        if (curTime - lastShotTime > timeBetweenShots) {
        super.fireBullet();
        lastShotTime = System.currentTimeMillis();
        }

    }

    @Override
    protected void createSpriteBehaviour() {
        gunSpriteBehaviour = new AutoRifleSprite(GO, this, spriteWidth, spiteHeight, tile.getTextureRegion(), Globals.DEFAULT_RENDER_ORDER);
    }
}
