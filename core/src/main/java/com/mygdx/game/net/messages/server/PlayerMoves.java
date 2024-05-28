package com.mygdx.game.net.messages.server;

import com.mygdx.game.net.messages.client.PlayerMove;

public class PlayerMoves {
    public PlayerMove[] moves;
    public PlayerMoves setMoves(PlayerMove... moves){
        this.moves = moves;
        return this;
    }
}
