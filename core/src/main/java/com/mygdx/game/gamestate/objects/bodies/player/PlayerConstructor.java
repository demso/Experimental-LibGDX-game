package com.mygdx.game.gamestate.objects.bodies.player;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.factories.MobsFactory;
import com.mygdx.game.gamestate.objects.bodies.userdata.SimpleUserData;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class PlayerConstructor {
    public Player createPlayer(GameState gg){
        Player player = new Player();

        player.setHp(10);
        player.setMaxHp(10);

        BodyDef bodyDef = MobsFactory.bodyDef(5, 90, BodyDef.BodyType.DynamicBody);
        Body body = GameState.Instance.world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.filter.categoryBits = Globals.PLAYER_CF;
        fixtureDef.filter.groupIndex = Globals.PLAYER_CG;
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        body.setUserData(player);
        circle.dispose();

        //sensor
        CircleShape sensorCircle = new CircleShape();
        sensorCircle.setRadius(1f);
        FixtureDef sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.density = 0.00001f;
        sensorFixtureDef.shape = sensorCircle;
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.filter.categoryBits = Globals.PLAYER_INTERACT_CF;
        sensorFixtureDef.filter.maskBits = (short) (sensorFixtureDef.filter.maskBits & ~Globals.PLAYER_CF);
        body.createFixture(sensorFixtureDef).setUserData(new SimpleUserData("playerInteractionBubble"));
        sensorCircle.dispose();

        body.setLinearDamping(12);

        player.setBody(body);

        GameObject playerObject = new GameObject(player.getName(), GameState.Instance.unbox);

        player.playerObject = playerObject;

        new Box2dBehaviour(body, playerObject);
        new PlayerCollisionBehaviour(playerObject);
        new PlayerHandler(playerObject, player);

        PointLight light = new PointLight(gg.rayHandler, 1300, Color.WHITE, 50, 0, 0);
        light.setSoft(true);
        light.setSoftnessLength(2f);
        light.attachToBody(body, 0, 0);
        light.setIgnoreAttachedBody(true);
        Filter f = new Filter();
        f.categoryBits = Globals.LIGHT_CF;
        f.groupIndex = -10;
        light.setContactFilter(f);

        MassData massData = new MassData();
        massData.mass = 60f;
        massData.center.set(new Vector2(0f,0f));
        body.setMassData(massData);

        return player;
    }
}
