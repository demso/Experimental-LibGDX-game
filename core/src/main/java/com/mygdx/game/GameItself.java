package com.mygdx.game;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.UI.HUD;
import com.mygdx.game.tiledmap.BodyUserData;
import com.mygdx.game.tiledmap.Door;
import com.mygdx.game.tiledmap.MyTmxMapLoader;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

public class GameItself {
    final static short PLAYER_CF= 0x0008,
        PLAYER_INTERACT_CF =   0x0002,
        LIGHT_CF =             0x4000,
        ALL_CF = Short.MAX_VALUE;
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
    Animation<TextureRegion> walkSide;
    Animation<TextureRegion> walkUp;
    Animation<TextureRegion> walkDown;
    float frameDur = 0.1f;
    ShapeRenderer debugRenderer;
    RayHandler rayHandler;
    public World world;
    Array<Body> bodies;
    float accumulator = 0;
    Box2DDebugRenderer debugRendererPh;
    PointLight light;
    TextureRegion textureRegions[][];
    float zoom = 2 ;
    float speedd = 5f;
    final String mapToLoad = "worldMap/newmap.tmx";
    public static final int tileSide = 32;
    HUD hudStage;
    public Stage gameStage;
    Label label;
    public ObjectIntMap<String> tilemapa;

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

        renderer = new OrthogonalTiledMapRenderer(map, 1f / tileSide);
        batch = renderer.getBatch();
        debugRendererPh = new Box2DDebugRenderer();

        renderer.setView(camera);

        game.player.WIDTH = 0.8f;
        game.player.HEIGHT = 0.8f;
        player.position.set(5,95);

        initTextures();
        initScene2D();
        initPhysics();

