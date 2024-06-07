package com.mygdx.game.net.messages.client;

public class GetStoredItems {
    public int x, y;
    public GetStoredItems set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
