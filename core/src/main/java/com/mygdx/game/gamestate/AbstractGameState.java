package com.mygdx.game.gamestate;

import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.gamestate.player.Player;

public interface AbstractGameState {
    ObjectMap<Long, Player> getPlayers();
}
