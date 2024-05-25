package com.mygdx.game.gamestate.factories;

import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;

public class ItemsFactory {
    static ObjectMap<String, String> itemNames = new ObjectMap<>();

    public static Item getItem(String itemId) {
        if (itemNames.isEmpty())
            init();
        switch (itemId) {
            case "deagle_44" -> {
                Gun gun = new Gun(TileResolver.getTile(itemId), itemNames.get(itemId));
                return gun;
            }
            default -> {
                return new Item(TileResolver.getTile(itemId), itemNames.get(itemId));
            }
        }
    }

    private static void init(){
        itemNames.put("deagle_44", "Deagle .44");
        itemNames.put("10mm_fmj", "10mm FMJ ammo");
        itemNames.put("beef", "Beef");
        itemNames.put("watches", "Watches");
        itemNames.put("shotgun_ammo", "Shotgun ammo");
    }
}
