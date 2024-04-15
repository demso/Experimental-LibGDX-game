package com.mygdx.game;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entities.MobsFactory;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.annotation.ConsoleDoc;

import java.util.function.Consumer;

public class ConsoleCommands extends CommandExecutor {
    GameState gameState;
    public ConsoleCommands(GameState gameState){
        super();
        this.gameState = gameState;
    }
    @ConsoleDoc(description = "Spawns mobs.") public final void spawnMobs () {
        gameState.tester();
    }

    @ConsoleDoc(description = "Spawns certain mob.", paramDescriptions = {"mob type", "x", "y"})
    public final void spawnMob (MobsFactory.Type type, float x, float y) {
        MobsFactory.spawnEntity(type, x, y);
    }

    @ConsoleDoc(description = "Get types of mob.") public final void mobTypes () {
        StringBuilder logs = new StringBuilder();

        new Array<MobsFactory.Type>(MobsFactory.Type.values()).forEach(new Consumer<MobsFactory.Type>() {
            @Override
            public void accept(MobsFactory.Type type) {
                logs.append(type).append("\n");
            }
        });

        console.log(logs.toString());
    }
}
