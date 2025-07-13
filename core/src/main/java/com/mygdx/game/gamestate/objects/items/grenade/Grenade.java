package com.mygdx.game.gamestate.objects.items.grenade;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.player.Player;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class Grenade extends Item {
    public float flySpeed = 15f;
    public float detonationTime = 2.5f;
    public float timeToDetonation = 0f;
    public float damage = 25f;
    public float radius = 4f;

    public Grenade(long uid, String iId, String itemName) {
        super(uid, iId, itemName);
    }

<<<<<<< HEAD
    public void fire(long t, boolean real){
        if (!real) {
            onDrop();
            prepareForRendering();

            physicalBody = bodyResolver.activeGrenadeBody(0, 0, this);
            physicalBody.setLinearDamping(0);
            physicalBody.setAngularDamping(0);
            physicalBody.getFixtureList().get(0).setRestitution(0);
            physicalBody.setMassData(new MassData().set(0.1f, new Vector2(0, -0.1f), 0.1f));

            new Box2dBehaviour(physicalBody, gameObject);
            new GrenadeHandler(gameObject);
            gameObject.setEnabled(true);
        }
        if (real && owner instanceof Player player) {
=======
    public void fire(long t){
        if (owner instanceof Player player) {
>>>>>>> single
            onDrop();
            prepareForRendering();

            float time = (float) t/1000f + 0.05f;

            if (time > 1.2f) {
                time = 1.2f;
            }

            physicalBody = bodyResolver.activeGrenadeBody(player.getPosition().x, player.getPosition().y, this);
            physicalBody.setLinearDamping(2);

            physicalBody.setAngularDamping(1);

            physicalBody.getFixtureList().get(0).setRestitution(1f);
            physicalBody.getFixtureList().get(0).setFriction(0.1f);

            FixtureDef detector = new FixtureDef();
            detector.isSensor = true;
            detector.filter.maskBits &= ~Globals.LIGHT_CONTACT_FILTER;
            detector.density = 0.001f;
            var shape = new CircleShape();
            shape.setRadius(physicalBody.getFixtureList().first().getShape().getRadius());
            detector.shape = shape;
            Fixture detectFix = physicalBody.createFixture(detector);
            detectFix.setUserData(this);
            detectFix.refilter();

            physicalBody.setMassData(new MassData().set(0.5f, new Vector2(0, -0.1f), 0.1f));

            new Box2dBehaviour(physicalBody, gameObject);
            GrenadeHost host = new GrenadeHost(gameObject);

            gameObject.setEnabled(true);

            player.removeItem(this);

            Vector2 flyVec = new Vector2(0, flySpeed).scl(time).setAngleDeg(player.itemRotation);
            Vector2 vec = physicalBody.getPosition();
            Vector2 playerSpeed = new Vector2(player.getVelocity());
            //Vector2 addImp = playerSpeed.scl(physicalBody.getMass() * physicalBody.getLinearDamping());
            vec.y -= 0.4f;
            physicalBody.setLinearVelocity(playerSpeed);
            physicalBody.applyLinearImpulse(flyVec, vec, true);
            host.thrown(detonationTime);
        }
    }

    public void onDetonation(){
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
        super.dispose();
    }
}
