package com.mygdx.game;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Collections;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.net.GameClient;
import com.mygdx.game.net.GameServer;
import com.mygdx.game.net.messages.server.OnConnection;
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
    public GameClient client;
    public GameServer server;
    public boolean readyToInit = false;
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

    public void createServerAndConnect(String name){
        this.name = menuScreen.nameField.getText();
        server = new GameServer();

        connectToServer("127.0.0.1", name);
        Gdx.graphics.setTitle(" SERVER");

        //setScreen(gameScreen);
    }

    public boolean connectToServer(String ip, String name){
        this.name = name;
        client = new GameClient();
        String er = client.connect(ip);
        if (er == null) {
            menuScreen.showInfoDialog("Connecting...");
            HandyHelper.instance.log("[Client] Waiting for data from server to start game");
        } else {
            menuScreen.showErrorDialog(er);
            return false;
        }
        return  true;
    }

    public void startGame(OnConnection msg){
        if (getScreen() == gameScreen)
            setScreen(menuScreen);

        gameScreen = new GameScreen();
        gameScreen.gameState = new GameConstructor().createGameState(msg);
        gameScreen.gameState.clientPlayer.setId(msg.yourPlayerInfo.id);
        gameScreen.gameState.tester();//создаем клиент

        ready();

        setScreen(gameScreen);
    }

    public void endGame(EndCause cause){
        setScreen(menuScreen);
        switch (cause) {
            case SERVER_LOST -> {
                menuScreen.showClosableInfoDialog("Connection lost.");
                clean();
            }
            case OK_END -> {
                clean();
            }
        }
    }

    public void ready(){
        client.ready(gameScreen.gameState);
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
            try {
                if (client != null) {
                    client.dispose();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            client = null;
            if (server != null)
                server.dispose();
            server = null;
        }   catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        if (endCause != null){
            endGame(endCause);
            endCause = null;
        }
        super.render();
        if (readyToInit){
            startGame(client.startMessage);
            client.startMessage = null;
            readyToInit = false;
        }
        helper.refreshLogsInConsole(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        super.dispose();
        if (server != null){
            server.dispose();
        }
        if (client != null) {
            client.dispose();
        }
    }

    public enum EndCause {
        SERVER_LOST,
        OK_END
    }
}
