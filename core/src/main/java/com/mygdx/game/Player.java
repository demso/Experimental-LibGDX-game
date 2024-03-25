package com.mygdx.game;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.tiledmap.BodyUserData;
import org.jetbrains.annotations.Nullable;

public class Player {
    SecondGDXGame game;
    float WIDTH;
    float HEIGHT;
    float maxVelocity = 5f;
    float DAMPING = 0.87f;
    public Body body;
    Body sensorBody;
    Array<Body> closeObjects;
    public Body closestObject;
    enum State {
        Standing, Walking
    }
    final Vector2 position = new Vector2();
    Vector2 velocity = new Vector2();
    State state = State.Walking;
    float stateTime = 0;
    enum Facing {
        RIGHT, LEFT, UP, DOWN
    }
    Facing facing = Facing.DOWN;
    Array<Item> inventoryItems = new Array<>();
    Item equipedItem;

    Player(SecondGDXGame game){
        this.game = game;
        closeObjects = new Array<>();
    }

    @Nullable
    Body getClosestObject(){
        Body co = null;
        float minDist = Float.MAX_VALUE;
        float dist = 0;
        for (Body closeObject : closeObjects) {
            dist = body.getPosition().dst2(closeObject.getPosition());
            if (dist < minDist){
                co = closeObject;
                minDist = dist;
            }

        }
        return co;
    }
    public void pickupItem(Item item){
        item.pickup();
        addItemToInventory(item);
    }
    public void addItemToInventory(Item item){
        inventoryItems.add(item);
    }
    public void removeItemFromInventory(Item item){
        if (equipedItem == item)
            equipedItem = null;
        inventoryItems.removeValue(item, true);
    }
    public Array<Item> getInventoryItems(){
        return inventoryItems;
    }
    public void equipItem(Item item){
        equipedItem = item;
    }
    public Item freeHands(){
        Item item = equipedItem;
        equipedItem = null;
        return item;
    }
    public void initBody(World world, RayHandler rayHandler){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(5, 95));
        Body body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.01f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.filter.categoryBits = GameItself.PLAYER_CF;
        fixtureDef.filter.groupIndex = GameItself.PLAYER_CG;
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        body.setUserData(new BodyUserData(this, "player"));
        circle.dispose();

        CircleShape sensorCircle = new CircleShape();
        sensorCircle.setRadius(1f);
        FixtureDef sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.shape = sensorCircle;
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.filter.categoryBits = GameItself.PLAYER_INTERACT_CF;
        sensorFixtureDef.filter.maskBits = (short) (sensorFixtureDef.filter.maskBits & ~GameItself.PLAYER_CF);
        body.createFixture(sensorFixtureDef).setUserData(new BodyUserData("playerInteractionBubble"));
        sensorCircle.dispose();

        this.body = body;
        body.setLinearDamping(2);

        PointLight light = new PointLight(rayHandler, 1300, Color.WHITE, 100f, 0, 0);
        light.setSoft(true);
        light.setSoftnessLength(2f);
        light.attachToBody(body, 0, 0);
        light.setIgnoreAttachedBody(true);
        Filter f = new Filter();
        f.categoryBits = GameItself.LIGHT_CF;
        f.groupIndex = -10;
        light.setContactFilter(f);
    }

    public void update(float deltaTime){
        if (deltaTime > 0.1f) deltaTime = 0.1f;
        stateTime += deltaTime;
        body.setLinearVelocity(0,0);
        closestObject = getClosestObject();
    }

    public void inputMove(boolean moveUp, boolean moveDown, boolean moveToTheRight, boolean moveToTheLeft){
        if (!(moveToTheRight && moveToTheLeft)) {
            if (moveToTheLeft) {
                body.setLinearVelocity(-100f, body.getLinearVelocity().y);
                state = Player.State.Walking;
                facing = Player.Facing.LEFT;
            }

            if (moveToTheRight) {
                body.setLinearVelocity(100f, body.getLinearVelocity().y);
                state = Player.State.Walking;
                facing = Player.Facing.RIGHT;
            }
        }
        if (!(moveUp && moveDown)){
            if (moveUp) {
                body.setLinearVelocity(body.getLinearVelocity().x, 100f);
                state = Player.State.Walking;
                facing = Player.Facing.UP;
            }

            if (moveDown) {
                body.setLinearVelocity(body.getLinearVelocity().x, -100f);
                state = Player.State.Walking;
                facing = Player.Facing.DOWN;
            }
        }
        Vector2 vel = body.getLinearVelocity().clamp(0, maxVelocity);
        body.setLinearVelocity(vel);
        velocity = vel;
        if (Math.abs(body.getLinearVelocity().len2()) < 0.5f) {
            state = Player.State.Standing;
        }

        position.x = (body.getPosition().x);
        position.y = (body.getPosition().y);
    }
}
