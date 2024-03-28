package com.mygdx.game.tiledmap;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.GameItself;

public class BodyTileResolver {
    World world = GameItself.world;
    public enum Type {
        FULL_BODY,
        METAL_CLOSET_BODY,
        WINDOW,
    }
    public enum Direction{
        NORTH,
        SOUTH,
        WEST,
        EAST
    }

    BodyTileResolver(World world){
        this.world = world;
    }
    public Body resolveBody(float x, float y, Object userData, Type type, Direction direction){
        Body body = null;
        switch (type){
            case FULL_BODY -> body = fullBody(x, y, userData);
            case WINDOW -> body = window(x, y, userData, direction);
            case METAL_CLOSET_BODY -> body = metalClosetBody(x, y, userData);
        }
        return body;
    }

    public Body resolveBody(float x, float y, Object userData, Type type){
        Body body = null;
        switch (type){
            case FULL_BODY -> body = fullBody(x, y, userData);
            case METAL_CLOSET_BODY -> body = metalClosetBody(x, y, userData);
        }
        return body;
    }

    public Body metalClosetBody(float x, float y, Object userData){
        Body body = null;

        BodyDef metalClosetBodyDef = new BodyDef();
        PolygonShape metalClosetBox = new PolygonShape();
        FixtureDef metalClosetFixtureDef = new FixtureDef();
        metalClosetBox.setAsBox(0.33f, 0.25f);
        metalClosetFixtureDef.shape = metalClosetBox;
        metalClosetFixtureDef.filter.groupIndex = 0;

        metalClosetBodyDef.position.set(new Vector2(x, y));
        body = world.createBody(metalClosetBodyDef);
        body.createFixture(metalClosetFixtureDef);
        body.setUserData(userData);

        return body;
    }

    public Body window(float x, float y, Object userData, Direction direction){
        BodyDef windowHorBodyDef = new BodyDef();
        PolygonShape windowHorBox = new PolygonShape();
        FixtureDef windowHorFixtureDef = new FixtureDef();
        windowHorBox.setAsBox(0.5f, 0.05f);
        windowHorFixtureDef.shape = windowHorBox;
        windowHorFixtureDef.filter.groupIndex = -10;

        BodyDef windowVertBodyDef = new BodyDef();
        PolygonShape windowVertBox = new PolygonShape();
        FixtureDef windowVertFixtureDef = new FixtureDef();
        windowVertBox.setAsBox(0.05f, 0.5f);
        windowVertFixtureDef.shape = windowVertBox;
        windowVertFixtureDef.filter.groupIndex = -10;

        Body body = null;

        switch (direction){
            case NORTH, SOUTH -> {
                windowHorBodyDef.position.set(x, y);
                body = world.createBody(windowHorBodyDef);
                body.createFixture(windowHorFixtureDef);
                body.setUserData(userData);
            } case EAST, WEST -> {
                windowVertBodyDef.position.set(x, y);
                body = world.createBody(windowVertBodyDef);
                body.createFixture(windowVertFixtureDef);
                body.setUserData(userData);
            }
        }

        return body;
    }

    public Body fullBody(float x, float y, Object userData){
        BodyDef fullBodyDef = new BodyDef();
        fullBodyDef.position.set(x, y);

        PolygonShape fullBox = new PolygonShape();

        FixtureDef fullFixtureDef = new FixtureDef();
        fullBox.setAsBox(0.5f, 0.5f);
        fullFixtureDef.shape = fullBox;
        fullFixtureDef.filter.groupIndex = 0;

        Body body = world.createBody(fullBodyDef);
        body.createFixture(fullFixtureDef);
        body.setUserData(userData);

        return body;
    }

    public Body transparentFullBody(float x, float y, Object userData){
        BodyDef transparentBodyDef = new BodyDef();
        PolygonShape transparentBox = new PolygonShape();
        FixtureDef transparentFixtureDef = new FixtureDef();
        transparentBox.setAsBox(x, y);
        transparentFixtureDef.shape = transparentBox;
        transparentFixtureDef.filter.groupIndex = -10;

        Body body = world.createBody(transparentBodyDef);
        body.createFixture(transparentFixtureDef);
        body.setUserData(userData);

        return body;
    }

    public Direction getDirection(TiledMapTileLayer.Cell cell){
        boolean southWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_0 && cell.getFlipVertically() && cell.getFlipVertically();
        boolean northWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_0 && !cell.getFlipVertically() && !cell.getFlipVertically();
        boolean eastWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_270 && !cell.getFlipVertically() && !cell.getFlipVertically();
        boolean westWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_90 && !cell.getFlipVertically() && !cell.getFlipVertically();
        if (northWard)
            return Direction.NORTH;
        if (southWard)
            return Direction.SOUTH;
        if (westWard)
            return Direction.WEST;
        if (eastWard)
            return Direction.EAST;
        return Direction.NORTH;
    }
}
