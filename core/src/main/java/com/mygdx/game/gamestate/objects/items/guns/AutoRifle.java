package com.mygdx.game.gamestate.objects.items.guns;

import com.mygdx.game.gamestate.Globals;

public class AutoRifle extends Gun{
    int RPM = 600;

    long lastShotTime = 0;
    long timeBetweenShots = 60000/RPM;

    public AutoRifle(long uid, String tileName, String itemName) {
        super(uid, tileName, itemName);
        reloadTime = 2.5f;
        spriteWidth = 0.5f;
        spiteHeight = 0.5f;
        setFireType(FireType.AUTO);
    }

    @Override
    public boolean fireBullet(boolean real) {
        long curTime = System.currentTimeMillis();
        if (curTime - lastShotTime > timeBetweenShots) {
            super.fireBullet(real);
            lastShotTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @Override
    protected void createSpriteBehaviour() {
        gunSpriteBehaviour = new AutoRifleSprite(gameObject, this, spriteWidth, spiteHeight, tile.getTextureRegion(), Globals.DEFAULT_RENDER_ORDER);
    }
}
