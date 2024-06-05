package com.mygdx.game.net.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.tiledmap.tiled.TmxMapLoader;
import com.mygdx.game.net.GameServer;
import dev.lyze.gdxUnBox2d.UnBox;

public class ServGameStateConstructor {
    ServGameState gameState;
    public ServGameState createGameState(GameServer serv){
        gameState = new ServGameState();
        ServGameState.instance = gameState;

        gameState.gameServer = serv;
        //gameState.game = SecondGDXGame.instance;
        //gameState.gameScreen = SecondGDXGame.instance.gameScreen;
        //gameState.font = SecondGDXGame.font;
        //gameState.skin = SecondGDXGame.skin;
        gameState.players = new ObjectMap<>();
        gameState.entities = new ObjectMap<>();

//        gameState.serverHandler = new ServerHandler();
//        SecondGDXGame.instance.client.handler = gameState.getServerHandler();

//        gameState.debugRenderer = new ShapeRenderer();
//        gameState.shapeRenderer = new ShapeRenderer();
        gameState.bodies = new Array<>();
        gameState.world = new World(new Vector2(0, 0), true);
        gameState.bodyResolver = new BodyResolver(gameState.world);
        gameState.mobsFactory = new ServerMobsFactory(gameState.world);
        gameState.unbox = new UnBox(gameState.world);
        gameState.unbox.getOptions().setTimeStep(gameState.physicsStep);
        gameState.unbox.getOptions().setInterpolateMovement(false);
//        gameState.hud = new HUD(gameState, new ScreenViewport(), gameState.game.batch);
//        gameState.camera = new OrthographicCamera();
//        gameState.gameStage = new Stage(new ScreenViewport(gameState.camera));
//        gameState.gameStage.addListener(new GameStageInputListener());

        //gameState.camera.setToOrtho(false, 30, 20);

        gameState.map = new ServMapLoader(gameState).load(serv.mapToLoad, new TmxMapLoader.Parameters());

        gameState.serverHandler = new ServHandler(gameState, serv);

//        gameState.shapeRenderer.setProjectionMatrix(gameState.camera.combined);
//        gameState.renderer = new OrthogonalTiledMapRenderer(gameState.map, 1f / (float) GameState.TILE_SIDE);
//        gameState.batch = gameState.renderer.getBatch();
//        gameState.debugRendererPh = new Box2DDebugRenderer();

        //gameState.renderer.setView(gameState.camera);

        //initTextures();
        //initScene2D();
        //initPhysics();

//        gameState.player = new ClientPlayerConstructor().createPlayer(gameState);
//        gameState.player.setPosition(msg.spawnX, msg.spawnY);
//        gameState.player.setName(SecondGDXGame.instance.name);

//        for (PlayerInfo plInf : serv.players){
//            if (plInf.name.equals(SecondGDXGame.instance.name))
//                continue;
//            gameState.getServerHandler().playerJoined(plInf);
//        }

//        gameState.console = new InGameConsole(SecondGDXGame.instance.skin1x,true);
//        gameState.console.setDisplayKeyID(Input.Keys.GRAVE);
//        //gameState.console.setNoHoverAlpha(0.5f);
//        gameState.console.setCommandExecutor(new ConsoleCommands(gameState));
//        gameState.console.setMaxEntries(50);

        //gameState.tester();

       // SecondGDXGame.instance.ready();
        return gameState;
    }
   // private void initTextures(){
//        gameState.userSelection = new Texture(Gdx.files.internal("selection.png"));
//    }

//    private void initScene2D(){
//        gameState.HUDIL = new HUDInputListener();
//        gameState.hud.addListener(gameState.HUDIL);
//    }

//    private void initPhysics(){
//        //light
//        gameState.rayHandler = new RayHandler(gameState.world);
//        gameState.rayHandler.setCombinedMatrix(gameState.camera);
//        RayHandler.useDiffuseLight(true );
//        gameState.rayHandler.setAmbientLight(0f, 0f, 0f, 1f);
//        gameState.rayHandler.setBlur(true);
//        gameState.rayHandler.setBlurNum(2);
//
//        //game
//        Item it = new Item(TileResolver.getTile("10mm_fmj"), "10mm FMJ bullets");
//        it.allocate(new Vector2(3.5f,96.5f));
//    }
}
