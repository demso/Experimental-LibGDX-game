package com.mygdx.game;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.MainMenuScreen;

public class SecondGDXGame extends Game {
    public static SecondGDXGame instance;
    public SpriteBatch batch;
    public static MainMenuScreen menuScreen;
    public static GameScreen gameScreen;
    public static Skin skin;
    public Skin skin1x;
    public static BitmapFont font;
    public static BitmapFont fontRoboto14;
    public static HandyHelper helper;
    @Override
    public void create() {
        instance = this;
        helper = new HandyHelper();
        batch = new SpriteBatch();
        HandyHelper.instance = helper;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Xolonium-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = generator.generateFont(parameter);
        generator.dispose();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 14;
        fontRoboto14 = generator.generateFont(parameter);

        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        skin = new Skin(Gdx.files.internal("vis/skin/x2/uiskin.json"));
        skin.add("default14font", fontRoboto14);
        skin1x = new Skin(Gdx.files.internal("vis/skin/x1/uiskin.json"));
        skin1x.add("default14font", fontRoboto14);
        Button.ButtonStyle bs = skin.get(Button.ButtonStyle.class);

        menuScreen = new MainMenuScreen(this);
        gameScreen = new GameScreen(this);

        this.setScreen(gameScreen);
    }
}
