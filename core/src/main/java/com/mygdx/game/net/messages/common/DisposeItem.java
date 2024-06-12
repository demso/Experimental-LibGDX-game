package com.mygdx.game.net.messages.common;

public class DisposeItem {

    public long uid;

    public DisposeItem set(long id) {
        uid = id;
        return this;
    }
}
