package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Collections;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.MainMenuScreen;

public class SecondGDXGame extends Game {
    public static SecondGDXGame instance;
    public SpriteBatch batch;
    public static MainMenuScreen menuScreen;
    public GameScreen gameScreen;
    public static Skin skin;
    public Skin skin1x;
    public static BitmapFont font;
    public static BitmapFont fontRoboto14;
    public static BitmapFont fontRoboto18;
    public BitmapFont fontRoboto12;
    public HandyHelper helper;
    public boolean gameIsReady = false;
    public String name;
    public EndCause endCause;

    @Override
    public void create() {
        instance = this;
        helper = new HandyHelper();
        batch = new SpriteBatch();
        Collections.allocateIterators = true;
        HandyHelper.instance = helper;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Xolonium-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);

        generator.dispose();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 14;
        //parameter.hinting = FreeTypeFontGenerator.Hinting.None;
//        parameter.minFilter = Texture.TextureFilter.Linear;
//        parameter.magFilter = Texture.TextureFilter.Linear;
        fontRoboto14 = generator.generateFont(parameter);
        fontRoboto14.setUseIntegerPositions(false);

        generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        parameter.hinting = FreeTypeFontGenerator.Hinting.Slight;
        fontRoboto12 = generator.generateFont(parameter);
        fontRoboto12.setUseIntegerPositions(false);
        //fontRoboto12.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        parameter.hinting = FreeTypeFontGenerator.Hinting.None;
        fontRoboto12 = generator.generateFont(parameter);
        fontRoboto12.setUseIntegerPositions(false);

        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        skin = new Skin(Gdx.files.internal("vis/skin/x2/uiskin.json"));
        skin.add("default14font", fontRoboto14);
        skin.add("default12font", fontRoboto12);
        skin1x = new Skin(Gdx.files.internal("vis/skin/x1/uiskin.json"));
        skin1x.add("default12font", fontRoboto12);
        Button.ButtonStyle bs = skin.get(Button.ButtonStyle.class);

        menuScreen = new MainMenuScreen(this);

        this.setScreen(menuScreen);
    }

    public void createServerAndConnect(){
        this.name = menuScreen.nameField.getText();
        startGame();
        //setScreen(gameScreen);
    }

    public boolean connectToServer(String ip, String name){
        menuScreen.showErrorDialog("Not available now");
        return  false;
    }

    public void startGame(){
        if (getScreen() == gameScreen)
            setScreen(menuScreen);

        gameScreen = new GameScreen();

        ready();

        setScreen(gameScreen);
    }

    public void endGame(EndCause cause){
        setScreen(menuScreen);
        clean();
        gameIsReady = false;
    }

    public void ready(){
        gameIsReady = true;
    }

    private void clean() {
        try {
            try {
                if (gameScreen != null)
                    gameScreen.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
            gameScreen = null;
            name = null;
            GameState.instance = null;
        }   catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        endGame(EndCause.SERVER_LOST);
        super.dispose();
    }

    public enum EndCause {
        SERVER_LOST,
        OK_END
    }
}
