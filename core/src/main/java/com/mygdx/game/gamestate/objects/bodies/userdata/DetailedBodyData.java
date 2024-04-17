package com.mygdx.game.gamestate.objects.bodies.userdata;

public class DetailedBodyData<T> implements BodyData{
    String prefix,
        firstName,
        secondName,
        suffix;

    T data;

    public DetailedBodyData(T data, String prefix,String firstname, String secondname, String suffix){
        this.prefix = prefix;
        this.firstName = firstname;
        this.secondName = secondname;
        this.suffix = suffix;

        this.data = data;
    }
    @Override
    public String getName() {
        return prefix + firstName + secondName +suffix;
    }

    @Override
    public T getData() {
        return data;
    }
}
