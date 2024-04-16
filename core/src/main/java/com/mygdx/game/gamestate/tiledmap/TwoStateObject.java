package com.mygdx.game.gamestate.tiledmap;

import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.bodies.player.Player;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
import com.mygdx.game.gamestate.objects.bodies.userdata.SimpleUserData;

public abstract class TwoStateObject implements Interactable, BodyData {
    public TwoStateObject() {

    }

    @Override
    public void interact(Player player) {

    }
}
