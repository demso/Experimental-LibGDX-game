package com.mygdx.game.net;

import com.esotericsoftware.kryonet.Connection;
import lombok.Getter;
import lombok.Setter;

public class PlayerInfo {
    @Setter @Getter
    String name;
    public float x, y, xSpeed, ySpeed;
    @Getter @Setter transient Connection connection;

    public PlayerInfo(String name, Connection con){
        this.name = name;
        connection = con;
    }

    public PlayerInfo(){}

    public PlayerInfo update(float x, float y, float xS, float yS){
        this.x = x;
        this.y = y;
        xSpeed = xS;
        ySpeed = yS;
        return this;
    }

}
