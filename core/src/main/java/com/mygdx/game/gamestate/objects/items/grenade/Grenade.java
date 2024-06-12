package com.mygdx.game.gamestate.objects.items.grenade;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.player.Player;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class Grenade extends Item {
    public float flySpeed = 8f;
    public float detonationTime = 3f;
    public float timeToDetonation = 0f;
    public float damage = 25f;
    public float radius = 3f;

    public Grenade(long uid, String iId, String itemName) {
        super(uid, iId, itemName);
    }

    public void fire(long t, boolean real){
        if (!real) {
            onDrop();
            prepareForRendering();

            physicalBody = bodyResolver.notInteractableItemBody(0, 0, this);
            physicalBody.setLinearDamping(0);
            physicalBody.getFixtureList().get(0).setRestitution(0);

            new Box2dBehaviour(physicalBody, gameObject);
            new GrenadeHandler(gameObject);
            gameObject.setEnabled(true);
        }
        if (real && owner instanceof Player player) {
            onDrop();
            prepareForRendering();

            float time = (float) t/1000f + 0.05f;

            if (time > 3f) {
                time = 3f;
            }

            physicalBody = bodyResolver.notInteractableItemBody(player.getPosition().x, player.getPosition().y, this);
            physicalBody.setLinearDamping(2);

            physicalBody.setAngularDamping(1);

            //physicalBody.getFixtureList().get(0).setDensity(10);
            physicalBody.getFixtureList().get(0).setRestitution(1f);
            physicalBody.getFixtureList().get(0).setFriction(0.1f);

            FixtureDef detector = new FixtureDef();
            detector.isSensor = true;
            detector.filter.maskBits &= ~Globals.LIGHT_CONTACT_FILTER;
            detector.density = 0.001f;
            var shape = new CircleShape();
            shape.setRadius(radius);
            detector.shape = shape;
            physicalBody.createFixture(detector).setUserData(this);

            physicalBody.setMassData(new MassData().set(0.5f, new Vector2(0, -0.1f), 0.1f));

            new Box2dBehaviour(physicalBody, gameObject);
            GrenadeHost host = new GrenadeHost(gameObject);

            gameObject.setEnabled(true);

            player.removeItem(this);

            Vector2 flyVec = new Vector2(0, flySpeed).scl(time).setAngleDeg(player.itemRotation);
            Vector2 vec = physicalBody.getPosition();
            vec.y -= 0.4f;
            physicalBody.applyLinearImpulse(flyVec, vec, true);
            host.thrown(detonationTime);
        }
    }

//    float fract = 1;
//    Vector2 vec = new Vector2();
    public void onDetonation(){
//        World world = physicalBody.getWorld();
//        nearEntities.forEach(entity -> {
//            world.rayCast(
//                    (fixture, point, normal, fraction) -> {
//                        if (((fixture.getFilterData().maskBits & (Globals.DEFAULT_CONTACT_FILTER)) == 0) || fixture.isSensor())
//                            return -1;
//                        Object data = fixture.getBody().getUserData();
//                        if (data == entity) {
//                            fract = fraction;
//                        } else if (data instanceof Entity) {
//                            fract = fraction +  0.1f + (float) Math.random() * 0.3f;
//                        };
//                        return fraction;
//                    },
//                    physicalBody.getPosition(),
//                    new Vector2(physicalBody.getPosition()).add( new Vector2(entity.getPosition()).sub(physicalBody.getPosition()).nor().scl(3)));
//            entity.hurt(Math.max(0, 1 - fract) * damage);
//        });
        dispose();
    }

    @Override
    public void prepareForRendering(){
        if (unBox != null && gameObject == null)
            gameObject = new GameObject(itemName, false, unBox);

        if (hud != null && spriteBehaviour == null)
            createSpriteBehaviour();
    }

    @Override
    protected void createSpriteBehaviour() {
        spriteBehaviour = new GrenadeSprite(gameObject, spriteWidth, spiteHeight, tile.getTextureRegion(), Globals.ITEMS_RENDER_ORDER);
    }

    @Override
    public void dispose() {
        GameState.instance.client.localGrenades.remove(this);
        super.dispose();
    }
}
