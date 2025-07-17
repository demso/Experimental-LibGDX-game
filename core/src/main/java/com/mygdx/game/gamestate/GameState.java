package com.mygdx.game.gamestate;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.Utils;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.UI.HUDInputListener;
import com.mygdx.game.gamestate.UI.console.InGameConsole;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.factories.ItemsFactory;
import com.mygdx.game.gamestate.factories.MobsFactory;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.bodies.mobs.zombie.Zombie;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.gamestate.tiledmap.loader.MyTiledMap;
import com.mygdx.game.gamestate.tiledmap.tiled.TiledMapTileLayer;
import com.mygdx.game.gamestate.tiledmap.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.screens.GameScreen;
import dev.lyze.gdxUnBox2d.UnBox;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameState extends AbstractGameState {
    public static GameState instance;
    public SecondGDXGame game;
    public GameScreen gameScreen;
    public Player clientPlayer;
    public Skin skin;
    public BitmapFont font;
    public Batch batch;
    public MyTiledMap map;
    public String mapToLoad = "tiled/firstmap/worldmap.tmx";
    public OrthogonalTiledMapRenderer renderer;
    public OrthographicCamera camera;
    public boolean debug = false;
    public Texture userSelection;
    public Texture bulletTracer;
    public ShapeRenderer debugRenderer;
    public RayHandler rayHandler;
    public World world;
    public Array<Body> bodies;
    public Box2DDebugRenderer debugRendererPh;
    public float zoom = 2;
    public static final float TILE_SIDE = 32f;
    public HUD hud;
    public Stage gameStage;
    public GameStageInputListener GSIL;
    public float physicsStep = 1 / 75f;
    public InGameConsole console;
    public ShapeRenderer shapeRenderer;
    public ShapeDrawer shapeDrawer;
    public UnBox unbox;
    public HUDInputListener HUDIL;
    public ObjectMap<Long, Player> players;
    volatile public ObjectMap<Long, Entity> entities;
    public volatile ObjectMap<Long, Item> items;//items on floor and in player inventory
    public BodyResolver bodyResolver;
    public MobsFactory mobsFactory;
    public ItemsFactory itemsFactory;
    public Array<Vector2> rayEnds = new Array<>();
    public Vector2 grPos = new Vector2();
    public int itemsCounter = 1;
    public int entitiesCounter = 1;

    public void tester() {
//        clientPlayer.takeItem(ItemsFactory.getItem("10mm_fmj"));
//        clientPlayer.takeItem(ItemsFactory.getItem("beef"));
//        clientPlayer.takeItem(ItemsFactory.getItem("watches"));
//        clientPlayer.takeItem(ItemsFactory.getItem("shotgun_ammo"));
//        clientPlayer.takeItem(ItemsFactory.getItem("deagle_44"));
//        clientPlayer.equipItem(ItemsFactory.getItem("deagle_44"));
    }

    private void update(float deltaTime) {
        unbox.preRender(deltaTime);
        //Input Listener Update
        HUDIL.update();
        GSIL.update(deltaTime);
        //CAMERA UPDATE
        camera.position.set(clientPlayer.getPosition(), 0);
        camera.update();
        spawner();

        HandyHelper.instance.refreshLogsInConsole(Gdx.graphics.getDeltaTime());
    }

<<<<<<< HEAD
    public void render(float deltaTime){
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        synchronized (world) {
            unbox.preRender(deltaTime);
        }
=======
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
>>>>>>> single

        update(deltaTime);

        renderer.setView(camera);
        renderer.render();

        batch.begin();
        unbox.render(batch);
        batch.end();

        gameStage.act(deltaTime);
        gameStage.draw();

        rayHandler.setCombinedMatrix(camera); rayHandler.updateAndRender();

        if (clientPlayer.getClosestObject() != null) {
            float w = Box2DUtils.width(clientPlayer.getClosestObject());
            float h = Box2DUtils.height(clientPlayer.getClosestObject()); batch.begin();
            batch.draw(userSelection, clientPlayer.getClosestObject().getPosition().x - w / 2f, clientPlayer.getClosestObject().getPosition().y - h / 2f, w, h);
            batch.end();
        }

        unbox.postRender();

        hud.act(deltaTime);
        hud.draw();

        if (debug) {
            hud.getBatch().begin();
            font.draw(hud.getBatch(), "FPS=" + Gdx.graphics.getFramesPerSecond(), 0, hud.getCamera().viewportHeight - 2);
            hud.getBatch().end();
            renderDebug();
            debugRendererPh.render(world, camera.combined);
        }

        if (console.isVisible()) console.draw();
    }

    Vector2 spawnCenter = new Vector2(10, 85);
    float spawnRadius = 5f;
    long spawnPeriod = 25000;
    Vector2 zombieSpawnPoint = new Vector2(1, 1).nor().scl(spawnRadius);
    int wave = 0;
    long lastSpawnTime = 0;
    int entitiesLimit = 50;
    public Rectangle worldBorders;

    long waveDuration = 300 * 1000;

    long waveStartTime;

    void newWave() {
        wave += 1;
        waveStartTime = System.currentTimeMillis();
    }

    void spawner(){
        if ((System.currentTimeMillis() - lastSpawnTime > spawnPeriod && entities.size < entitiesLimit)){

            if (waveDuration < System.currentTimeMillis() - waveStartTime){
                newWave();
                HandyHelper.instance.log("[GameState] New wave: " + wave);
            }

            Vector2 pos = new Vector2();
            float minX = worldBorders.x + 3, maxX = worldBorders.x + worldBorders.width, minY = worldBorders.y + 3, maxY = worldBorders.y + worldBorders.height;

            float amountMultiplier = 1;
            int packSizeMultiplier = 7;
            int howMuchSpawns = Math.round(amountMultiplier) * wave; //pack of zombies counter per spawn

            for (int i = 0; i < howMuchSpawns; i++) {
                do {
                    float random1 = (float) Math.random() * (worldBorders.width - 6), random2 = (float) Math.random() * (worldBorders.height - 6);
                    pos.set(minX, minY).add(random1, random2);
                } while (((TiledMapTileLayer) map.getLayers().get("obstacles")).getCell((int) pos.x, (int) pos.y) != null);


                long pack = Math.round( Math.max(Math.random() * packSizeMultiplier * Math.cbrt(wave), 1));
                float spawnRadius = (float) Math.sqrt(pack) + 0.5f;

                for (int j = 0; j < pack; j++) {
                    float spawnX = pos.x + (float) Math.random() * spawnRadius,
                            spawnY = pos.y + (float) Math.random() * spawnRadius;
                    if (((TiledMapTileLayer) map.getLayers().get("obstacles")).getCell(Math.round(spawnX), Math.round(spawnY)) != null)
                        continue;
                    spawnZombie(spawnX, spawnY);

                }
                HandyHelper.instance.log("[GameState:spawner] Spawning zombie pack at: " + Utils.round(pos.x, 1) + ", " + Utils.round(pos.y, 1) + " with size: " + pack);
            }

            lastSpawnTime = System.currentTimeMillis();
        }
    }

    void spawnZombie(float x, float y){
        Zombie zomb = (Zombie) mobsFactory.spawnEntity(entitiesCounter, Entity.Kind.ZOMBIE, x, y);
        zomb.setName("zombie" + entitiesCounter);
        zomb.setHp(10);
        entities.put((long)entitiesCounter, zomb);
        entitiesCounter += 1;
    }

    Vector2 beginV;
    Vector2 endV;

    private void renderDebug() {
        if (beginV != null && endV != null) {
            debugRenderer.setProjectionMatrix(camera.combined); debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            debugRenderer.setColor(Color.RED); debugRenderer.line(beginV, endV); debugRenderer.setColor(Color.YELLOW);
            debugRenderer.end();
        }
        //        if (rayEnds != null && rayEnds.size > 0){
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//            shapeRenderer.setProjectionMatrix(camera.combined);
//            shapeRenderer.setColor(Color.GOLDENROD);
//            for(Vector2 vec2 : rayEnds){
//                shapeRenderer.line(new Vector2(grPos), new Vector2(grPos).add(vec2));
//            }
//            shapeRenderer.end();
//        }
    }

}



