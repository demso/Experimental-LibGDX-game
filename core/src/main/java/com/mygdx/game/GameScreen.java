package com.mygdx.game;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.jetbrains.annotations.NotNull;

public class GameScreen implements Screen {

    final static short PLAYER_CF= 0x0008,
    PLAYER_INTERACT_CF =   0x0002,
    LIGHT_CF =             0x4000,
    ALL_CF = Short.MAX_VALUE;

    SecondGDXGame game;
    Player player;
    Skin skin;
    BitmapFont font;
    Batch batch;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private boolean debug = false;
    Texture textureSheet;
    Texture userSelection;
    Animation<TextureRegion> walkSide;
    Animation<TextureRegion> walkUp;
    Animation<TextureRegion> walkDown;
    float frameDur = 0.1f;
    private ShapeRenderer debugRenderer;
    private RayHandler rayHandler;
    private World world;
    private Array<Body> bodies;
    private float accumulator = 0;
    Box2DDebugRenderer debugRendererPh;
    Array<Body> staticObjects = new Array<>() ;
    PointLight light;

    TextureRegion textureRegions[][];
    private float zoom = 4 ;
    private float speedd = 5f;
    private final String mapToLoad = "newmap.tmx";
    private final int tileSide = 32;

    Stage stage;
    Label label;

