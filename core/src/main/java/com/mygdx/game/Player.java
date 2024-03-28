package com.mygdx.game;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.behaviours.PlayerCollisionBehaviour;
import com.mygdx.game.tiledmap.SimpleUserData;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;
import dev.lyze.gdxUnBox2d.behaviours.fixtures.CreateBoxFixtureBehaviour;
import org.jetbrains.annotations.Nullable;

public class Player extends Entity {
    SecondGDXGame game;
    float WIDTH;
    float HEIGHT;
    float maxVelocity = 40f;
    float DAMPING = 0.87f;
    public Array<Body> closeObjects;
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
    float frameDuration = 0.1f;
    TextureRegion currentFrame;
    Animation<TextureRegion> walkSide;
    Animation<TextureRegion> walkUp;
    Animation<TextureRegion> walkDown;

    Player(SecondGDXGame game){
        this.game = game;
        closeObjects = new Array<>();

        setEntityType(EntityType.PLAYER);
        setHp(10);
        setMaxHp(10);

        Texture textureSheet = new Texture(Gdx.files.internal("ClassicRPG_Sheet.png"));
        TextureRegion[][] textureRegions = TextureRegion.split(textureSheet, 16, 16);

        TextureRegion[] walkFrames = new TextureRegion[4];
        int index = 0;
        for (int i = 0; i < 4; i++)
            walkFrames[index++] = textureRegions[0][i];
        walkDown = new Animation<TextureRegion>(frameDuration, walkFrames);

        walkFrames = new TextureRegion[4];
        index = 0;
        for (int i = 0; i < 4; i++)
            walkFrames[index++] = textureRegions[1][i];
        walkSide = new Animation<TextureRegion>(frameDuration, walkFrames);

        walkFrames = new TextureRegion[4];
        index = 0;
        for (int i = 0; i < 4; i++)
            walkFrames[index++] = textureRegions[3][i];
        walkUp = new Animation<TextureRegion>(frameDuration, walkFrames);
    }
    public void initBody(World world, RayHandler rayHandler){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(5, 90));
        Body body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.filter.categoryBits = GameItself.PLAYER_CF;
        fixtureDef.filter.groupIndex = GameItself.PLAYER_CG;
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
        sensorFixtureDef.filter.categoryBits = GameItself.PLAYER_INTERACT_CF;
        sensorFixtureDef.filter.maskBits = (short) (sensorFixtureDef.filter.maskBits & ~GameItself.PLAYER_CF);
        body.createFixture(sensorFixtureDef).setUserData(new SimpleUserData("playerInteractionBubble"));
        sensorCircle.dispose();

        this.body = body;
        body.setLinearDamping(12);

        GameObject playerObject = new GameObject(getName(), GameItself.unbox);

        new Box2dBehaviour(body, playerObject);
        new PlayerCollisionBehaviour(playerObject);

        PointLight light = new PointLight(rayHandler, 1300, Color.WHITE, 100f, 0, 0);
        light.setSoft(true);
        light.setSoftnessLength(2f);
        light.attachToBody(body, 0, 0);
        light.setIgnoreAttachedBody(true);
        Filter f = new Filter();
        f.categoryBits = GameItself.LIGHT_CF;
        f.groupIndex = -10;
        light.setContactFilter(f);

        MassData massData = new MassData();
        massData.mass = 60f;
        massData.center.set(new Vector2(0f,0f));
        body.setMassData(massData);
    }
    @Nullable
    Body getClosestObject(){
        return closestObject;
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
    public void update(float deltaTime){
        if (deltaTime > 0.1f) deltaTime = 0.1f;
        stateTime += deltaTime;
        //closestObject = getClosestObject();
        switch (state) {
            case Standing:
                currentFrame = walkDown.getKeyFrame(1);
                break;
            case Walking:
                switch (facing) {
                    case RIGHT:
                    case LEFT:
                        currentFrame = walkSide.getKeyFrame(stateTime, true);
                        break;
                    case UP:
                        currentFrame = walkUp.getKeyFrame(stateTime, true);
                        break;
                    case DOWN:
                        currentFrame = walkDown.getKeyFrame(stateTime, true);
                        break;
                }
                break;
        }
        position.x = (body.getPosition().x);
        position.y = (body.getPosition().y);
    }


    Vector2 movingVector = new Vector2();
    Vector2 vel = new Vector2();
    Vector2 zeroVector = new Vector2(0, 0);
    public void inputMove(boolean moveUp, boolean moveDown, boolean moveToTheRight, boolean moveToTheLeft, float deltaTime){
        if (moveUp || moveDown || moveToTheRight || moveToTheLeft){
            vel.set(0,0);
            movingVector.set(0,0);
            if (!(moveToTheRight && moveToTheLeft)) {
                if (moveToTheLeft) {
                    movingVector.set(-maxVelocity, movingVector.y);
                    state = Player.State.Walking;
                    facing = Player.Facing.LEFT;
                }

                if (moveToTheRight) {
                    movingVector.set(maxVelocity, movingVector.y);
                    state = Player.State.Walking;
                    facing = Player.Facing.RIGHT;
                }
            }
            if (!(moveUp && moveDown)){
                if (moveUp) {
                    movingVector.set(movingVector.x, maxVelocity);
                    state = Player.State.Walking;
                    facing = Player.Facing.UP;
                }

                if (moveDown) {
                    movingVector.set(movingVector.x, -maxVelocity);
                    state = Player.State.Walking;
                    facing = Player.Facing.DOWN;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.C))
                vel.set(movingVector.clamp(0, maxVelocity).scl(0.5f));
            else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                vel.set(movingVector.clamp(0, maxVelocity).scl(1.5f));
            else vel.set(movingVector.clamp(0, maxVelocity));
            body.applyLinearImpulse(vel, zeroVector, true);
            velocity.set(vel);
        } if (Math.abs(body.getLinearVelocity().len2()) < 0.5f) {
            //body.setLinearVelocity(0f,0f);
            state = Player.State.Standing;
        }
    }
    public void renderPlayer(Batch batch, Camera camera){
        //player
        if (facing == Facing.RIGHT)
            batch.draw(currentFrame, position.x - WIDTH/2 + WIDTH, position.y - WIDTH * 1/4, -WIDTH, HEIGHT);
        else
            batch.draw(currentFrame, position.x - WIDTH/2, position.y - WIDTH * 1/4, WIDTH, HEIGHT);
        if (equipedItem != null){
            TextureRegion tileTextureRegion = equipedItem.item.tile.getTextureRegion();
            float width = 0.5f;
            float height = 0.5f;
            float offsetX = 0;
            float offsetY = 0f;
            Vector3 mousePos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            Vector2 playerPos = position;
            float rotation = Double.valueOf(Math.toDegrees(Math.atan2(mousePos.y - playerPos.y, mousePos.x - playerPos.x))).floatValue()-34;
            batch.draw(tileTextureRegion, position.x - (tileTextureRegion.getRegionWidth()*width /2f + offsetX)/(float) GameItself.TILE_SIDE, position.y - (tileTextureRegion.getRegionHeight()*height/2f + offsetY)/(float) GameItself.TILE_SIDE, tileTextureRegion.getRegionWidth()*width/2f/(float)GameItself.TILE_SIDE, tileTextureRegion.getRegionHeight()*height/2f/ (float) GameItself.TILE_SIDE, width, height, 1,1,rotation);
        }
    }
    @Override
    public String getName() {
        return "player";
    }

    @Override
    public void kill() {

    }

    public void addCloseBody(Body closeBody){
        closeObjects.add(closeBody);
    }
}
