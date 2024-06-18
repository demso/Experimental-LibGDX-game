package com.mygdx.game.net.server;

public class ServGameStateConstructor {
    //ServerGameState gameState;
//    public ServerGameState createGameState(GameServer serv){
//        gameState = new ServerGameState();
//        ServerGameState.instance = gameState;
//
//        gameState.gameServer = serv;
//
//        gameState.players = Collections.synchronizedMap(new HashMap<>());
//        gameState.entities = Collections.synchronizedMap(new HashMap<>());
//        gameState.items = Collections.synchronizedMap(new HashMap<>());
//
//        gameState.bodies = new Array<>();
//        gameState.world = new World(new Vector2(0, 0), true);
//        gameState.bodyResolver = new BodyResolver(gameState.world);
//        gameState.mobsFactory = new ServerMobsFactory(gameState.world);
//        gameState.unbox = new UnBox(gameState.world);
//        gameState.unbox.getOptions().setTimeStep(gameState.physicsStep);
//        gameState.unbox.getOptions().setInterpolateMovement(false);
//
//        gameState.itemsFactory = new ItemsFactory(gameState.items, gameState.unbox, gameState.bodyResolver, null, null);
//
//        gameState.map = new ServerMapLoader(gameState).load(serv.mapToLoad, new TmxMapLoader.Parameters());
//        gameState.obstaclesLayer = ((TiledMapTileLayer)gameState.map.getLayers().get("obstacles"));
//
//        gameState.serverHandler = new ServHandler(gameState, serv);
//        return gameState;
//    }
}
