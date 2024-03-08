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
    SecondGDXGame game;
    private Player player;
    Skin skin;
    BitmapFont font;
    Batch batch;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private boolean debug = false;
    Texture textureSheet;
    Animation<TextureRegion> walkSide;
    Animation<TextureRegion> walkUp;
    Animation<TextureRegion> walkDown;
    float frameDur = 0.1f;
    private ShapeRenderer debugRenderer;
    private RayHandler rayHandler;
    private World world;
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
        world = new World(new Vector2(0, 0), true);
        loadMap();
        renderer = new OrthogonalTiledMapRenderer(map, 1f / tileSide);
        batch = renderer.getBatch();
        debugRendererPh = new Box2DDebugRenderer();
        textureSheet = new Texture(Gdx.files.internal("ClassicRPG_Sheet.png"));
        textureRegions= TextureRegion.split(textureSheet, 16, 16);
        camera = new OrthographicCamera();
        player = game.player;

        camera.setToOrtho(false, 30, 20);
        camera.update();
        renderer.setView(camera);

        game.player.WIDTH = 0.8f;
        game.player.HEIGHT = 0.8f;
        player.position.set(5,95);

        loadMap();
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

        Array<TiledMapTile> cells = new Array<>();
        for(var i = 0; i < obstaclesLayer.getWidth(); i++)
            for(var j = 0; j < obstaclesLayer.getHeight(); j++){
                var cell = obstaclesLayer.getCell(i, j);
                if (cell != null && cell.getTile().getProperties().get("type") != null){
                    var df = cell.getTile().getProperties();
                    switch (cell.getTile().getProperties().get("type").toString()){
                        case "wall":
                            fullBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                            Body fullBody = world.createBody(fullBodyDef);
                            fullBody.createFixture(fullFixtureDef);
                            fullBody.setUserData(cell);
                            staticObjects.add(fullBody);
                            break;
                        case "fullBody":
                            fullBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                            fullBody = world.createBody(fullBodyDef);
                            fullBody.createFixture(fullFixtureDef);
                            fullBody.setUserData(cell);
                            staticObjects.add(fullBody);
                            break;
                        case "metalCloset":
                            metalClosetBodyDef.position.set(new Vector2(i+0.5f, j+0.3f));
                            Body metalClosetBody = world.createBody(metalClosetBodyDef);
                            metalClosetBody.createFixture(metalClosetFixtureDef);
                            metalClosetBody.setUserData(cell);
                            staticObjects.add(metalClosetBody);
                            break;
                        case "window":
                            boolean southFloor = groundLayer.getCell(i, j + 1).getTile().getProperties().get("supercustomproperty", "", String.class).equals("floor");
                            boolean northFloor = groundLayer.getCell(i, j - 1).getTile().getProperties().get("supercustomproperty", "", String.class).equals("floor");
                            boolean eastFloor = groundLayer.getCell(i - 1, j).getTile().getProperties().get("supercustomproperty", "", String.class).equals("floor");
                            boolean westFloor = groundLayer.getCell(i + 1, j).getTile().getProperties().get("supercustomproperty", "", String.class).equals("floor");
                            if(northFloor && southFloor && westFloor && eastFloor || !northFloor && !southFloor && !westFloor && !eastFloor){
                                transparentBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                                Body transparentBody = world.createBody(transparentBodyDef);
                                transparentBody.createFixture(transparentFixtureDef);
                                transparentBody.setUserData(cell);
                                staticObjects.add(transparentBody);
                            }
                            else if(northFloor){
                                windowHorBodyDef.position.set(new Vector2(i+0.5f, j+0.95f));
                                Body windowHorBody = world.createBody(windowHorBodyDef);
                                windowHorBody.createFixture(windowHorFixtureDef);
                                windowHorBody.setUserData(cell);
                                staticObjects.add(windowHorBody);
                            }
                            else if(southFloor){
                                windowHorBodyDef.position.set(new Vector2(i+0.5f, j+0.05f));
                                Body windowHorBody = world.createBody(windowHorBodyDef);
                                windowHorBody.createFixture(windowHorFixtureDef);
                                windowHorBody.setUserData(cell);
                                staticObjects.add(windowHorBody);
                            }
                            else if(westFloor){
                                windowVertBodyDef.position.set(new Vector2(i+0.05f, j+0.5f));
                                Body windowVertBody = world.createBody(windowVertBodyDef);
                                windowVertBody.createFixture(windowVertFixtureDef);
                                windowVertBody.setUserData(cell);
                                staticObjects.add(windowVertBody);
                            }
                            else if(eastFloor){
                                windowVertBodyDef.position.set(new Vector2(i+0.95f, j+0.5f));
                                Body windowVertBody = world.createBody(windowVertBodyDef);
                                windowVertBody.createFixture(windowVertFixtureDef);
                                windowVertBody.setUserData(cell);
                                staticObjects.add(windowVertBody);
                            }
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
        f.groupIndex = -10;
        light.setContactFilter(f);
    }
    public void initPhysics(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(5, 95);
        Body body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.01f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.0f; // Make it bounce a little bit
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        circle.dispose();
        player.body = body;
        player.body.setLinearDamping(speedd);
        body.setUserData(player);
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
        //System.out.println(light.getPosition() + " " + player.body.getPosition());

        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

//        renderer.getBatch().begin();
//        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("obstacles"));
//        renderer.getBatch().end();

        updateStage();
        stage.act(deltaTime);
        stage.draw();

        stage.getBatch().begin();
        font.draw(stage.getBatch(), "FPS=" + Gdx.graphics.getFramesPerSecond(), 0, stage.getCamera().viewportHeight - 2);
        stage.getBatch().end();

        if (debug) {
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
    private void drawTileDebugInfo() {
        StringBuilder labelText = new StringBuilder("");
        labelText.append("Player velocity : ").append(player.body.getLinearVelocity()).append("\n");
        Vector3 mouse_position = new Vector3(0,0,0);
        Vector3 tilePosition = camera.unproject(mouse_position.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0));
        int tileX = (int)Math.floor(tilePosition.x);
        int tileY = (int)Math.floor(tilePosition.y);
        for (var x = 0; x < map.getLayers().size(); x++){
            TiledMapTileLayer currentLayer = (TiledMapTileLayer)map.getLayers().get(x);
            TiledMapTileLayer.Cell mcell = currentLayer.getCell(tileX, tileY);

            if(mcell != null){
                labelText.append("Layer : ").append(currentLayer.getName()).append("\nX : ").append(tileX).append("\n").append("Y : ").append(tileY).append("\n").append("ID : ").append(mcell.getTile().getId()).append("\n");
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
