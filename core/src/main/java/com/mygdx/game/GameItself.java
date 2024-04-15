package com.mygdx.game;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.UI.HUD;
import com.mygdx.game.behaviours.PlayerHandler;
import com.mygdx.game.entities.MobsFactory;
import com.mygdx.game.entities.Player;
import com.mygdx.game.tiledmap.Door;
import com.mygdx.game.tiledmap.MyTmxMapLoader;
import com.strongjoshua.console.GUIConsole;
import dev.lyze.gdxUnBox2d.UnBox;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

public class GameItself {
    //categoryBits
//    final static short
//        DEFAULT_CF =            0x0001,                 //00000000 00000001
//        PLAYER_CF =             0x0008,                 //00000000 00001000
//        PLAYER_INTERACT_CF =    0x0002,                 //00000000 00000010
//        LIGHT_CF =              Short.MIN_VALUE,        //10000000 00000000
//        BULLET_CF =             0x0004,                 //00000000 00000100
//        ZOMBIE_CF =             0x0010,                 //00000000 00010000
//        ALL_CF =                -1,                     //11111111 11111111
//
//        PLAYER_CG =             -42;
    public static GameItself Instance;

    public SecondGDXGame game;
    public GameScreen gameScreen;
    public static Player player;
    public Skin skin;
    BitmapFont font;
    Batch batch;
    public static TiledMap map;
    OrthogonalTiledMapRenderer renderer;
    public OrthographicCamera camera;
    boolean debug = false;
    Texture userSelection;
    ShapeRenderer debugRenderer;
    RayHandler rayHandler;
    public static World world;
    Array<Body> bodies;
    Box2DDebugRenderer debugRendererPh;
    float zoom = 2 ;
    final String mapToLoad = "newWorldMap/newmap.tmx";
    public static final int TILE_SIDE = 32;
    HUD hudStage;
    public Stage gameStage;
    public static ObjectSet<Body> bodiesToDeletion = new ObjectSet<>();
    float physicsStep;
    public static GUIConsole console;
    ShapeRenderer shapeRenderer;
    public static UnBox unbox;

    GameItself(GameScreen gameScreen){
        this.game = gameScreen.game;
        this.gameScreen = gameScreen;
        this.player = game.player;
        this.font = game.font;
        this.skin = game.skin;

        physicsStep = 1/60f;

        debugRenderer = new ShapeRenderer();
        shapeRenderer = new ShapeRenderer();
        bodies = new Array<>();
        world = new World(new Vector2(0, 0), true);
        unbox = new UnBox(world);
        unbox.getOptions().setTimeStep(physicsStep);
        hudStage = new HUD(this, new ScreenViewport(), game.batch);
        camera = new OrthographicCamera();
        gameStage = new Stage(new ScreenViewport(camera));

        camera.setToOrtho(false, 30, 20);

        map = new MyTmxMapLoader(this).load(mapToLoad);

        renderer = new OrthogonalTiledMapRenderer(map, 1f / TILE_SIDE);
        batch = renderer.getBatch();
        debugRendererPh = new Box2DDebugRenderer();

        renderer.setView(camera);

        game.player.WIDTH = 0.8f;
        game.player.HEIGHT = 0.8f;
        //player.position.set(5,95);

        initTextures();
        initScene2D();
        initPhysics();

        console = new GUIConsole(true);
        console.setNoHoverAlpha(0.5f);
        console.setCommandExecutor(new ConsoleCommands(this));

        tester();
    }
    public void tester(){
        player.addItemToInventory(new Item(TileResolver.getTile("10mm_fmj"), this, "10mm FMJ bullets"));
        player.addItemToInventory(new Item(TileResolver.getTile("beef"), this, "Beef"));
        player.addItemToInventory(new Item(TileResolver.getTile("watches"), this, "Watches"));
        player.addItemToInventory(new Item(TileResolver.getTile("shotgunammo"), this, "Shotgun ammo"));
        player.addItemToInventory(new Item(TileResolver.getTile("deagle_44"), this, "Deagle .44"));
        MobsFactory.spawnZombie(5, 85);
    }
    void initTextures(){
        userSelection = new Texture(Gdx.files.internal("selection.png"));
    }
    void initScene2D(){
        hudStage.addListener(new HUDInputListener());
    }
    void initPhysics(){
        //light
        rayHandler = new RayHandler(world);
        rayHandler.setCombinedMatrix(camera);
        RayHandler.useDiffuseLight(true );
        rayHandler.setAmbientLight(0f, 0f, 0f, 1f);
        rayHandler.setBlur(true);
        rayHandler.setBlurNum(3);

        //game
        Item it = new Item(TileResolver.getTile("10mm_fmj"), this, "10mm FMJ bullets");
        it.allocate(new Vector2(3.5f,96.5f));
        //player
        player.initBody(world, rayHandler);
        //mobs
    }

    private void update(float deltaTime) {
        if (bodiesToDeletion.size != 0) {
            bodiesToDeletion.forEach((Body body) -> world.destroyBody(body));
            bodiesToDeletion.clear();
        }

        //UPDATE PLAYER
        //player.update(deltaTime);

        boolean moveToTheRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean moveToTheLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean moveUp = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean moveDown = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);

        //player.inputMove(moveUp, moveDown, moveToTheRight, moveToTheLeft, deltaTime);

