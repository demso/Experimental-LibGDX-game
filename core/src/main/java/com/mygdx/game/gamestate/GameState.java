package com.mygdx.game.gamestate;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.gamestate.player.AnotherPlayerConstructor;
import com.mygdx.game.gamestate.player.PlayerConstructor;
import com.mygdx.game.net.GameClient;
import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.mygdx.game.gamestate.tiledmap.tiled.renderers.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.UI.HUDInputListener;
import com.mygdx.game.gamestate.UI.console.InGameConsole;
import com.mygdx.game.gamestate.factories.ItemsFactory;
import com.mygdx.game.net.messages.client.PlayerMove;
import com.mygdx.game.net.messages.server.PlayerJoined;
import com.mygdx.game.net.messages.server.PlayerMoves;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.net.GameServer;
import dev.lyze.gdxUnBox2d.UnBox;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

public class GameState {
    public static GameState instance;
    public SecondGDXGame game;
    public GameScreen gameScreen;
    public Player player;
    public Skin skin;
    public BitmapFont  font;
    public Batch batch;
    public TiledMap map;
    public OrthogonalTiledMapRenderer renderer;
    public OrthographicCamera camera;
    public boolean debug = false;
    public Texture userSelection;
    public ShapeRenderer debugRenderer;
    public RayHandler rayHandler;
    public World world;
    public Array<Body> bodies;
    public Box2DDebugRenderer debugRendererPh;
    public float zoom = 2 ;
    public static final float TILE_SIDE = 32f;
    public HUD hud;
    public Stage gameStage;
    public float physicsStep = 1/75f;
    public InGameConsole console;
    public ShapeRenderer shapeRenderer;
    public UnBox unbox;
    public HUDInputListener HUDIL;
    public GameClient client;
    public ObjectMap<String, Player> players;
    public PlayerJoined playerJoined;
    public boolean playersNeedUpdate;
    public PlayerMoves moves;

    public void tester(){
        player.takeItem(ItemsFactory.getItem("10mm_fmj"));
        player.takeItem(ItemsFactory.getItem("beef"));
        player.takeItem(ItemsFactory.getItem("watches"));
        player.takeItem(ItemsFactory.getItem("shotgun_ammo"));
        player.takeItem(ItemsFactory.getItem("deagle_44"));
        player.equipItem(ItemsFactory.getItem("deagle_44"));
    }

    private void update(float deltaTime) {
        //Input Listener Update
        HUDIL.update();
        //CAMERA UPDATE
        camera.position.set(player.getPosition(), 0);
        camera.update();

        if (playersNeedUpdate)
            for (PlayerMove move : moves.moves){
                players.get(move.name).playerHandler.receivePlayerUpdate(move);
            }
    }

    public void render(float deltaTime){
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        unbox.preRender(deltaTime);

        update(deltaTime);

        renderer.setView(camera);
        renderer.render();

        batch.begin();

        unbox.render(batch);

        batch.end();

        gameStage.act(deltaTime);
        gameStage.draw();

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

        hud.act(deltaTime);
        hud.draw();

        if (debug) {
            hud.getBatch().begin();
            font.draw(hud.getBatch(), "FPS=" + Gdx.graphics.getFramesPerSecond(), 0, hud.getCamera().viewportHeight - 2);
            hud.getBatch().end();
            renderDebug();
            debugRendererPh.render(world, camera.combined);
        }

        if (console.isVisible())
            console.draw();

        if (playerJoined != null){
            playerJoined(playerJoined);
            playerJoined = null;
        }
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

    public void playerJoined(PlayerJoined plJoin){
        Player anotherPlayer = AnotherPlayerConstructor.createPlayer(plJoin.name);
        anotherPlayer.setPosition(plJoin.x, plJoin.y);
        players.put(plJoin.name, anotherPlayer);
    }

    public void playerJoined(String name, float x, float y){
        Player anotherPlayer = AnotherPlayerConstructor.createPlayer(name);
        anotherPlayer.setPosition(x, y);
        players.put(name, anotherPlayer);
    }

}



