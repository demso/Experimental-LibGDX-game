package com.mygdx.game;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.UI.HUD;
import com.mygdx.game.tiledmap.BodyUserData;
import com.mygdx.game.tiledmap.BodyUserName;
import com.mygdx.game.tiledmap.Door;
import com.mygdx.game.tiledmap.MyTmxMapLoader;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

public class GameItself {
    final static short
        PLAYER_CF =             0x0008,
        PLAYER_CG =             -42,
        PLAYER_INTERACT_CF =    0x0002,
        LIGHT_CF =              0x4000,
        BULLET_CF =             0x0004,
        ALL_CF =                Short.MAX_VALUE;

    public SecondGDXGame game;
    public GameScreen gameScreen;
    public Player player;
    public Skin skin;
    BitmapFont font;
    Batch batch;
    public TiledMap map;
    OrthogonalTiledMapRenderer renderer;
    public OrthographicCamera camera;
    boolean debug = false;
    Texture textureSheet;
    Texture userSelection;
    float frameDur = 0.1f;
    ShapeRenderer debugRenderer;
    RayHandler rayHandler;
    public World world;
    Array<Body> bodies;
    float accumulator = 0;
    Box2DDebugRenderer debugRendererPh;
    TextureRegion textureRegions[][];
    float zoom = 2 ;
    final String mapToLoad = "worldMap/newmap.tmx";
    public static final int TILE_SIDE = 32;
    HUD hudStage;
    public Stage gameStage;
    public ObjectIntMap<String> tilemapa;
    ObjectSet<Body> bodiesToDeletion = new ObjectSet<>();