        //UPDATE STAGE
        hudStage.update(debug);

        //CAMERA UPDATE
        camera.position.set(player.getPosition(), 0);
        camera.update();
    }
    void render(float deltaTime){
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        unbox.preRender(deltaTime);

        update(deltaTime);

        renderer.setView(camera);
        renderer.render();

        batch.begin();
        Box2DSprite.draw(batch, world, true);

        unbox.render(batch);

        //player.renderPlayer(renderer.getBatch(), camera);
        batch.end();

        gameStage.act(deltaTime);
        gameStage.draw();

        shapeRenderer.setProjectionMatrix(hudStage.getBatch().getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.line( hudStage.getWidth()/2f, hudStage.getHeight()/2f, Gdx.input.getX(), hudStage.getHeight()-Gdx.input.getY());
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.end();

        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

        if (player.closestObject != null) {
            float w = Box2DUtils.width(player.closestObject);
            float h = Box2DUtils.height(player.closestObject);
            batch.begin();
            batch.draw(userSelection, player.closestObject.getPosition().x-w/2f, player.closestObject.getPosition().y-h/2f, w,h);
            batch.end();
        }

        unbox.postRender();

        hudStage.act(deltaTime);
        hudStage.draw();

        if (debug) {
            hudStage.getBatch().begin();
            font.draw(hudStage.getBatch(), "FPS=" + Gdx.graphics.getFramesPerSecond(), 0, hudStage.getCamera().viewportHeight - 2);
            hudStage.getBatch().end();
            renderDebug();
            debugRendererPh.render(world, camera.combined);
        }

        console.draw();
    }
    public void fireBullet(Player pla){
        Vector3 mousePos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 vv = new Vector2(mousePos.x-player.getPosition().x, mousePos.y-player.getPosition().y);
        new Bullet(TileResolver.getTile("bullet"), pla.getPosition(), vv);
    }

    Vector2 beginV;
    Vector2 endV;
    private void renderDebug () {
        if (beginV != null && endV != null) {
            debugRenderer.setProjectionMatrix(camera.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.setColor(Color.RED);
            debugRenderer.line(beginV, endV);
            debugRenderer.setColor(Color.YELLOW);
            debugRenderer.end();
        }
    }

    class HUDInputListener extends InputListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            super.touchDown(event, x, y, pointer, button);
            return false;
        }

        @Override
        public boolean keyUp (InputEvent event, int keycode) {
            if (keycode == Input.Keys.ESCAPE)
                if (hudStage.esClosablePopups.notEmpty()){
                    hudStage.closeTopPopup();
                }
                else game.setScreen(game.menuScreen);
            if (keycode == Input.Keys.B){
                debug = !debug;
                hudStage.setDebugAll(debug);
                gameStage.setDebugAll(debug);
            }
            if (keycode == Input.Keys.EQUALS){
                zoom += 0.3f;
                camera.setToOrtho(false, Gdx.graphics.getWidth() * (1f/ TILE_SIDE) * (1/zoom), Gdx.graphics.getHeight() * (1f/ TILE_SIDE) * (1/zoom));
            }
            if (keycode == Input.Keys.MINUS){
                zoom -= 0.3f;
                camera.setToOrtho(false, Gdx.graphics.getWidth() * (1f/ TILE_SIDE) * (1/zoom), Gdx.graphics.getHeight() * (1f/ TILE_SIDE) * (1/zoom));
            }
            if (keycode == Input.Keys.R){
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    player.revive();
                    return false;
                }
                System.out.println(player.getClosestObject());
            }
            if (keycode == Input.Keys.E){
                if (player.closestObject != null) {
                    var obj = player.closestObject.getUserData();
                    if (obj instanceof Door) {
                        ((Door) obj).doAction();
                    }
                    if (obj instanceof Item){
                        player.pickupItem((Item) obj);
                        hudStage.updateInvHUDContent();
                    }
                }
            }
            if (keycode == Input.Keys.I){
                hudStage.toggleInventoryHUD();
            }
            if (keycode == Input.Keys.H){
                player.freeHands();
            }
            if (keycode == Input.Keys.T){
                if (player.equipedItem != null && player.equipedItem.itemName.equals("Deagle .44"))
                    fireBullet(player);
            }
            if (keycode == Input.Keys.W || keycode == Input.Keys.UP){
                player.playerObject.getBehaviour(PlayerHandler.class).moveUp = false;
            }
            if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN){
                player.playerObject.getBehaviour(PlayerHandler.class).moveDown = false;
            }
            if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT){
                player.playerObject.getBehaviour(PlayerHandler.class).moveToTheLeft = false;
            }
            if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT){
                player.playerObject.getBehaviour(PlayerHandler.class).moveToTheRight = false;
            }
            return false;
        }

        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Input.Keys.W || keycode == Input.Keys.UP){
                player.playerObject.getBehaviour(PlayerHandler.class).moveUp = true;
            }
            if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN){
                player.playerObject.getBehaviour(PlayerHandler.class).moveDown = true;
            }
            if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT){
                player.playerObject.getBehaviour(PlayerHandler.class).moveToTheLeft = true;
            }
            if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT){
                player.playerObject.getBehaviour(PlayerHandler.class).moveToTheRight = true;
            }
            return false;
        }
    }
}



