package com.mygdx.game;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.gamestate.GameStageInputListener;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.UI.HUDInputListener;
import com.mygdx.game.gamestate.UI.console.ConsoleCommands;
import com.mygdx.game.gamestate.UI.console.InGameConsole;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.factories.ItemsFactory;
import com.mygdx.game.gamestate.factories.MobsFactory;
import com.mygdx.game.gamestate.player.ClientPlayerConstructor;
import com.mygdx.game.gamestate.tiledmap.loader.ServerMapLoader;
import com.mygdx.game.gamestate.tiledmap.tiled.TmxMapLoader;
import com.mygdx.game.gamestate.tiledmap.tiled.renderers.OrthogonalTiledMapRenderer;
import dev.lyze.gdxUnBox2d.UnBox;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameConstructor {
    GameState gameState;
    public GameState createGameState(){
        gameState = new GameState();
        GameState.instance = gameState;

        gameState.game = SecondGDXGame.instance;
        gameState.gameScreen = SecondGDXGame.instance.gameScreen;
        gameState.font = SecondGDXGame.font;
        gameState.skin = SecondGDXGame.skin;
        gameState.players = new ObjectMap<>();
        gameState.entities = new ObjectMap<>();
        gameState.items = new ObjectMap<>();

        //SecondGDXGame.instance.client.handler = gameState.getServerHandler();

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
        var GSIL = new GameStageInputListener();
        gameState.gameStage.addListener(GSIL);
        gameState.GSIL = GSIL;

        gameState.itemsFactory = new ItemsFactory(gameState.items, gameState.unbox, gameState.bodyResolver, gameState.hud, gameState.gameStage);

        gameState.camera.setToOrtho(false, 30, 20);

        gameState.map = new ServerMapLoader(gameState).load(gameState.mapToLoad, new TmxMapLoader.Parameters());
        Vector2 mapSize = new Vector2((int)gameState.map.getProperties().get("width"),(int) gameState.map.getProperties().get("height"));
        gameState.worldBorders = new Rectangle(0, 0, mapSize.x, mapSize.y);

        gameState.hud.init();

        gameState.shapeRenderer.setProjectionMatrix(gameState.camera.combined);
        gameState.renderer = new OrthogonalTiledMapRenderer(gameState.map, 1f / (float) GameState.TILE_SIDE);
        gameState.batch = gameState.renderer.getBatch();
        gameState.debugRendererPh = new Box2DDebugRenderer();

        gameState.renderer.setView(gameState.camera);

        initTextures();
        initScene2D();
        initPhysics();

        gameState.shapeDrawer = new ShapeDrawer(gameState.batch, new TextureRegion(gameState.userSelection, 1,1,1,1));
        gameState.shapeDrawer.setDefaultLineWidth(0.03f);

        gameState.clientPlayer = new ClientPlayerConstructor().createPlayer(gameState);
        gameState.players.put(gameState.clientPlayer.getId(), gameState.clientPlayer);

        gameState.hud.setClientPlayer(gameState.clientPlayer);

        gameState.console = new InGameConsole(SecondGDXGame.instance.skin1x,true);
        gameState.console.setDisplayKeyID(Input.Keys.GRAVE);
        //gameState.console.setNoHoverAlpha(0.5f);
        gameState.console.setCommandExecutor(new ConsoleCommands(gameState));
        gameState.console.setMaxEntries(50);

        return gameState;
    }
    private void initTextures(){
        gameState.userSelection = new Texture(Gdx.files.internal("selection.png"));
        gameState.bulletTracer = new Texture(Gdx.files.internal("visual/textures/bullet_tracer_yellow.png"));
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
