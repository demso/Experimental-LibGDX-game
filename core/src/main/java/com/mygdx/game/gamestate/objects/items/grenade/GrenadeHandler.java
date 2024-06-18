package com.mygdx.game.gamestate.objects.items.grenade;
//
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.Body;
//import com.mygdx.game.net.messages.client.GrenadeInfo;
//import dev.lyze.gdxUnBox2d.GameObject;
//import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;
//
//public class GrenadeHandler extends BehaviourAdapter {
//    public boolean needsUpdate;
//    public GrenadeInfo grenadeInfo;
//    Grenade grenade;
//    Body body;
//
//    public GrenadeHandler(GameObject gameObject) {
//        super(gameObject);
//    }
//
//    @Override
//    public void onEnable() {
//        grenade = (Grenade)getGameObject().getBox2dBehaviour().getBody().getUserData();
//        body = grenade.getPhysicalBody();
//    }
//
//    Vector2 velocity = new Vector2();
//    @Override
//    public void update(float delta) {
//        if (needsUpdate){
//            body.setLinearVelocity(grenadeInfo.xS, grenadeInfo.yS);
//            body.setTransform(grenadeInfo.x, grenadeInfo.y, grenadeInfo.rotation);
//            needsUpdate = false;
//        }
//    }
//
//    public void requestUpdate(GrenadeInfo info){
//        needsUpdate = true;
//        grenadeInfo = info;
//    }
//}
