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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.*;
import com.mygdx.game.SecondGDXGame;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class MainMenuScreen implements Screen {
    int viewportWidth = 640, viewportHeight = 480;
    private Stage stage;
    final SecondGDXGame game;
    OrthographicCamera camera;
    Skin skin;
    Skin skin1x = SecondGDXGame.instance.skin1x;
    VerticalGroup buttonsGroup;
    TextButton playButton;
    TextButton settingsButton;
    TextButton exitButton;
    TextButton connectButton;
    Table connectionDialog;
    Table errorDialog;
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

    public void showErrorDialog(String s){
        stage.getRoot().removeActor(errorDialog);
        errorDialog = new Table(skin);
        errorDialog.setBackground("connection-dialog");
        Label errorName = new Label("Problems with connection: ", skin1x);
        TextArea errorMessage = new TextArea("",skin1x);
        errorMessage.setTouchable(Touchable.disabled);
        errorMessage.setText(s);
        errorMessage.setPrefRows(10);
        errorMessage.setSize(1000, 200);
        TextButton errorCopy = new TextButton("Cope", skin1x);
        errorCopy.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(new StringSelection(errorMessage.getText()), null);
            }
        });
        TextButton errorClose = new TextButton("Close", skin1x);
        errorClose.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getRoot().removeActor(errorDialog);
            }
        });
        errorDialog.add(errorName).colspan(2).expandX().pad(5).row();
        errorDialog.add(errorMessage).colspan(2).fillX().expandX().pad(5).center().row();
        errorDialog.add(errorCopy).pad(5);
        errorDialog.add(errorClose).pad(5);
        errorDialog.pack();
        errorDialog.setWidth(500);
        errorDialog.setPosition(stage.getWidth()/2 - errorDialog.getWidth()/2, stage.getHeight()/2 - errorDialog.getHeight()/2);
        stage.addActor(errorDialog);
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
