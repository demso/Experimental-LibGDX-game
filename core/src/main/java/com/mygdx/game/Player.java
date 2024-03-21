package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import org.jetbrains.annotations.Nullable;

public class Player {
    SecondGDXGame game;
    float WIDTH;
    float HEIGHT;
    float MAX_VELOCITY = 10f;
    float DAMPING = 0.87f;
    Body body;
    Body sensorBody;
    Array<Body> closeObjects;
    Body closestObject;
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
    public void addItemToInventory(Item item){
        inventoryItems.add(item);
    }
    public void removeItemToInventory(Item item){
        inventoryItems.removeValue(item, true);
    }
    public Array<Item> getInventoryItems(){
        return inventoryItems;
    }
}
