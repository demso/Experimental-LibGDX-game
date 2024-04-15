package com.mygdx.game;

public class Globals {
    public static final float DEFAULT_RO = 0;
    public static final float ZOMBIE_RO = DEFAULT_RO;
    public static final float PLAYER_RO = DEFAULT_RO;

    public final static short
            DEFAULT_CF =            0x0001,                 //00000000 00000001
            PLAYER_CF =             0x0008,                 //00000000 00001000
            PLAYER_INTERACT_CF =    0x0002,                 //00000000 00000010
            LIGHT_CF =              Short.MIN_VALUE,        //10000000 00000000
            BULLET_CF =             0x0004,                 //00000000 00000100
            ZOMBIE_CF =             0x0010,                 //00000000 00010000
            ALL_CF =                -1,                     //11111111 11111111

    PLAYER_CG =             -42;
}
