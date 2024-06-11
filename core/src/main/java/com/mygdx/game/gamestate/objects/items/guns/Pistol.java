package com.mygdx.game.gamestate.objects.items.guns;

import com.mygdx.game.gamestate.Globals;

public class Pistol extends Gun{

    public Pistol(long uid, String tileName, String itemName) {
        super(uid, tileName, itemName);
        setFireType(FireType.SEMI_AUTO);
    }

    @Override
    protected void createSpriteBehaviour() {
        gunSpriteBehaviour = new PistolSprite(GO, this, spriteWidth, spiteHeight, tile.getTextureRegion(), Globals.DEFAULT_RENDER_ORDER);
    }
}
