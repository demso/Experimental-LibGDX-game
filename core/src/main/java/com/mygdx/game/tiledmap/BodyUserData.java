package com.mygdx.game.tiledmap;

public class BodyUserData implements BodyUserName{
    Object data;
    public String bodyName;
    public BodyUserData(Object d, String n){
        data = d;
        setName(n);
    }

    public BodyUserData(String n){
        setName(n);
    }

    public void setName(String name) {
        bodyName = name;
    }

    @Override
    public String getName() {
        return bodyName;
    }
}
