package com.mygdx.game.gamestate.objects.items.grenade;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.player.Player;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class Grenade extends Item {
    public float flySpeed = 10f;
    public float detonationTime = 3f;
    public float timeToDetonation = 0f;
    public ObjectSet<Entity> nearEntities = new ObjectSet<>();
    public float damage = 25f;
    public float radius = 3f;

    public Grenade(long uid, String iId, String itemName) {
        super(uid, iId, itemName);
    }

    public void fire(long t, boolean real){
        if (!isOwnedByPlayer())
            return;
        if (!real) {
            onDrop();
            prepareForRendering();

            physicalBody = bodyResolver.notInteractableItemBody(0, 0, this);
            physicalBody.setLinearDamping(0);
            physicalBody.getFixtureList().get(0).setRestitution(1);

            new Box2dBehaviour(physicalBody, gameObject);
            new GrenadeHandler(gameObject);
            gameObject.setEnabled(true);
        }
        if (real && owner instanceof Player player) {
            onDrop();
            prepareForRendering();

            float time = (float) t * 2f /1000f + 0.1f;

            if (time > 3f) {
                time = 3f;
            }

            physicalBody = bodyResolver.notInteractableItemBody(player.getPosition().x, player.getPosition().y, this);
            physicalBody.setLinearDamping(2);
            physicalBody.getFixtureList().get(0).setRestitution(1);

            FixtureDef detector = new FixtureDef();
            detector.isSensor = true;
            detector.filter.maskBits &= ~Globals.LIGHT_CONTACT_FILTER;
            var shape = new CircleShape();
            shape.setRadius(radius);
            detector.shape = shape;
            physicalBody.createFixture(detector).setUserData(this);

            new Box2dBehaviour(physicalBody, gameObject);
            GrenadeHost host = new GrenadeHost(gameObject);

            gameObject.setEnabled(true);

            player.removeItem(this);

            Vector2 flyVec = new Vector2(0, flySpeed).scl(time).setAngleDeg(player.itemRotation);
            physicalBody.applyLinearImpulse(flyVec, Vector2.Zero, true);
            host.thrown(detonationTime);
        }
    }

    public void detonation(){
        nearEntities.forEach(entity -> entity.hurt(Math.max(0, 1 - entity.getPosition().sub(getPosition()).len()/radius) * damage));
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
