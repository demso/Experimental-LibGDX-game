package com.mygdx.game.gamestate.factories;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;
import com.mygdx.game.net.messages.common.ItemInfo;
import dev.lyze.gdxUnBox2d.UnBox;

public class ItemsFactory {
    static ObjectMap<String, String> itemNames = new ObjectMap<>();
    UnBox unBox;
    BodyResolver bodyResolver;
    HUD hud;
    Stage gameStage;

    public ItemsFactory(UnBox box, BodyResolver resolver, HUD h, Stage st) {
        unBox = box;
        bodyResolver = resolver;
        hud = h;
        gameStage = st;
    }

    public Item getItem(long uid, String itemId) {
        if (itemNames.isEmpty())
            init();
        if (itemId == null)
            return null;
        switch (itemId) {
            case "deagle_44" -> {
                Gun gun = new Gun(uid, itemId, getNameForID(itemId));
                gun.setData(unBox, bodyResolver, hud, gameStage);
                return gun;
            }
            default -> {
                Item item = new Item(uid, itemId, getNameForID(itemId));
                item.setData(unBox, bodyResolver, hud, gameStage);
                return item;
            }
        }
    }

    public Item getItem(ItemInfo info) {
        return getItem(info.uid, info.itemId);
    }

    private static String getNameForID(String id){
        return itemNames.get(id, id);
    }

    private static void init(){
        itemNames.put("deagle_44", "Deagle .44");
        itemNames.put("10mm_fmj", "10mm FMJ ammo");
        itemNames.put("beef", "Beef");
        itemNames.put("watches", "Watches");
        itemNames.put("shotgun_ammo", "Shotgun ammo");
    }
}
