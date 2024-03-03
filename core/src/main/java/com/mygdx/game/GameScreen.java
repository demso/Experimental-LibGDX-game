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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;

public class GameScreen implements Screen {
    SecondGDXGame game;
    private Player player;
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
    private float zoom = 3;
    private float speedd = 10f;

    GameScreen(@NotNull SecondGDXGame game){
        this.game = game;
        debugRenderer = new ShapeRenderer();
        Box2D.init();
        world = new World(new Vector2(0, 0), true);
        loadMap();
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 16f);
        batch = renderer.getBatch();
        debugRendererPh = new Box2DDebugRenderer();
        textureSheet = new Texture(Gdx.files.internal("ClassicRPG_Sheet.png"));
        textureRegions= TextureRegion.split(textureSheet, 16, 16);
        camera = new OrthographicCamera();
        player = game.player;

        camera.setToOrtho(false, 30, 20);
        camera.update();
        renderer.setView(camera);

        game.player.WIDTH = 1;
        game.player.HEIGHT = 1;
        player.position.set(5,5);

        loadMap();
        loadAnimations();
        initPhysics();
        initLights();
    }

    void loadMap(){
        map = new TmxMapLoader().load("tiledmap.tmx");
        MapLayers mlayers = map.getLayers();
        // var what = ((TiledMapTileMapObject) mlayers.get("shadows").getObjects().get(0)).getProperties();
        var obstaclesLayer = (TiledMapTileLayer) mlayers.get("obstacles");

        BodyDef treeBodyDef = new BodyDef();
        PolygonShape treeBox = new PolygonShape();
        FixtureDef treeFixtureDef = new FixtureDef();
        treeBox.setAsBox(0.6f, 0.4f);
        treeFixtureDef.shape = treeBox;
        treeFixtureDef.filter.groupIndex = 0;

        BodyDef stoneBodyDef = new BodyDef();
        PolygonShape stoneBox = new PolygonShape();
        FixtureDef stoneFixtureDef = new FixtureDef();
        stoneBox.setAsBox(0.4f, 0.4f);
        stoneFixtureDef.shape = stoneBox;
        stoneFixtureDef.filter.groupIndex = -10;

        Array<TiledMapTile> cells = new Array<>();
        for(var i = 0; i < obstaclesLayer.getWidth(); i++)
            for(var j = 0; j < obstaclesLayer.getHeight(); j++){
                var cell = obstaclesLayer.getCell(i, j);
                if (cell != null && cell.getTile().getProperties().get("type") != null){
                    var df = cell.getTile().getProperties();
                    switch (cell.getTile().getProperties().get("type").toString()){
                        case "tree":
                            treeBodyDef.position.set(new Vector2(i+1f, j+0.5f));
                            Body treeBody = world.createBody(treeBodyDef);
                            treeBody.createFixture(treeFixtureDef);
                            treeBody.setUserData(cell);
                            staticObjects.add(treeBody);
                            break;
                        case "stone":
                            stoneBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                            Body stoneBody = world.createBody(stoneBodyDef);
                            stoneBody.createFixture(stoneFixtureDef);
                            stoneBody.setUserData(cell);
                            staticObjects.add(stoneBody);
                            break;
                        case "fence":
                            break;
                    }
                }
            }
        treeBox.dispose();
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
        rayHandler.setAmbientLight(0.1f, 0.1f, 0.1f, 1f);
        rayHandler.setBlurNum(2);

        light = new PointLight(rayHandler, 5000, Color.GOLD, 15f, 0, 0);
        light.setSoft(true);
        light.setSoftnessLength(6f);
        light.attachToBody(player.body, 0, 0);
        light.setIgnoreAttachedBody(true);
        Filter f = new Filter();
        f.groupIndex = -10;
        light.setContactFilter(f);
    }
    public void initPhysics(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(5, 10);
        Body body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.0f; // Make it bounce a little bit
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        circle.dispose();

        player.body = body;
        player.body.setLinearDamping(12f);
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

        if (debug) {
            renderDebug();
            debugRendererPh.render(world, camera.combined);
        }
    }
    private void updatePlayer(float deltaTime) {
        if (deltaTime == 0) return;
        if (deltaTime > 0.1f) deltaTime = 0.1f;
        player.stateTime += deltaTime;

        Vector2 vel = player.body.getLinearVelocity();
        Vector2 pos = player.body.getPosition();

        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) && vel.x > -game.player.MAX_VELOCITY) {
            player.body.applyLinearImpulse(-speedd, 0, pos.x, pos.y, true);
            player.state = Player.State.Walking;
            player.facing = Player.Facing.LEFT;
        }

        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) && vel.x < game.player.MAX_VELOCITY) {
            player.body.applyLinearImpulse(speedd, 0, pos.x, pos.y, true);
            player.state = Player.State.Walking;
            player.facing = Player.Facing.RIGHT;
        }

        if ((Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) && vel.y < game.player.MAX_VELOCITY) {
            player.body.applyLinearImpulse(0, speedd, pos.x, pos.y, true);
            player.state = Player.State.Walking;
            player.facing = Player.Facing.UP;
        }

        if ((Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) && vel.y > -game.player.MAX_VELOCITY) {
            player.body.applyLinearImpulse(0, -speedd, pos.x, pos.y, true);
            player.state = Player.State.Walking;
            player.facing = Player.Facing.DOWN;
        }
        player.body.setLinearVelocity(player.body.getLinearVelocity().clamp(0,10f));
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) debug = !debug;
        if (Math.abs(player.body.getLinearVelocity().len2()) < 0.5f) {
            player.state = Player.State.Standing;
        }

        player.position.x = (player.body.getPosition().x);
        player.position.y = (player.body.getPosition().y);
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
        Gdx.input.setInputProcessor(null);
    }
}