    GameScreen(@NotNull SecondGDXGame game){
        this.game = game;
        font = game.font;
        skin = game.skin;
        stage = new Stage(new ScreenViewport(), game.batch);
        debugRenderer = new ShapeRenderer();
        Box2D.init();
        bodies = new Array<>();
        world = new World(new Vector2(0, 0), true);
        world.getBodies(bodies);
        world.setContactListener(new ContactListener() {
            static int ll = 0;
            static int llend = 0;
            @Override
            public void beginContact(Contact contact) {
                ll++;
                System.out.println("начало контакта (A) " + contact.getFixtureA().getBody().getUserData() + " c (B) " + contact.getFixtureB().getBody().getUserData());
                var dataA = contact.getFixtureA().getBody().getUserData();
                var dataB = contact.getFixtureB().getBody().getUserData();
                if (dataA instanceof BodyUserData && ((BodyUserData) dataA).bodyName.equals("playerInteractionBubble")){
                    player.closeObjects.add(contact.getFixtureB().getBody());
                }
                if (dataB instanceof BodyUserData && ((BodyUserData) dataB).bodyName.equals("playerInteractionBubble")){
                    player.closeObjects.add(contact.getFixtureA().getBody());
                }
                //               if (contact.getFixtureB().getBody().getUserData() instanceof Player){
//                   world.getBodies(bodies);
//                   ll++;
//                   System.out.println("начало контакта игрока "+ll);
//               }
            }

            @Override
            public void endContact(Contact contact) {
                llend++;
                System.out.println("конец контакта (A) " + contact.getFixtureA().getBody().getUserData() + " c (B) " + contact.getFixtureB().getBody().getUserData());
                var dataA = contact.getFixtureA().getBody().getUserData();
                var dataB = contact.getFixtureB().getBody().getUserData();
                if (dataA instanceof BodyUserData && ((BodyUserData) dataA).bodyName.equals("playerInteractionBubble")){
                    player.closeObjects.removeValue(contact.getFixtureB().getBody(), true);
                }
                if (dataB instanceof BodyUserData && ((BodyUserData) dataB).bodyName.equals("playerInteractionBubble")){
                    player.closeObjects.removeValue(contact.getFixtureA().getBody(), true);
                }
//                if (contact.getFixtureB().getBody().getUserData() instanceof Player){
//                    llend++;
//                    System.out.println("конец контакта игрока "+llend);
//                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        loadMap();

        renderer = new OrthogonalTiledMapRenderer(map, 1f / tileSide);
        batch = renderer.getBatch();
        debugRendererPh = new Box2DDebugRenderer();
        textureSheet = new Texture(Gdx.files.internal("ClassicRPG_Sheet.png"));
        textureRegions= TextureRegion.split(textureSheet, 16, 16);
        userSelection = new Texture(Gdx.files.internal("selection.png"));
        camera = new OrthographicCamera();
        player = game.player;

        camera.setToOrtho(false, 30, 20);
        camera.update();
        renderer.setView(camera);

        game.player.WIDTH = 0.8f;
        game.player.HEIGHT = 0.8f;
        player.position.set(5,95);

        loadAnimations();
        initPhysics();
        initLights();

        label = new Label("", skin);
        label.setFontScale(0.5f);
        label.setWidth(350);
        label.setAlignment(Align.topLeft);
        stage.addActor(label);
        stage.addListener(new InputListener(){
            @Override
            public boolean keyUp (InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE)
                    game.setScreen(game.menuScreen);
                if (keycode == Input.Keys.B)
                    stage.setDebugAll(!stage.isDebugAll());
                if (keycode == Input.Keys.EQUALS){
                    zoom += 0.5f;
                    camera.setToOrtho(false, Gdx.graphics.getWidth() * (1/16f) * (1/zoom), Gdx.graphics.getHeight() * (1/16f) * (1/zoom));
                }
                if (keycode == Input.Keys.MINUS){
                    zoom -= 0.5f;
                    camera.setToOrtho(false, Gdx.graphics.getWidth() * (1/16f) * (1/zoom), Gdx.graphics.getHeight() * (1/16f) * (1/zoom));
                }
                if (keycode == Input.Keys.R){
                    System.out.println(player.getClosestObject());
                }
                if (keycode == Input.Keys.E){
                    if (player.closestObject != null) {
                        var obj = player.closestObject.getUserData();
                        if (player.closestObject.getUserData() instanceof Door) {
                            ((Door) obj).doAction();
                        }
                    }
                }
                return true;
            }
        });
    }

    void loadMap(){
        map = new TmxMapLoader().load(mapToLoad);
        MapLayers mlayers = map.getLayers();
        // var what = ((TiledMapTileMapObject) mlayers.get("shadows").getObjects().get(0)).getProperties();
        var obstaclesLayer = (TiledMapTileLayer) mlayers.get("obstacles");
        var groundLayer = (TiledMapTileLayer) mlayers.get("ground");

        BodyDef fullBodyDef = new BodyDef();
        PolygonShape fullBox = new PolygonShape();
        FixtureDef fullFixtureDef = new FixtureDef();
        fullBox.setAsBox(0.5f, 0.5f);
        fullFixtureDef.shape = fullBox;
        fullFixtureDef.filter.groupIndex = 0;

        BodyDef metalClosetBodyDef = new BodyDef();
        PolygonShape metalClosetBox = new PolygonShape();
        FixtureDef metalClosetFixtureDef = new FixtureDef();
        metalClosetBox.setAsBox(0.33f, 0.25f);
        metalClosetFixtureDef.shape = metalClosetBox;
        metalClosetFixtureDef.filter.groupIndex = 0;

        BodyDef windowVertBodyDef = new BodyDef();
        PolygonShape windowVertBox = new PolygonShape();
        FixtureDef windowVertFixtureDef = new FixtureDef();
        windowVertBox.setAsBox(0.05f, 0.5f);
        windowVertFixtureDef.shape = windowVertBox;
        windowVertFixtureDef.filter.groupIndex = -10;

        BodyDef windowHorBodyDef = new BodyDef();
        PolygonShape windowHorBox = new PolygonShape();
        FixtureDef windowHorFixtureDef = new FixtureDef();
        windowHorBox.setAsBox(0.5f, 0.05f);
        windowHorFixtureDef.shape = windowHorBox;
        windowHorFixtureDef.filter.groupIndex = -10;

        BodyDef transparentBodyDef = new BodyDef();
        PolygonShape transparentBox = new PolygonShape();
        FixtureDef transparentFixtureDef = new FixtureDef();
        transparentBox.setAsBox(0.5f, 0.5f);
        transparentFixtureDef.shape = transparentBox;
        transparentFixtureDef.filter.groupIndex = -10;

        Body fullBody;

        for(var i = 0; i < obstaclesLayer.getWidth(); i++)
            for(var j = 0; j < obstaclesLayer.getHeight(); j++){
                var cell = obstaclesLayer.getCell(i, j);
                if (cell != null && cell.getTile().getProperties().get("type") != null){
                    var df = cell.getTile().getProperties();
                    switch (cell.getTile().getProperties().get("type").toString()){
                        case "wall":
                            fullBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                            fullBody = world.createBody(fullBodyDef);
                            fullBody.createFixture(fullFixtureDef);
                            fullBody.setUserData(new BodyUserData(cell, "betonWall"));
                            staticObjects.add(fullBody);
                            break;
                        case "fullBody":
                            fullBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                            fullBody = world.createBody(fullBodyDef);
                            fullBody.createFixture(fullFixtureDef);
                            fullBody.setUserData(new BodyUserData(cell, "mareFullBody"));
                            staticObjects.add(fullBody);
                            break;
                        case "metalCloset":
                            metalClosetBodyDef.position.set(new Vector2(i+0.5f, j+0.3f));
                            Body metalClosetBody = world.createBody(metalClosetBodyDef);
                            metalClosetBody.createFixture(metalClosetFixtureDef);
                            metalClosetBody.setUserData(new BodyUserData(cell, "metalCloset"));
                            staticObjects.add(metalClosetBody);
                            break;
                        case "window":
                            boolean southWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_0 && cell.getFlipVertically() && cell.getFlipVertically();
                            boolean northWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_0 && !cell.getFlipVertically() && !cell.getFlipVertically();
                            boolean eastWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_270 && !cell.getFlipVertically() && !cell.getFlipVertically();
                            boolean westWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_90 && !cell.getFlipVertically() && !cell.getFlipVertically();
//                            boolean southWard = groundLayer.getCell(i, j + 1).getTile().getProperties().get("supercustomproperty", "", String.class).equals("floor");
//                            boolean northWard = groundLayer.getCell(i, j - 1).getTile().getProperties().get("supercustomproperty", "", String.class).equals("floor");
//                            boolean eastWard = groundLayer.getCell(i - 1, j).getTile().getProperties().get("supercustomproperty", "", String.class).equals("floor");
//                            boolean westWard = groundLayer.getCell(i + 1, j).getTile().getProperties().get("supercustomproperty", "", String.class).equals("floor");
//                            if(northWard && southWard && westWard && eastWard || !northWard && !southWard && !westWard && !eastWard){
//                                transparentBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
//                                Body transparentBody = world.createBody(transparentBodyDef);
//                                transparentBody.createFixture(transparentFixtureDef);
//                                transparentBody.setUserData(cell);
//                                staticObjects.add(transparentBody);
//                            }
//                            else
                            if(northWard){
                                windowHorBodyDef.position.set(new Vector2(i+0.5f, j+0.95f));
                                Body windowHorBody = world.createBody(windowHorBodyDef);
                                windowHorBody.createFixture(windowHorFixtureDef);
                                windowHorBody.setUserData(new BodyUserData(cell, "northWindow"));
                                staticObjects.add(windowHorBody);
                            }
                            else if(southWard){
                                windowHorBodyDef.position.set(new Vector2(i+0.5f, j+0.05f));
                                Body windowHorBody = world.createBody(windowHorBodyDef);
                                windowHorBody.createFixture(windowHorFixtureDef);
                                windowHorBody.setUserData(new BodyUserData(cell, "southWindow"));
                                staticObjects.add(windowHorBody);
                            }
                            else if(westWard){
                                windowVertBodyDef.position.set(new Vector2(i+0.05f, j+0.5f));
                                Body windowVertBody = world.createBody(windowVertBodyDef);
                                windowVertBody.createFixture(windowVertFixtureDef);
                                windowVertBody.setUserData(new BodyUserData(cell, "westWindow"));
                                staticObjects.add(windowVertBody);
                            }
                            else if(eastWard){
                                windowVertBodyDef.position.set(new Vector2(i+0.95f, j+0.5f));
                                Body windowVertBody = world.createBody(windowVertBodyDef);
                                windowVertBody.createFixture(windowVertFixtureDef);
                                windowVertBody.setUserData(new BodyUserData(cell, "eastWindow"));
                                staticObjects.add(windowVertBody);
                            }
                            break;
                        case "door":
                            fullBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                            fullBody = world.createBody(fullBodyDef);
                            fullBody.createFixture(fullFixtureDef);
                            fullBody.setUserData(new Door(this,cell, fullBody, map.getTileSets().getTileSet("normalTerrain").getTile(160), map.getTileSets().getTileSet("normalTerrain").getTile(110), i, j));
                            staticObjects.add(fullBody);
                            break;
                    }
                }
            }
    }
    void loadAnimations() {
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
    public void initLights(){
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
        //f.categoryBits = LIGHT_CF;
        f.groupIndex = -10;
        light.setContactFilter(f);
    }
    public void initPhysics(){
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

    }
    @Override
    public void render (float deltaTime) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        doPhysicsStep(deltaTime);
        updatePlayer(deltaTime);

        camera.position.x = player.position.x;
        camera.position.y = player.position.y;

        camera.update();

        renderer.setView(camera);
        renderer.render();

        renderPlayer(deltaTime);

        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

        if (player.closestObject != null) {
            batch.begin();
            batch.draw(userSelection, (int)player.closestObject.getPosition().x, (int)player.closestObject.getPosition().y,1,1);
            batch.end();
        }

        updateStage();
        stage.act(deltaTime);
        stage.draw();

        if (debug) {
            stage.getBatch().begin();
            font.draw(stage.getBatch(), "FPS=" + Gdx.graphics.getFramesPerSecond(), 0, stage.getCamera().viewportHeight - 2);
            stage.getBatch().end();
            //renderDebug();
            debugRendererPh.render(world, camera.combined);
        }
    }
    private void updatePlayer(float deltaTime) {
        if (deltaTime == 0) return;
        if (deltaTime > 0.1f) deltaTime = 0.1f;
        player.stateTime += deltaTime;
        //player.body.setLinearVelocity(player.body.getLinearVelocity().add());
        player.body.setLinearVelocity(0,0);
        Vector2 vel = player.body.getLinearVelocity();
        Vector2 pos = player.body.getPosition();

        boolean moveToTheRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean moveToTheLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean moveUp = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean moveDown = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);

        if (!(moveToTheRight && moveToTheLeft)) {
            if (moveToTheLeft) {
                //player.body.applyLinearImpulse(-speedd, 0, pos.x, pos.y, true);
                player.body.setLinearVelocity(-100f, player.body.getLinearVelocity().y);
                player.state = Player.State.Walking;
                player.facing = Player.Facing.LEFT;
            }

            if (moveToTheRight) {
                //player.body.applyLinearImpulse(speedd, 0, pos.x, pos.y, true);
                player.body.setLinearVelocity(100f, player.body.getLinearVelocity().y);
                player.state = Player.State.Walking;
                player.facing = Player.Facing.RIGHT;
            }
        }

        if (!(moveUp && moveDown)){
            if (moveUp) {
                //player.body.applyLinearImpulse(0, speedd, pos.x, pos.y, true);
                player.body.setLinearVelocity(player.body.getLinearVelocity().x, 100f);
                player.state = Player.State.Walking;
                player.facing = Player.Facing.UP;
            }

            if (moveDown) {
                //player.body.applyLinearImpulse(0, -speedd, pos.x, pos.y, true);
                player.body.setLinearVelocity(player.body.getLinearVelocity().x, -100f);
                player.state = Player.State.Walking;
                player.facing = Player.Facing.DOWN;
            }
        }
        player.body.setLinearVelocity(player.body.getLinearVelocity().clamp(0,speedd));
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) debug = !debug;
        if (Math.abs(player.body.getLinearVelocity().len2()) < 0.5f) {
            player.state = Player.State.Standing;
        }

        player.position.x = (player.body.getPosition().x);
        player.position.y = (player.body.getPosition().y);

        player.sensorBody.setTransform(player.position, 0);

        player.closestObject = player.getClosestObject();
    }
    private void updateStage(){
        float height = Gdx.graphics.getHeight();
        float width = Gdx.graphics.getWidth();
        label.setPosition(100, height - 100);
        if(debug)
            drawTileDebugInfo();
        else
            label.setText("");
    }
    Body clObj;
    StringBuilder labelText = new StringBuilder();
    private void drawTileDebugInfo() {
        labelText = new StringBuilder();
        labelText.append("Player velocity : ").append(player.body.getLinearVelocity()).append("\n");
        clObj = player.closestObject;
        labelText.append("Closest object : ").append(clObj == null ? null : clObj.getUserData() instanceof BodyUserData ? ((BodyUserData) clObj.getUserData()).bodyName + " " + clObj.getPosition() : clObj.getUserData()).append("\n\n");
        Vector3 mouse_position = new Vector3(0,0,0);
        Vector3 tilePosition = camera.unproject(mouse_position.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0));
        int tileX = (int)Math.floor(tilePosition.x);
        int tileY = (int)Math.floor(tilePosition.y);
        for (var x = 0; x < map.getLayers().size(); x++){
            TiledMapTileLayer currentLayer = (TiledMapTileLayer)map.getLayers().get(x);
            TiledMapTileLayer.Cell mcell = currentLayer.getCell(tileX, tileY);

            if(mcell != null){
                labelText.append("Rotation : ").append(mcell.getRotation()).append("\nFlip Horizontally : ").append(mcell.getFlipHorizontally()).append("\nFlip Vertically : ").append(mcell.getFlipVertically()).append("\nLayer : ").append(currentLayer.getName()).append("\nX : ").append(tileX).append("\n").append("Y : ").append(tileY).append("\n").append("ID : ").append(mcell.getTile().getId()).append("\n");
                var itrK = mcell.getTile().getProperties().getKeys();
                var itrV = mcell.getTile().getProperties().getValues();
                while (itrK.hasNext()){
                    labelText.append(itrK.next()).append(" : ").append(itrV.next()).append("\n");
                }
            }
            labelText.append("\n");
        }
        label.setText(labelText);
    }
    private void doPhysicsStep(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= 1/60f) {
            world.step(1/60f, 6, 2);
            accumulator -= 1/60f;
        }
    }
    private void renderPlayer(float deltaTime) {
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
    private void renderDebug () {
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        debugRenderer.rect(player.position.x - player.WIDTH/2, player.position.y - player.HEIGHT/2, player.WIDTH, player.HEIGHT);
        debugRenderer.setColor(Color.YELLOW);
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get("layer1");
        for (int y = 0; y <= layer.getHeight(); y++) {
            for (int x = 0; x <= layer.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    if (camera.frustum.boundsInFrustum(x + 0.5f, y + 0.5f, 0, 1, 1, 0)) debugRenderer.rect(x, y, 1, 1);
                }
            }
        }
        debugRenderer.end();
    }
    @Override
    public void dispose () {
        batch.dispose();
        textureSheet.dispose();
    }
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, Gdx.graphics.getWidth() * (1/16f) * (1/zoom), Gdx.graphics.getHeight() * (1/16f) * (1/zoom));
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }
    @Override
    public void pause() {

    }
    @Override
    public void resume() {

    }
    @Override
    public void hide() {

    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
}
