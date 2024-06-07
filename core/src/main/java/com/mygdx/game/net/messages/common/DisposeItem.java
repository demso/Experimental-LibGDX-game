package com.mygdx.game.net.messages.common;
//do not send sender
public class DisposeItem {
    public long uid;

    public DisposeItem set(long id) {
        uid = id;
        return this;
    }
}
