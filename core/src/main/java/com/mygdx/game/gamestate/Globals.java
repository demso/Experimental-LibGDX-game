package com.mygdx.game.gamestate;

import com.badlogic.gdx.physics.box2d.Filter;

public class Globals {
    public static final float DEFAULT_RENDER_ORDER = 0,
        ZOMBIE_RO = DEFAULT_RENDER_ORDER,
        PLAYER_RO = DEFAULT_RENDER_ORDER;

    public final static short
            DEFAULT_CONTACT_FILTER =            0x0001,                 //00000000 00000001
            PLAYER_CONTACT_FILTER =             0x0008,                 //00000000 00001000
            PLAYER_INTERACT_CONTACT_FILTER =    0x0002,                 //00000000 00000010
            LIGHT_CONTACT_FILTER =              Short.MIN_VALUE,        //10000000 00000000
            BULLET_CONTACT_FILTER =             0x0004,                 //00000000 00000100
            ZOMBIE_CONTACT_FILTER =             0x0010,                 //00000000 00010000
            ALL_CONTACT_FILTER =                -1,                     //11111111 11111111
            NONE_CONTACT_FILTER =               0X0000,

            PLAYER_CONTACT_GROUP =              -42,
            LIGHT_CONTACT_GROUP =               -10;

}
