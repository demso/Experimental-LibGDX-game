package com.mygdx.game.net.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.factories.ItemsFactory;
import com.mygdx.game.gamestate.tiledmap.tiled.TiledMapTileLayer;
import com.mygdx.game.gamestate.tiledmap.tiled.TmxMapLoader;
import com.mygdx.game.net.GameServer;
import dev.lyze.gdxUnBox2d.UnBox;

import java.util.Collections;
import java.util.HashMap;

public class ServGameStateConstructor {
    ServGameState gameState;
    public ServGameState createGameState(GameServer serv){
        gameState = new ServGameState();
        ServGameState.instance = gameState;

        gameState.gameServer = serv;

        gameState.players = Collections.synchronizedMap(new HashMap<>());
        gameState.entities = Collections.synchronizedMap(new HashMap<>());
        gameState.items = Collections.synchronizedMap(new HashMap<>());

        gameState.bodies = new Array<>();
        gameState.world = new World(new Vector2(0, 0), true);
        gameState.bodyResolver = new BodyResolver(gameState.world);
        gameState.mobsFactory = new ServerMobsFactory(gameState.world);
        gameState.unbox = new UnBox(gameState.world);
        gameState.unbox.getOptions().setTimeStep(gameState.physicsStep);
        gameState.unbox.getOptions().setInterpolateMovement(false);

        gameState.itemsFactory = new ItemsFactory(gameState.unbox, gameState.bodyResolver, null, null);

        gameState.map = new ServerMapLoader(gameState).load(serv.mapToLoad, new TmxMapLoader.Parameters());
        gameState.obstaclesLayer = ((TiledMapTileLayer)gameState.map.getLayers().get("obstacles"));

        gameState.serverHandler = new ServHandler(gameState, serv);
        return gameState;
    }
}
