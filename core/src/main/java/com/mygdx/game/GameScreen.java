package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.jetbrains.annotations.NotNull;

public class GameScreen implements Screen {
    static GameItself gameItself;
    SecondGDXGame game;
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
        gameItself.camera.setToOrtho(false, Gdx.graphics.getWidth() * (1/16f) * (1/ gameItself.zoom), Gdx.graphics.getHeight() * (1/16f) * (1/ gameItself.zoom));
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        gameItself.hudStage.getViewport().update((int)Math.floor(Gdx.graphics.getWidth() * (1/16f) * (1/ gameItself.zoom)), (int)Math.floor(Gdx.graphics.getHeight() * (1/16f) * (1/ gameItself.zoom)), true);
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
