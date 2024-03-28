package com.mygdx.game;

import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.annotation.ConsoleDoc;

public class ConsoleCommands extends CommandExecutor {
    GameItself gameItself;
    public ConsoleCommands(GameItself gameItself){
        super();
        this.gameItself = gameItself;
    }
    @ConsoleDoc(description = "Spawns mobs.") public final void spawnMobs () {
        gameItself.tester();
    }

    @ConsoleDoc(description = "Spawns certain mob.") public final void spawnMob (String name, float x, float y) {
        MobsFactory.spawnEntity("zombie", x, y);
    }
}
