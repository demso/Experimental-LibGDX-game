package com.mygdx.game.net.messages.common;

public class Message {
    public String message;
    public Message set(String message) {
        this.message = message;
        return this;
    }
}
