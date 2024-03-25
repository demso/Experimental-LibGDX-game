package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.tiledmap.BodyUserData;
import org.jetbrains.annotations.Nullable;

public class Player {
    SecondGDXGame game;
    float WIDTH;
    float HEIGHT;
    float MAX_VELOCITY = 10f;
    float DAMPING = 0.87f;
    public Body body;
    Body sensorBody;
    Array<Body> closeObjects;
    public Body closestObject;
    enum State {
        Standing, Walking
    }
    final Vector2 position = new Vector2();
    final Vector2 velocity = new Vector2();
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
    public void initBody(World world){
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
        body.setUserData(this);
        circle.dispose();

        BodyDef sensorBodyDef = new BodyDef();
        sensorBodyDef.type = BodyDef.BodyType.DynamicBody;
        sensorBodyDef.position.set(new Vector2(5, 95));
        Body sensorBody = world.createBody(sensorBodyDef);
        CircleShape sensorCircle = new CircleShape();
        sensorCircle.setRadius(1f);
        FixtureDef sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.shape = sensorCircle;
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.filter.categoryBits = GameItself.PLAYER_INTERACT_CF;
        sensorFixtureDef.filter.maskBits = (short) (sensorFixtureDef.filter.maskBits & ~GameItself.PLAYER_CF);
        sensorBody.createFixture(sensorFixtureDef);
        sensorBody.setFixedRotation(true);
        sensorBody.setSleepingAllowed(false);
        sensorBody.setUserData(new BodyUserData(this, "playerInteractionBubble"));
        sensorCircle.dispose();

        this.sensorBody = sensorBody;
        this.body = body;
        body.setLinearDamping(2);
    }
}
