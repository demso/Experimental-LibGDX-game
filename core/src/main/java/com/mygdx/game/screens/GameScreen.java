package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.mygdx.game.GameConstructor;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;

public class GameScreen implements Screen {
    public GameState gameState;
    public SecondGDXGame game;

    public GameScreen() {
        this.game = SecondGDXGame.instance; gameState = new GameConstructor().createGameState();
    }

    @Override
    public void render(float deltaTime) {
        gameState.render(deltaTime);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void resize(int width, int height) {
        gameState.hud.updateOnResize(width, height); gameState.console.refresh(false);
        gameState.gameStage.getViewport().update(width, height, false);
        gameState.camera.setToOrtho(false, width * (1f / GameState.TILE_SIDE) * (1 / gameState.zoom), height * (1f / GameState.TILE_SIDE) * (1 / gameState.zoom));
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
        InputMultiplexer multiplexer = new InputMultiplexer(); multiplexer.addProcessor(gameState.hud);
        multiplexer.addProcessor(gameState.gameStage);

        Gdx.input.setInputProcessor(multiplexer);

        gameState.console.resetInputProcessing();
    }
}
