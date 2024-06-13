package com.mygdx.game.gamestate.factories;

import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.gamestate.Globals;
import lombok.Setter;

public class BodyResolver {
    @Setter
    World world;
    public enum Type {
        FULL_BODY,
        METAL_CLOSET_BODY,
        WINDOW
    }
    public enum Direction{
        NORTH,
        SOUTH,
        WEST,
        EAST
    }
    public BodyResolver(World w) {
        world = w;
    }

    public Body resolveTileBody(float x, float y, Object userData, Type type, Direction direction){
        Body body = null;
        switch (type){
            case FULL_BODY -> body = fullBody(x + 0.5f, y + 0.5f, userData);
            case WINDOW -> body = window(x, y, userData, direction, true);
            case METAL_CLOSET_BODY -> body = metalClosetBody(x + 0.5f, y + 0.3f, userData);
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

    public Body window(float x, float y, Object userData, Direction direction, boolean offset){
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
            case NORTH-> {
                if (offset)
                    windowHorBodyDef.position.set(x + 0.5f, y + 0.95f);
                else
                    windowHorBodyDef.position.set(x, y);
                body = world.createBody(windowHorBodyDef);
                body.createFixture(windowHorFixtureDef);
                body.setUserData(userData);
            } case WEST -> {
                if (offset)
                    windowVertBodyDef.position.set(x + 0.05f, y + 0.5f);
                else
                    windowVertBodyDef.position.set(x, y);
                body = world.createBody(windowVertBodyDef);
                body.createFixture(windowVertFixtureDef);
                body.setUserData(userData);
            } case SOUTH -> {
                if (offset)
                    windowHorBodyDef.position.set(x + 0.5f, y + 0.05f);
                else
                    windowHorBodyDef.position.set(x, y);
                body = world.createBody(windowHorBodyDef);
                body.createFixture(windowHorFixtureDef);
                body.setUserData(userData);
            } case EAST -> {
                if (offset)
                    windowVertBodyDef.position.set(x + 0.95f, y + 0.5f);
                else
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
        fullFixtureDef.restitution = 0.2f;
        fullFixtureDef.friction = 1;

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

    public Body itemBody(float x, float y, Object userData){
        BodyDef transparentBodyDef = new BodyDef();
        transparentBodyDef.type = BodyDef.BodyType.DynamicBody;
        transparentBodyDef.fixedRotation = false;
        CircleShape transparentBox = new CircleShape();
        FixtureDef transparentFixtureDef = new FixtureDef();
        transparentBox.setRadius(0.2f);
        transparentFixtureDef.shape = transparentBox;
        transparentFixtureDef.restitution = 0.1f;
        transparentFixtureDef.friction = 1f;
        transparentBodyDef.active = false;
        transparentBodyDef.position.set(x, y);
        Body body = world.createBody(transparentBodyDef);
        body.createFixture(transparentFixtureDef);
        Filter filtr = body.getFixtureList().get(0).getFilterData();
        filtr.maskBits = Globals.ALL_CONTACT_FILTER & ~Globals.LIGHT_CONTACT_FILTER & ~Globals.PLAYER_CONTACT_FILTER;
        body.getFixtureList().get(0).refilter();
        body.setUserData(userData);

        return body;
    }

    public Body notInteractableItemBody(float x, float y, Object userData){
        Body body = itemBody(x, y, userData);
        Filter filter = body.getFixtureList().get(0).getFilterData();
        filter.maskBits = Globals.ALL_CONTACT_FILTER & ~Globals.PLAYER_INTERACT_CONTACT_FILTER & ~Globals.PLAYER_CONTACT_FILTER & ~Globals.LIGHT_CONTACT_FILTER;
        body.getFixtureList().get(0).refilter();
        return body;
    }

    public Body bulletBody(float x, float y, Object userData){
        BodyDef bodyDef = MobsFactory.bodyDef(x, y, BodyDef.BodyType.DynamicBody, true);
        Body body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(0.04f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 100f;
        fixtureDef.restitution = -100;

        fixtureDef.filter.categoryBits = Globals.BULLET_CONTACT_FILTER;
        fixtureDef.filter.maskBits = (short) (fixtureDef.filter.maskBits & ~Globals.LIGHT_CONTACT_FILTER & ~Globals.PLAYER_CONTACT_FILTER & ~Globals.PLAYER_INTERACT_CONTACT_FILTER);
        circle.dispose();
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);

        MassData massData = new MassData();
        massData.mass = 0.7f;
        massData.center.set(new Vector2(0f,0f));
        body.setMassData(massData);

        body.setUserData(userData);

        return body;
    }

    public static Direction getDirection(TiledMapTileLayer.Cell cell){
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

    public static Filter createFilter(short mask, short category, short group){
        Filter filter = new Filter();
        filter.maskBits = mask;
        filter.categoryBits = category;
        filter.groupIndex = group;
        return filter;
    }
}
