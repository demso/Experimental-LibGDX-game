package com.mygdx.game.gamestate;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.mygdx.game.gamestate.tiledmap.tiled.renderers.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.UI.HUDInputListener;
import com.mygdx.game.gamestate.UI.console.InGameConsole;
import com.mygdx.game.gamestate.factories.ItemsFactory;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.objects.bullet.Bullet;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.player.Player;
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
    public final String mapToLoad = "tiled/worldmap.tmx";
    public static final float TILE_SIDE = 32f;
    public HUD hud;
    public Stage gameStage;
    public float physicsStep = 1/144f;
    public InGameConsole console;
    public ShapeRenderer shapeRenderer;
    public UnBox unbox;
    public HUDInputListener HUDIL;

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


}



