package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.*;
import com.mygdx.game.SecondGDXGame;

public class MainMenuScreen implements Screen {
    int viewportWidth = 640, viewportHeight = 480;
    private Stage stage;
    final SecondGDXGame game;
    OrthographicCamera camera;
    Skin skin;
    VerticalGroup buttonsGroup;
    TextButton playButton;
    TextButton settingsButton;
    TextButton exitButton;
    TextButton connectButton;
    Table connectionDialog;
    String ip = "127.0.0.1";

    public MainMenuScreen(final SecondGDXGame gam) {
        game = gam;
        skin = game.skin;
        stage = new Stage(new ScreenViewport(), game.batch);
        buttonsGroup = new VerticalGroup().space(30);
        buttonsGroup.setColor(Color.BLUE);
        buttonsGroup.fill();
        playButton = new TextButton("Create server", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.createServerAndConnect();
            }
        });
        stage.addListener(new InputListener(){
            public boolean keyUp (InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE){
                    Gdx.app.exit();
                    return true;
                }
                if (keycode == Input.Keys.SPACE){
                    game.setScreen(game.gameScreen);
                    return true;
                }
                if (keycode == Input.Keys.B){
                    stage.setDebugAll(!stage.isDebugAll());
                }
                return true;
            }
        });
        settingsButton = new TextButton("Settings", skin);
        connectButton = new TextButton("Connect", skin);
        connectButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getRoot().removeActor(connectionDialog, true);
                stage.addActor(connectionDialog);
                connectionDialog.setPosition(stage.getWidth()/2 - connectionDialog.getWidth()/2, stage.getHeight()/2 - connectionDialog.getHeight()/2);
            }
        });
        exitButton = new TextButton("Exit", skin);
        buttonsGroup.addActor(playButton);
        buttonsGroup.addActor(connectButton);
        buttonsGroup.addActor(settingsButton);
        buttonsGroup.setPosition(230,stage.getHeight()-200);
        stage.addActor(buttonsGroup);

        connectionDialog = new Table(skin);
        //connectionDialog.debugAll();
        connectionDialog.remove();
        connectionDialog.setBackground("connection-dialog");
        TextField ipField = new TextField("127.0.0.1", skin);
        TextButton connectDialogBut = new TextButton("Connect", skin);
        connectDialogBut.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.connectToServer(ipField.getText());
            }
        });
        TextButton cancelDialogBut = new TextButton("Cancel", skin);
        cancelDialogBut.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getRoot().removeActor(connectionDialog, true);
            }
        });

        connectionDialog.clear();
        connectionDialog.add(ipField).colspan(2).expandX().pad(5).center().row();
        connectionDialog.add(connectDialogBut).pad(5);
        connectionDialog.add(cancelDialogBut).pad(5);
        connectionDialog.pack();
    }

    public String getIp(){
        return ip;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

//        if (Gdx.input.isTouched()) {
//            game.setScreen(new GameScreen(game));
//            dispose();
//        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
    @Override
    public void hide() {
    }
    @Override
    public void pause() {
    }
    @Override
    public void resume() {
    }
    @Override
    public void dispose() {
    }
}
