package com.mygdx.game.gamestate.player;

import com.mygdx.game.net.messages.client.PlayerMove;

public interface PlayerMoveReceiver {
    void receivePlayerUpdate(PlayerMove move);
}
