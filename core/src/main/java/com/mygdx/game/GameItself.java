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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameItself {
    final static short PLAYER_CF= 0x0008,
        PLAYER_INTERACT_CF =   0x0002,
        LIGHT_CF =             0x4000,
        ALL_CF = Short.MAX_VALUE;

    SecondGDXGame game;
    GameScreen gameScreen;
    Player player;
    Skin skin;
    BitmapFont font;
    Batch batch;
    TiledMap map;
    OrthogonalTiledMapRenderer renderer;
    OrthographicCamera camera;
    boolean debug = false;
    Texture textureSheet;
    Texture userSelection;
    Animation<TextureRegion> walkSide;
    Animation<TextureRegion> walkUp;
    Animation<TextureRegion> walkDown;
    float frameDur = 0.1f;
    ShapeRenderer debugRenderer;
    RayHandler rayHandler;
    World world;
    Array<Body> bodies;
    float accumulator = 0;
    Box2DDebugRenderer debugRendererPh;
    PointLight light;

    TextureRegion textureRegions[][];
    float zoom = 4 ;
    float speedd = 5f;
    final String mapToLoad = "newmap.tmx";
    final int tileSide = 32;

    Stage stage;
    Label label;

    GameItself(GameScreen gameScreen){
        this.game = gameScreen.game;
        this.gameScreen = gameScreen;

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

        map = new MyTmxMapLoader(this).load(mapToLoad);

        renderer = new OrthogonalTiledMapRenderer(map, 1f / tileSide);
        batch = renderer.getBatch();
        debugRendererPh = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        player = game.player;

        camera.setToOrtho(false, 30, 20);
        camera.update();
        renderer.setView(camera);

        game.player.WIDTH = 0.8f;
        game.player.HEIGHT = 0.8f;
        player.position.set(5,95);

        initTextures();
        initPhysics();

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
    void render(float deltaTime){
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(deltaTime);

        camera.position.x = player.position.x;
        camera.position.y = player.position.y;

        camera.update();

        renderer.setView(camera);
        renderer.render();

        renderPlayer();

        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

        if (player.closestObject != null) {
            batch.begin();
            batch.draw(userSelection, (int)player.closestObject.getPosition().x, (int)player.closestObject.getPosition().y,1,1);
            batch.end();
        }

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
    void initTextures() {
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
    public void initPhysics(){
        //world
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
        //physicsStep
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= 1/60f) {
            world.step(1/60f, 6, 2);
            accumulator -= 1/60f;
        }

        //player
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

        //stage
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
}
