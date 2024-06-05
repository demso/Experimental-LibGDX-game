package com.mygdx.game.gamestate.UI.console;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.UI.console.sjconsole.CommandExecutor;
import com.mygdx.game.gamestate.UI.console.sjconsole.annotation.ConsoleDoc;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.gamestate.objects.items.guns.GunSpriteBehaviour;

public class ConsoleCommands extends CommandExecutor {
    GameState gameState;
    public ConsoleCommands(GameState gameState){
        super();
        this.gameState = gameState;
    }

    @ConsoleDoc(description = "Spawns certain mob.", paramDescriptions = {"mob type", "x", "y"})
    public final void summon(String type, float x, float y) {
        //MobsFactory.spawnEntity(MobsFactory.Type.valueOf(type.toUpperCase()), x, y);
    }

    @ConsoleDoc(description = "Prints all types of mobs.")
    public final void mobs() {
        StringBuilder logs = new StringBuilder();

        new Array<>(Entity.Kind.values()).forEach(type -> logs.append(type.toString().toLowerCase()).append("\n"));

        console.log(logs.toString());
    }

    @ConsoleDoc(description = "Enables debug of certain part of the game.", paramDescriptions = {"what to debug", "true or false"})
    public final void debug(String type, boolean bool) {
        switch (type) {
            case "gunSprite" -> {
                GunSpriteBehaviour.debug = bool;
            }
        }
    }

    @ConsoleDoc(description = "Clears console.")
    public final void clear() {
        console.clear();
    }
}
