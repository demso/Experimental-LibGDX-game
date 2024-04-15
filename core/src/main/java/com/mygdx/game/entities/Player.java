package com.mygdx.game.entities;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.*;
import com.mygdx.game.Item;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.behaviours.PlayerHandler;
import com.mygdx.game.behaviours.collision.PlayerCollisionBehaviour;
import com.mygdx.game.tiledmap.SimpleUserData;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import org.jetbrains.annotations.Nullable;

public class Player extends Entity {
    SecondGDXGame game;
    public float WIDTH;
    public float HEIGHT;
    public float maxVelocity = 30f;
    float DAMPING = 0.87f;
    public Body closestObject;

//    public enum State {
//        Standing, Walking
//    }

    public Vector2 velocity = new Vector2();
    //public State state = State.Walking;
    //float stateTime = 0;
//    public enum Facing {
//        RIGHT, LEFT, UP, DOWN
//    }
    //public Facing facing = Facing.DOWN;
    Array<Item> inventoryItems = new Array<>();
    public Item equipedItem;
//    float frameDuration = 0.1f;
//    TextureRegion currentFrame;

    public GameObject playerObject;

    public Player(SecondGDXGame game){
        this.game = game;

        setFriendliness(Friendliness.PLAYER);
        setKind(Kind.PLAYER);
        setHp(10);
        setMaxHp(10);

//        Texture textureSheet = new Texture(Gdx.files.internal("ClassicRPG_Sheet.png"));
//        TextureRegion[][] textureRegions = TextureRegion.split(textureSheet, 16, 16);
//
//        TextureRegion[] walkFrames = new TextureRegion[4];
//        int index = 0;
//        for (int i = 0; i < 4; i++)
//            walkFrames[index++] = textureRegions[0][i];
//        walkDown = new Animation<TextureRegion>(frameDuration, walkFrames);
//
//        walkFrames = new TextureRegion[4];
//        index = 0;
//        for (int i = 0; i < 4; i++)
//            walkFrames[index++] = textureRegions[1][i];
//        walkSide = new Animation<TextureRegion>(frameDuration, walkFrames);
//
//        walkFrames = new TextureRegion[4];
//        index = 0;
//        for (int i = 0; i < 4; i++)
//            walkFrames[index++] = textureRegions[3][i];
//        walkUp = new Animation<TextureRegion>(frameDuration, walkFrames);
    }
    public void initBody(World world, RayHandler rayHandler){
        BodyDef bodyDef = MobsFactory.bodyDef(5, 90, BodyDef.BodyType.DynamicBody);
        Body body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.filter.categoryBits = Constants.PLAYER_CF;
        fixtureDef.filter.groupIndex = Constants.PLAYER_CG;
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        body.setUserData(this);
        circle.dispose();

        //sensor
        CircleShape sensorCircle = new CircleShape();
        sensorCircle.setRadius(1f);
        FixtureDef sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.density = 0.00001f;
        sensorFixtureDef.shape = sensorCircle;
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.filter.categoryBits = Constants.PLAYER_INTERACT_CF;
        sensorFixtureDef.filter.maskBits = (short) (sensorFixtureDef.filter.maskBits & ~Constants.PLAYER_CF);
        body.createFixture(sensorFixtureDef).setUserData(new SimpleUserData("playerInteractionBubble"));
        sensorCircle.dispose();

        this.body = body;
        body.setLinearDamping(12);

        playerObject = new GameObject(getName(), GameItself.unbox);

        new Box2dBehaviour(body, playerObject);
        new PlayerCollisionBehaviour(playerObject);
        new PlayerHandler(playerObject, this);

        PointLight light = new PointLight(rayHandler, 1300, Color.WHITE, 50, 0, 0);
        light.setSoft(true);
        light.setSoftnessLength(2f);
        light.attachToBody(body, 0, 0);
        light.setIgnoreAttachedBody(true);
        Filter f = new Filter();
        f.categoryBits = Constants.LIGHT_CF;
        f.groupIndex = -10;
        light.setContactFilter(f);

        MassData massData = new MassData();
        massData.mass = 60f;
        massData.center.set(new Vector2(0f,0f));
        body.setMassData(massData);
    }
    @Nullable
    public Body getClosestObject(){
        return closestObject;
    }
    public void pickupItem(Item item){
        item.removeFromWorld();
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

    @Override
    public String getName() {
        return "player";
    }

    @Override
    public Object getData() {
        return this;
    }

    @Override
    public void kill() {
        super.kill();
        SecondGDXGame.helper.log("Oh no im killed!");
    }

    public void revive(){
        setHp(getMaxHp());
        isAlive = true;
        SecondGDXGame.helper.log("Player revived");
    }
}
