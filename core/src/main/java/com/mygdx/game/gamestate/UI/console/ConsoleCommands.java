package com.mygdx.game.gamestate.UI.console;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.factories.MobsFactory;
import com.mygdx.game.gamestate.UI.console.sjconsole.CommandExecutor;
import com.mygdx.game.gamestate.UI.console.sjconsole.annotation.ConsoleDoc;

import java.util.function.Consumer;

public class ConsoleCommands extends CommandExecutor {
    GameState gameState;
    public ConsoleCommands(GameState gameState){
        super();
        this.gameState = gameState;
    }

    @ConsoleDoc(description = "Spawns certain mob.", paramDescriptions = {"mob type", "x", "y"})
    public final void summon(String type, float x, float y) {
        MobsFactory.spawnEntity(MobsFactory.Type.valueOf(type.toUpperCase()), x, y);
    }

    @ConsoleDoc(description = "Get types of mob.")
    public final void mobs() {
        StringBuilder logs = new StringBuilder();

        new Array<MobsFactory.Type>(MobsFactory.Type.values()).forEach(new Consumer<MobsFactory.Type>() {
            @Override
            public void accept(MobsFactory.Type type) {
                logs.append(type.toString().toLowerCase()).append("\n");
            }
        });

        console.log(logs.toString());
    }
}
