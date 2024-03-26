package com.mygdx.game.tiledmap;

public class SimpleUserData implements UserData {
    Object data;
    public String bodyName;
    public SimpleUserData(Object d, String n){
        data = d;
        setName(n);
    }

    public SimpleUserData(String n){
        setName(n);
    }

    public void setName(String name) {
        bodyName = name;
    }

    @Override
    public String getName() {
        return bodyName;
    }

    @Override
    public Object getData() {
        return data;
    }
}
