package com.mygdx.game.gamestate.objects.items.guns;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dev.lyze.gdxUnBox2d.GameObject;

public class AutoRifleSprite extends GunSpriteBehaviour{
    public AutoRifleSprite(GameObject gameObject, Gun gun, float width, float height, TextureRegion textureRegion, float renderOrder) {
        super(gameObject, gun, width, height, textureRegion, renderOrder);
    }
}
