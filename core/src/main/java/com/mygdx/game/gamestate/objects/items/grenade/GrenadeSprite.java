package com.mygdx.game.gamestate.objects.items.grenade;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.net.messages.client.GrenadeInfo;
import dev.lyze.gdxUnBox2d.GameObject;

public class GrenadeSprite extends SpriteBehaviour {
    public boolean needsUpdate;
    public GrenadeInfo grenadeInfo;
    float timeToExplosion;
    public GrenadeSprite(GameObject gameObject, float width, float height, TextureRegion textureRegion, float renderOrder) {
        super(gameObject, width, height, textureRegion, renderOrder);
    }

//    @Override
//    public void update(float delta) {
//        if (needsUpdate){
//
//        }
//    }
//
//    @Override
//    public void fixedUpdate() {
//        super.fixedUpdate();
//        timeToExplosion -= getUnBox().getOptions().getTimeStep();
//        Grenade grenade = ((Grenade)(getGameObject().getBox2dBehaviour().getBody().getUserData()));
//        grenade.timeToDetonation = timeToExplosion;
//
//        if (timeToExplosion <= 0) {
//            grenade.detonation();
//        }
//    }
//
//    public void requestUpdate(GrenadeInfo info){
//        needsUpdate = true;
//        grenadeInfo = info;
//    }
//
//    public void thrown(float time){
//        timeToExplosion = time;
//    }

}
