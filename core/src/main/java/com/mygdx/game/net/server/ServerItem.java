package com.mygdx.game.net.server;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.gamestate.tiledmap.tiled.TiledMapTile;
import com.mygdx.game.net.PlayerInfo;
import dev.lyze.gdxUnBox2d.GameObject;
import lombok.Getter;

public class ServerItem {
    @Getter
    public Body physicalBody;
    protected boolean isEquipped = false;
    @Getter
    PlayerInfo owner;
    public long uid;
    public String itemId = "{No tile name}"; //string item identifier
    public String itemName = "{No name item}";
    public String description = "First you must develop a Skin that implements all the widgets you plan to use in your layout. You can't use a widget if it doesn't have a valid style. Do this how you would usually develop a Skin in Scene Composer.";
    protected GameObject GO;


}
