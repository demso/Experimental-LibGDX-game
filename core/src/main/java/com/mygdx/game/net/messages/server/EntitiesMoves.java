package com.mygdx.game.net.messages.server;

import com.mygdx.game.net.messages.client.PlayerMove;

public class EntitiesMoves {
    public PlayerMove[] pmoves;
    public ZombieMove[] zmoves;
    public EntitiesMoves setZMoves(ZombieMove... moves){
        this.zmoves = moves;
        return this;
    }
    public EntitiesMoves setPlayersMoves(PlayerMove... moves){
        this.pmoves = moves;
        return this;
    }
}
