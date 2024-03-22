package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.jetbrains.annotations.NotNull;

public class GameScreen implements Screen {
    static GameItself gameItself;
    public SecondGDXGame game;
    Stage stage;
    GameScreen(@NotNull SecondGDXGame game){
        this.game = game;
        gameItself = new GameItself(this);
        this.stage = gameItself.hudStage;
    }

    @Override
    public void render (float deltaTime) {
       gameItself.render(deltaTime);
    }
    @Override
    public void dispose () {
    }
    @Override
    public void resize(int width, int height) {
       stage.getViewport().update(width, height, true);
       gameItself.gameStage.getViewport().update(width , height, false);
       gameItself.camera.setToOrtho(false, width * (1f/GameItself.tileSide) * (1/ gameItself.zoom), height * (1f/GameItself.tileSide) * (1/ gameItself.zoom));
    }
    @Override
    public void pause() {

    }
    @Override
    public void resume() {

    }
    @Override
    public void hide() {

    }
    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        multiplexer.addProcessor(gameItself.gameStage);
        multiplexer.addProcessor(stage);

        Gdx.input.setInputProcessor(multiplexer);
    }
}
