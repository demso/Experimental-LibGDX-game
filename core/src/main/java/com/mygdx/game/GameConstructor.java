package com.mygdx.game;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.gamestate.AcceptHandler;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.factories.ItemsFactory;
import com.mygdx.game.gamestate.factories.MobsFactory;
import com.mygdx.game.gamestate.tiledmap.tiled.TmxMapLoader;
import com.mygdx.game.gamestate.GameStageInputListener;
import com.mygdx.game.gamestate.tiledmap.tiled.renderers.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.UI.console.InGameConsole;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;
import com.mygdx.game.gamestate.UI.console.ConsoleCommands;
import com.mygdx.game.gamestate.UI.HUDInputListener;
import com.mygdx.game.net.PlayerInfo;
import com.mygdx.game.net.messages.common.EntityInfo;
import com.mygdx.game.net.messages.server.OnConnection;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.player.ClientPlayerConstructor;
import com.mygdx.game.gamestate.tiledmap.loader.ClientMapLoader;
import dev.lyze.gdxUnBox2d.UnBox;

public class GameConstructor {
    GameState gameState;
    public GameState createGameState(OnConnection msg){
        gameState = new GameState();
        GameState.instance = gameState;

        gameState.game = SecondGDXGame.instance;
        gameState.gameScreen = SecondGDXGame.instance.gameScreen;
        gameState.font = SecondGDXGame.font;
        gameState.skin = SecondGDXGame.skin;
        gameState.players = new ObjectMap<>();
        gameState.entities = new ObjectMap<>();
        gameState.items = new ObjectMap<>();

        gameState.client = SecondGDXGame.instance.client;
        gameState.acceptHandler = new AcceptHandler(gameState.client, gameState);
        SecondGDXGame.instance.client.handler = gameState.getServerHandler();

        gameState.debugRenderer = new ShapeRenderer();
        gameState.shapeRenderer = new ShapeRenderer();
        gameState.bodies = new Array<>();
        gameState.world = new World(new Vector2(0, 0), true);
        gameState.bodyResolver = new BodyResolver(gameState.world);
        gameState.mobsFactory = new MobsFactory(gameState.world);
        gameState.unbox = new UnBox(gameState.world);
        gameState.unbox.getOptions().setTimeStep(gameState.physicsStep);
        gameState.unbox.getOptions().setInterpolateMovement(false);
        gameState.hud = new HUD(gameState, new ScreenViewport(), gameState.game.batch);
        gameState.camera = new OrthographicCamera();
        gameState.gameStage = new Stage(new ScreenViewport(gameState.camera));
        gameState.gameStage.addListener(new GameStageInputListener());

        gameState.itemsFactory = new ItemsFactory(gameState.items, gameState.unbox, gameState.bodyResolver, gameState.hud, gameState.gameStage);

        gameState.camera.setToOrtho(false, 30, 20);

        gameState.map = new ClientMapLoader(gameState.world).load(msg.map, new TmxMapLoader.Parameters());

        gameState.shapeRenderer.setProjectionMatrix(gameState.camera.combined);
        gameState.renderer = new OrthogonalTiledMapRenderer(gameState.map, 1f / (float) GameState.TILE_SIDE);
        gameState.batch = gameState.renderer.getBatch();
        gameState.debugRendererPh = new Box2DDebugRenderer();

        gameState.renderer.setView(gameState.camera);

        initTextures();
        initScene2D();
        initPhysics();

        gameState.clientPlayer = new ClientPlayerConstructor().createPlayer(gameState, msg.yourPlayerInfo);


        gameState.console = new InGameConsole(SecondGDXGame.instance.skin1x,true);
        gameState.console.setDisplayKeyID(Input.Keys.GRAVE);
        //gameState.console.setNoHoverAlpha(0.5f);
        gameState.console.setCommandExecutor(new ConsoleCommands(gameState));
        gameState.console.setMaxEntries(50);

        if (msg.playersInfo != null) {
            for (PlayerInfo plInf : msg.playersInfo) {
                if (plInf.name.equals(SecondGDXGame.instance.name)) continue;
                gameState.getServerHandler().playerJoined(plInf);
            }
        } else
            HandyHelper.instance.log("[GameConstructor:95] PlayersInfo null in OnConnection");

        if (msg.entitiesInfo != null) {
            for (EntityInfo enInf : msg.entitiesInfo) {
                if (enInf.name.equals(SecondGDXGame.instance.name)) continue;
                gameState.getServerHandler().spawnEntity(enInf);
            }
        } else
            HandyHelper.instance.log("[GameConstructor:95] EntitiesInfo null in OnConnection");

        return gameState;
    }
    private void initTextures(){
        gameState.userSelection = new Texture(Gdx.files.internal("selection.png"));
        gameState.bulletTracer = new Texture(Gdx.files.internal("visual/textures/bullet_tracer_yellow.png"));
        //Texture tex = new Texture(Gdx.files.internal("visual/textures/bullet_tracer_red.png"));

    }

    private void initScene2D(){
        gameState.HUDIL = new HUDInputListener();
        gameState.hud.addListener(gameState.HUDIL);
    }

    private void initPhysics(){
        //light
        gameState.rayHandler = new RayHandler(gameState.world);
        gameState.rayHandler.setCombinedMatrix(gameState.camera);
        RayHandler.useDiffuseLight(true);
        gameState.rayHandler.setAmbientLight(0f, 0f, 0f, 1f);
        gameState.rayHandler.setBlur(true);
        gameState.rayHandler.setBlurNum(2);
    }
}
