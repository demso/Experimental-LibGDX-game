package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.*;

class MainMenuScreen implements Screen {
    int viewportWidth = 640, viewportHeight = 480;
    private Stage stage;
    final SecondGDXGame game;
    OrthographicCamera camera;
    Skin skin;
    VerticalGroup buttonsGroup;
    TextButton playButton;
    TextButton settingsButton;
    TextButton exitButton;

    public MainMenuScreen(final SecondGDXGame gam) {
        game = gam;
        skin = new Skin(Gdx.files.internal("vis/skin/x2/uiskin.json"));
        stage = new Stage(new ScreenViewport());
        buttonsGroup = new VerticalGroup().space(30);
        buttonsGroup.setColor(Color.BLUE);
        buttonsGroup.fill();
        playButton = new TextButton("Play", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(game.gameScreen);
            }
        });
        stage.addListener(new InputListener(){
            public boolean keyUp (InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE){
                    Gdx.app.exit();
                }
                return true;
            }
        });
        settingsButton = new TextButton("Settings", skin);
        exitButton = new TextButton("Exit", skin);
        buttonsGroup.addActor(playButton);
        buttonsGroup.addActor(settingsButton);
        buttonsGroup.setPosition(230,stage.getHeight()-200);
        stage.addActor(buttonsGroup);
        stage.setDebugAll(true);
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