        player.addItemToInventory(new Item(map.getTileSets().getTile(tilemapa.get("10mm_fmj", 1)), this, "10mm FMJ bullets"));
        player.addItemToInventory(new Item(map.getTileSets().getTile(tilemapa.get("beef", 1)), this, "Beef"));
        player.addItemToInventory(new Item(map.getTileSets().getTile(tilemapa.get("watches", 1)), this, "Watches"));
        player.addItemToInventory(new Item(map.getTileSets().getTile(tilemapa.get("shotgunammo", 1)), this, "Shotgun ammo"));
    }
    void initTextures(){
        textureSheet = new Texture(Gdx.files.internal("ClassicRPG_Sheet.png"));
        textureRegions= TextureRegion.split(textureSheet, 16, 16);
        userSelection = new Texture(Gdx.files.internal("selection.png"));

        TextureRegion[] walkFrames = new TextureRegion[4];
        int index = 0;
        for (int i = 0; i < 4; i++)
            walkFrames[index++] = textureRegions[0][i];
        walkDown = new Animation<TextureRegion>(frameDur, walkFrames);

        walkFrames = new TextureRegion[4];
        index = 0;
        for (int i = 0; i < 4; i++)
            walkFrames[index++] = textureRegions[1][i];
        walkSide = new Animation<TextureRegion>(frameDur, walkFrames);

        walkFrames = new TextureRegion[4];
        index = 0;
        for (int i = 0; i < 4; i++)
            walkFrames[index++] = textureRegions[3][i];
        walkUp = new Animation<TextureRegion>(frameDur, walkFrames);
    }
    void initScene2D(){
        hudStage.addListener(new HUDInputListener());
    }
    void initPhysics(){
        world.getBodies(bodies);
        world.setContactListener(new ContactListener() {
            static int ll = 0;
            static int llend = 0;
            @Override
            public void beginContact(Contact contact) {
                ll++;
                var dataA = contact.getFixtureA().getBody().getUserData();
                var dataB = contact.getFixtureB().getBody().getUserData();
                if (dataA instanceof BodyUserData && ((BodyUserData) dataA).bodyName.equals("playerInteractionBubble")){
                    player.closeObjects.add(contact.getFixtureB().getBody());
                }
                if (dataB instanceof BodyUserData && ((BodyUserData) dataB).bodyName.equals("playerInteractionBubble")){
                    player.closeObjects.add(contact.getFixtureA().getBody());
                }
            }

            @Override
            public void endContact(Contact contact) {
                llend++;
                var dataA = contact.getFixtureA().getBody().getUserData();
                var dataB = contact.getFixtureB().getBody().getUserData();
                if (dataA instanceof BodyUserData && ((BodyUserData) dataA).bodyName.equals("playerInteractionBubble")){
                    player.closeObjects.removeValue(contact.getFixtureB().getBody(), true);
                }
                if (dataB instanceof BodyUserData && ((BodyUserData) dataB).bodyName.equals("playerInteractionBubble")){
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

        //world
        Item it = new Item(map.getTileSets().getTile(tilemapa.get("10mm_fmj", 1)), this, "10mm FMJ bullets");
        it.allocate(world, new Vector2(3.5f,96.5f));

        //player
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(5, 95));
        Body body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.01f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.filter.categoryBits = PLAYER_CF;
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        body.setUserData(player);
        circle.dispose();

        BodyDef sensorBodyDef = new BodyDef();
        sensorBodyDef.type = BodyDef.BodyType.DynamicBody;
        sensorBodyDef.position.set(new Vector2(5, 95));
        Body sensorBody = world.createBody(sensorBodyDef);
        CircleShape sensorCircle = new CircleShape();
        sensorCircle.setRadius(1f);
        FixtureDef sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.shape = sensorCircle;
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.filter.categoryBits = PLAYER_INTERACT_CF;
        sensorFixtureDef.filter.maskBits = (short) (sensorFixtureDef.filter.maskBits & ~PLAYER_CF);
        sensorBody.createFixture(sensorFixtureDef);
        sensorBody.setFixedRotation(true);
        sensorBody.setSleepingAllowed(false);
        sensorBody.setUserData(new BodyUserData(player, "playerInteractionBubble"));
        sensorCircle.dispose();

        player.sensorBody = sensorBody;
        player.body = body;
        player.body.setLinearDamping(speedd);

        //light
        rayHandler = new RayHandler(world);
        rayHandler.setCombinedMatrix(camera);
        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0f, 0f, 0f, 1f);
        rayHandler.setBlurNum(1);

        light = new PointLight(rayHandler, 1300, Color.WHITE, 100f, 0, 0);
        light.setSoft(true);
        light.setSoftnessLength(2f);
        light.attachToBody(player.body, 0, 0);
        light.setIgnoreAttachedBody(true);
        Filter f = new Filter();
        f.groupIndex = -10;
        light.setContactFilter(f);
    }

    private void update(float deltaTime) {
        //UPDATE PHYSICSSTEP
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= 1/60f) {
            world.step(1/60f, 6, 2);
            accumulator -= 1/60f;
        }

        //UPDATE PLAYER
        if (deltaTime == 0) return;
        if (deltaTime > 0.1f) deltaTime = 0.1f;
        player.stateTime += deltaTime;
        player.body.setLinearVelocity(0,0);

        boolean moveToTheRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean moveToTheLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean moveUp = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean moveDown = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
        if (!(moveToTheRight && moveToTheLeft)) {
            if (moveToTheLeft) {
                player.body.setLinearVelocity(-100f, player.body.getLinearVelocity().y);
                player.state = Player.State.Walking;
                player.facing = Player.Facing.LEFT;
            }

            if (moveToTheRight) {
                player.body.setLinearVelocity(100f, player.body.getLinearVelocity().y);
                player.state = Player.State.Walking;
                player.facing = Player.Facing.RIGHT;
            }
        }
        if (!(moveUp && moveDown)){
            if (moveUp) {
                player.body.setLinearVelocity(player.body.getLinearVelocity().x, 100f);
                player.state = Player.State.Walking;
                player.facing = Player.Facing.UP;
            }

            if (moveDown) {
                player.body.setLinearVelocity(player.body.getLinearVelocity().x, -100f);
                player.state = Player.State.Walking;
                player.facing = Player.Facing.DOWN;
            }
        }

        player.body.setLinearVelocity(player.body.getLinearVelocity().clamp(0,speedd));
        if (Math.abs(player.body.getLinearVelocity().len2()) < 0.5f) {
            player.state = Player.State.Standing;
        }

        player.position.x = (player.body.getPosition().x);
        player.position.y = (player.body.getPosition().y);

        player.sensorBody.setTransform(player.position, 0);

        player.closestObject = player.getClosestObject();

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

        renderPlayer();

        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

        if (player.closestObject != null) {
            batch.begin();
            batch.draw(userSelection, (int)player.closestObject.getPosition().x, (int)player.closestObject.getPosition().y,1,1);
            batch.end();
        }

        hudStage.act(deltaTime);
        hudStage.draw();

        if (debug) {
//            stage.getBatch().begin();
//            font.draw(stage.getBatch(), "FPS=" + Gdx.graphics.getFramesPerSecond(), 0, stage.getCamera().viewportHeight - 2);
//            stage.getBatch().end();
            //renderDebug();
            debugRendererPh.render(world, camera.combined);
        }
    }
    void renderPlayer(){
        //player
        TextureRegion frame = null;
        switch (player.state) {
            case Standing:
                frame = walkDown.getKeyFrame(1);
                break;
            case Walking:
                switch (player.facing) {
                    case RIGHT:
                    case LEFT:
                        frame = walkSide.getKeyFrame(player.stateTime, true);
                        break;
                    case UP:
                        frame = walkUp.getKeyFrame(player.stateTime, true);
                        break;
                    case DOWN:
                        frame = walkDown.getKeyFrame(player.stateTime, true);
                        break;
                }
                break;
        }
        Batch batch = renderer.getBatch();
        batch.begin();
        if (player.facing == Player.Facing.RIGHT)
            batch.draw(frame, player.position.x - player.WIDTH/2 + player.WIDTH, player.position.y - player.WIDTH * 1/4, -player.WIDTH, player.HEIGHT);
        else
            batch.draw(frame, player.position.x - player.WIDTH/2, player.position.y - player.WIDTH * 1/4, player.WIDTH, player.HEIGHT);
        batch.end();
    }

    class HUDInputListener extends InputListener {
        @Override
        public boolean keyUp (InputEvent event, int keycode) {
            if (keycode == Input.Keys.ESCAPE)
                if (hudStage.esClosablePopups.notEmpty()){
                    hudStage.closeInventoryHUD();

                }
                else game.setScreen(game.menuScreen);
            if (keycode == Input.Keys.B){
                debug = !debug;
                hudStage.setDebugAll(debug);
                gameStage.setDebugAll(debug);
            }
            if (keycode == Input.Keys.EQUALS){
                zoom += 0.3f;
                camera.setToOrtho(false, Gdx.graphics.getWidth() * (1f/tileSide) * (1/zoom), Gdx.graphics.getHeight() * (1f/tileSide) * (1/zoom));
            }
            if (keycode == Input.Keys.MINUS){
                zoom -= 0.3f;
                camera.setToOrtho(false, Gdx.graphics.getWidth() * (1f/tileSide) * (1/zoom), Gdx.graphics.getHeight() * (1f/tileSide) * (1/zoom));
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
                        hudStage.updateInvHUD();
                    }
                }
            }
            if (keycode == Input.Keys.I){
                hudStage.toggleInventoryHUD();
            }
            return true;
        }
    }
}



