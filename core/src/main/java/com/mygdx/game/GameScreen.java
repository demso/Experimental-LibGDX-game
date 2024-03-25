package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.mygdx.game.UI.HUD;
import org.jetbrains.annotations.NotNull;

public class GameScreen implements Screen {
    static GameItself gameItself;
    public SecondGDXGame game;
    HUD hudStage;
    GameScreen(@NotNull SecondGDXGame game){
        this.game = game;
        gameItself = new GameItself(this);
        this.hudStage = gameItself.hudStage;
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
       hudStage.updateOnResize(width, height);
       gameItself.gameStage.getViewport().update(width , height, false);
       gameItself.camera.setToOrtho(false, width * (1f/GameItself.TILE_SIDE) * (1/ gameItself.zoom), height * (1f/GameItself.TILE_SIDE) * (1/ gameItself.zoom));
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
        multiplexer.addProcessor(hudStage);
        multiplexer.addProcessor(gameItself.gameStage);


        Gdx.input.setInputProcessor(multiplexer);
    }
}
