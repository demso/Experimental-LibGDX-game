package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.GameConstructor;
import com.mygdx.game.gamestate.GameState;
import org.jetbrains.annotations.NotNull;

public class GameScreen implements Screen {
    static GameState gameState;
    public SecondGDXGame game;
    HUD hudStage;
    public GameScreen(@NotNull SecondGDXGame game){
        this.game = game;
        SecondGDXGame.menuScreen.getIp();
        gameState = new GameConstructor().createGameState(this);
        GameState.instance = gameState;
        this.hudStage = gameState.hud;
    }

    @Override
    public void render (float deltaTime) {
       gameState.render(deltaTime);
    }
    @Override
    public void dispose () {
    }
    @Override
    public void resize(int width, int height) {
       hudStage.updateOnResize(width, height);
       gameState.console.refresh(false);
       gameState.gameStage.getViewport().update(width , height, false);
       gameState.camera.setToOrtho(false, width * (1f/ GameState.TILE_SIDE) * (1/ gameState.zoom), height * (1f/ GameState.TILE_SIDE) * (1/ gameState.zoom));
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
        multiplexer.addProcessor(hudStage);
        multiplexer.addProcessor(gameState.gameStage);

        Gdx.input.setInputProcessor(multiplexer);

        gameState.console.resetInputProcessing();
    }
}
