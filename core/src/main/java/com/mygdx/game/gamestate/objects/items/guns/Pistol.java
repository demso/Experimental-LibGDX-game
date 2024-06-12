package com.mygdx.game.gamestate.objects.items.guns;

import com.mygdx.game.gamestate.Globals;

public class Pistol extends Gun{

    public Pistol(long uid, String tileName, String itemName) {
        super(uid, tileName, itemName);
        reloadTime = 1f;
        setFireType(FireType.SEMI_AUTO);
    }

    @Override
    protected void createSpriteBehaviour() {
        gunSpriteBehaviour = new PistolSprite(gameObject, this, spriteWidth, spiteHeight, tile.getTextureRegion(), Globals.DEFAULT_RENDER_ORDER);
    }
}
