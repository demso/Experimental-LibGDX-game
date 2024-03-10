package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

class Player {
    SecondGDXGame game;
    float WIDTH;
    float HEIGHT;
    float MAX_VELOCITY = 10f;
    float DAMPING = 0.87f;
    Body body;
    Body sensorBody;
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

    Player(SecondGDXGame game){
        this.game = game;
    }
}
