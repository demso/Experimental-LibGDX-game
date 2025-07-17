package com.mygdx.game.gamestate;

public class Globals {
    public static final float
            DEFAULT_RENDER_ORDER =              0,
            ZOMBIE_RENDER_ORDER =               0.5f,
            PLAYER_RENDER_ORDER =               2,
            ANOTHER_PLAYER_RENDER_ORDER =       1,
            ITEMS_RENDER_ORDER =                0.4f;

    public static float
            SERVER_UPDATE_TIME =                1/30f,

            PLAYER_HEALTH =                     20f,
            ZOMBIE_HEALTH =                     8f,
            ZOMBIE_DAMAGE =                     1f;

    public final static short
            DEFAULT_CONTACT_FILTER =            0x0001,                 //00000000 00000001
            PLAYER_CONTACT_FILTER =             0x0008,                 //00000000 00001000
            PLAYER_INTERACT_CONTACT_FILTER =    0x0002,                 //00000000 00000010
            LIGHT_CONTACT_FILTER =              Short.MIN_VALUE,        //10000000 00000000
            BULLET_CONTACT_FILTER =             0x0004,                 //00000000 00000100
            ZOMBIE_CONTACT_FILTER =             0x0010,                 //00000000 00010000
            ALL_CONTACT_FILTER =                -1,                     //11111111 11111111
            NONE_CONTACT_FILTER =               0X0000,
            GRENADE_CONTACT_FILTER =            0X0020,              //00000000 0010000

            PLAYER_CONTACT_GROUP =              -42,
            LIGHT_CONTACT_GROUP =               -10;
}