    GameItself(GameScreen gameScreen){
        this.game = gameScreen.game;
        this.gameScreen = gameScreen;
        this.player = game.player;
        this.font = game.font;
        this.skin = game.skin;

        debugRenderer = new ShapeRenderer();
        bodies = new Array<>();
        world = new World(new Vector2(0, 0), true);
        hudStage = new HUD(this, new ScreenViewport(), game.batch);
        camera = new OrthographicCamera();
        gameStage = new Stage(new ScreenViewport(camera));

        camera.setToOrtho(false, 30, 20);

        tilemapa = new ObjectIntMap<>();
        map = new MyTmxMapLoader(this).load(mapToLoad);

        renderer = new OrthogonalTiledMapRenderer(map, 1f / TILE_SIDE);
        batch = renderer.getBatch();
        debugRendererPh = new Box2DDebugRenderer();

        renderer.setView(camera);

        game.player.WIDTH = 0.8f;
        game.player.HEIGHT = 0.8f;
        player.position.set(5,95);

        initTextures();
        initScene2D();
        initPhysics();

        player.addItemToInventory(new Item(map.getTileSets().getTile(tilemapa.get("10mm_fmj", 958)), this, "10mm FMJ bullets"));
        player.addItemToInventory(new Item(map.getTileSets().getTile(tilemapa.get("beef", 958)), this, "Beef"));
        player.addItemToInventory(new Item(map.getTileSets().getTile(tilemapa.get("watches", 958)), this, "Watches"));
        player.addItemToInventory(new Item(map.getTileSets().getTile(tilemapa.get("shotgunammo", 958)), this, "Shotgun ammo"));
        player.addItemToInventory(new Item(map.getTileSets().getTile(tilemapa.get("deagle_44", 958)), this, "Deagle .44"));
//        player.equipItem(player.getInventoryItems().select(new Predicate<Item>() {
//            @Override
//            public boolean evaluate(Item arg0) {
//                if (arg0.itemName.equals("Deagle .44"))
//                    return true;
//                else
//                    return false;
//            }
//        }).iterator().next());
    }
    void initTextures(){
        userSelection = new Texture(Gdx.files.internal("selection.png"));
    }
    void initScene2D(){
        hudStage.addListener(new HUDInputListener());
    }
    void initPhysics(){
        //world
        world.setContactListener(new ContactListener() {
            static int ll = 0;
            static int llend = 0;
            @Override
            public void beginContact(Contact contact) {
                ll++;
                Fixture[] fixtures = new Fixture[]{contact.getFixtureA(), contact.getFixtureB()};
                for (int i = 0; i < 2; i++){
                    Fixture thisFixture = fixtures[i];
                    Fixture anotherFixture = fixtures[(i+1)%2];
                    Body thisBody = thisFixture.getBody();
                    Body anotherBody = anotherFixture.getBody();
                    Object userData = thisBody.getUserData();
                    if (userData instanceof BodyUserName){
                        String bodyUserName = ((BodyUserName) userData).getName();
                        switch (bodyUserName){
                            case "player" -> { if (thisFixture.isSensor() && !anotherFixture.isSensor()) player.closeObjects.add(anotherBody); }
                            case "bullet" -> bodiesToDeletion.add(thisBody);
                        }
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
                llend++;
                var fixtureA = contact.getFixtureA();
                var fixtureB = contact.getFixtureB();
                var dataA = contact.getFixtureA().getBody().getUserData();
                var dataB = contact.getFixtureB().getBody().getUserData();
                if (dataA instanceof BodyUserData && ((BodyUserData) dataA).bodyName.equals("player") && fixtureA.isSensor() && !fixtureB.isSensor()){
                    player.closeObjects.removeValue(contact.getFixtureB().getBody(), true);
                }
                if (dataB instanceof BodyUserData && ((BodyUserData) dataB).bodyName.equals("player") && !fixtureA.isSensor() && fixtureB.isSensor()){
                    player.closeObjects.removeValue(contact.getFixtureA().getBody(), true);
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
        //light
        rayHandler = new RayHandler(world);
        rayHandler.setCombinedMatrix(camera);
        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0f, 0f, 0f, 1f);
        rayHandler.setBlurNum(1);

        //game
        Item it = new Item(map.getTileSets().getTile(tilemapa.get("10mm_fmj", 1)), this, "10mm FMJ bullets");
        it.allocate(world, new Vector2(3.5f,96.5f));

        //player
        player.initBody(world, rayHandler);
    }

    private void update(float deltaTime) {
        //UPDATE PHYSICSSTEP
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= 1/60f) {
            world.step(1/60f, 8, 3);
            accumulator -= 1/60f;
        }
        if (bodiesToDeletion.size != 0) {
            bodiesToDeletion.forEach((Body body) -> world.destroyBody(body));
            bodiesToDeletion.clear();
        }

        //UPDATE PLAYER
        player.update(deltaTime);

        boolean moveToTheRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean moveToTheLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean moveUp = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean moveDown = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);

        player.inputMove(moveUp, moveDown, moveToTheRight, moveToTheLeft);

        //UPDATE STAGE
        hudStage.update(debug);
    }

    void render(float deltaTime){
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(deltaTime);

        camera.position.x = player.position.x;
        camera.position.y = player.position.y;

        camera.update();

        renderer.setView(camera);
        renderer.render();

        gameStage.act(deltaTime);
        gameStage.draw();

        batch.begin();
        Box2DSprite.draw(batch, world, true);
        batch.end();

        player.renderPlayer(renderer.getBatch(), camera);

        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

        if (player.closestObject != null) {
            float w = Box2DUtils.width(player.closestObject);
            float h = Box2DUtils.height(player.closestObject);
            batch.begin();
            batch.draw(userSelection, player.closestObject.getPosition().x-w/2f, player.closestObject.getPosition().y-h/2f, w,h);
            batch.end();
        }

        hudStage.act(deltaTime);
        hudStage.draw();

        if (debug) {
            hudStage.getBatch().begin();
            font.draw(hudStage.getBatch(), "FPS=" + Gdx.graphics.getFramesPerSecond(), 0, hudStage.getCamera().viewportHeight - 2);
            hudStage.getBatch().end();
            renderDebug();
            debugRendererPh.render(world, camera.combined);
        }
    }

    public void fireBullet(Player pla){
        float bulletSpeed = 20f;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pla.position);
        Body body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.04f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.filter.categoryBits = BULLET_CF;
        fixtureDef.filter.maskBits = (short) (fixtureDef.filter.maskBits & ~LIGHT_CF);
        body.setBullet(true);
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);

        CustomBox2DSprite bSprite = new CustomBox2DSprite(map.getTileSets().getTile(tilemapa.get("bullet", 958)).getTextureRegion(), "bullet");
        bSprite.setSize(0.5f, 0.5f);

        body.setUserData(bSprite);

        circle.dispose();
        Vector3 mousePos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 vv = new Vector2(mousePos.x-player.position.x, mousePos.y-player.position.y);

        beginV = body.getPosition();
        endV = new Vector2(mousePos.x, mousePos.y);

        vv.nor().scl(bulletSpeed);
        Filter filter = body.getFixtureList().get(0).getFilterData();
        filter.maskBits = (short) (filter.maskBits & ~LIGHT_CF & ~PLAYER_CF & ~PLAYER_INTERACT_CF);
        body.getFixtureList().get(0).setFilterData(filter);

        //body.applyForceToCenter(vv, true);
        body.applyLinearImpulse(vv, body.getPosition(), true);
        //body.setLinearVelocity(vv);
    }



    public void spawnMobs(){

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
        static boolean ctrl = false;
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            super.touchDown(event, x, y, pointer, button);
            if (event.getKeyCode() == Input.Keys.CONTROL_LEFT){
                ctrl = true;
            }
            return true;
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
            if (event.getKeyCode() == Input.Keys.CONTROL_LEFT){
                ctrl = false;
            }
            if (event.getKeyCode() == Input.Keys.CONTROL_LEFT){
                ctrl = false;
            }

            return true;
        }
    }
}



