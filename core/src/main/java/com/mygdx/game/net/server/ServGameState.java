package com.mygdx.game.net.server;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.UI.HUDInputListener;
import com.mygdx.game.gamestate.UI.console.InGameConsole;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.gamestate.tiledmap.loader.MyTiledMap;
import com.mygdx.game.gamestate.tiledmap.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.net.GameServer;
import dev.lyze.gdxUnBox2d.UnBox;

public class ServGameState {
    public static ServGameState instance;
    public SecondGDXGame game;
    //public GameScreen gameScreen;
    public Skin skin;
    public BitmapFont font;
    public Batch batch;
    public MyTiledMap map;
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
    public ObjectMap<Long, Player> players;
    volatile public ObjectMap<Long, Entity> entities;
    volatile public ServHandler serverHandler;
    public GameServer gameServer;
    public BodyResolver bodyResolver;
    public ServerMobsFactory mobsFactory;

    public void tester(){
    }

    public void update(float deltaTime) {
        unbox.preRender(deltaTime);
        unbox.postRender();
        //getServerHandler().update();
    }

    public ServHandler getServerHandler() {
        return serverHandler;
    }


    public BodyResolver getBodyResolver() {
        return bodyResolver;
    }
}
