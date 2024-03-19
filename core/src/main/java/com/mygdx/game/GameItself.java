package com.mygdx.game;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class GameItself {
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
    World world;
    private Array<Body> bodies;
    private float accumulator = 0;
    Box2DDebugRenderer debugRendererPh;
    PointLight light;

    TextureRegion textureRegions[][];
    private float zoom = 4 ;
    private float speedd = 5f;
    private final String mapToLoad = "newmap.tmx";
    private final int tileSide = 32;

    Stage stage;
    Label label;

    GameItself(SecondGDXGame g){
        game = g;
    }
}
