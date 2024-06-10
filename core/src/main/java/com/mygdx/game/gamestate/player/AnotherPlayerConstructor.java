package com.mygdx.game.gamestate.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.factories.MobsFactory;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.net.PlayerInfo;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class AnotherPlayerConstructor {
    public static Player createPlayer(GameState gameState, PlayerInfo info){
        Player player = new Player();

        player.setId(info.id);
        player.setName(info.name);
        player.setHp(info.hp);
        player.setMaxHp(info.maxHp);
        player.setFriendliness(Entity.Friendliness.PLAYER);
        player.setKind(Entity.Kind.ANOTHER_PLAYER);

        BodyDef bodyDef = MobsFactory.bodyDef(player.startX, player.startY, BodyDef.BodyType.DynamicBody);
        Body body = GameState.instance.world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.filter.categoryBits = Globals.PLAYER_CONTACT_FILTER;
        fixtureDef.filter.groupIndex = Globals.PLAYER_CONTACT_GROUP;
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        body.setUserData(player);
        circle.dispose();

//        //sensor
//        CircleShape sensorCircle = new CircleShape();
//        sensorCircle.setRadius(1f);
//        FixtureDef sensorFixtureDef = new FixtureDef();
//        sensorFixtureDef.density = 0.00001f;
//        sensorFixtureDef.shape = sensorCircle;
//        sensorFixtureDef.isSensor = true;
//        sensorFixtureDef.filter.categoryBits = Globals.PLAYER_INTERACT_CONTACT_FILTER;
//        sensorFixtureDef.filter.maskBits = (short) (sensorFixtureDef.filter.maskBits & ~Globals.PLAYER_CONTACT_FILTER);
//        body.createFixture(sensorFixtureDef).setUserData(new SimpleUserData("playerInteractionBubble"));
//        sensorCircle.dispose();

        //body.setLinearDamping(0);

        player.setBody(body);

        GameObject playerObject = new GameObject(player.getName(), GameState.instance.unbox);

        player.playerObject = playerObject;

        new Box2dBehaviour(body, playerObject);
        new PlayerCollisionBehaviour(playerObject);
        AnotherPlayerHandler ph = new AnotherPlayerHandler(player);
        player.playerHandler = ph;

        //Player handler construction
        ph.player = player;

        Texture textureSheet = new Texture(Gdx.files.internal("ClassicRPG_Sheet.png"));
        TextureRegion[][] textureRegions = TextureRegion.split(textureSheet, 16, 16);

        TextureRegion[] walkFrames = new TextureRegion[4];
        int index = 0;
        for (int i = 0; i < 4; i++)
            walkFrames[index++] = textureRegions[0][i];
        ph.walkDown = new Animation<TextureRegion>(ph.frameDuration, walkFrames);

        walkFrames = new TextureRegion[4];
        index = 0;
        for (int i = 0; i < 4; i++)
            walkFrames[index++] = textureRegions[1][i];
        ph.walkSide = new Animation<TextureRegion>(ph.frameDuration, walkFrames);

        walkFrames = new TextureRegion[4];
        index = 0;
        for (int i = 0; i < 4; i++)
            walkFrames[index++] = textureRegions[3][i];
        ph.walkUp = new Animation<TextureRegion>(ph.frameDuration, walkFrames);
        //Player handler construction end

//        PointLight light = new PointLight(gg.rayHandler, 1300, Color.WHITE, 50, 0, 0);
//        light.setSoft(true);
//        light.setSoftnessLength(2f);
//        light.attachToBody(body, 0, 0);
//        light.setIgnoreAttachedBody(true);
//        Filter f = new Filter();
//        f.categoryBits = Globals.LIGHT_CONTACT_FILTER;
//        f.groupIndex = -10;
//        light.setContactFilter(f);

        MassData massData = new MassData();
        massData.mass = 0.05f;
        massData.center.set(new Vector2(0f,0f));
        body.setMassData(massData);

        player.setPosition(info.x, info.y);
        if (info.equippedItem != null) {
            Item it = gameState.items.get(info.equippedItem.uid);
            player.equipItem(it == null ? gameState.itemsFactory.getItem(info.equippedItem) : it);
        }

        return player;
    }
}
