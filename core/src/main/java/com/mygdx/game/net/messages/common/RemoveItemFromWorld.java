package com.mygdx.game.net.messages.common;
//do not send sender
public class RemoveItemFromWorld {
    public long uid;

    public RemoveItemFromWorld set(long id) {
        uid = id;
        return this;
    }
}
