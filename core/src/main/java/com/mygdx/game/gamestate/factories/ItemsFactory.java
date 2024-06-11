package com.mygdx.game.gamestate.factories;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.items.Meds;
import com.mygdx.game.gamestate.objects.items.guns.AutoRifle;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.gamestate.objects.items.guns.GunMagazine;
import com.mygdx.game.gamestate.objects.items.guns.Pistol;
import com.mygdx.game.net.messages.common.ItemInfo;
import dev.lyze.gdxUnBox2d.UnBox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ItemsFactory {
    static ObjectMap<String, String> itemNames = new ObjectMap<>();
    UnBox unBox;
    BodyResolver bodyResolver;
    HUD hud;
    Stage gameStage;
    Object container;
    Method addMethod;
    Method removeMehod;

    public ItemsFactory(Object container, UnBox box, BodyResolver resolver, HUD h, Stage st) {
        unBox = box;
        bodyResolver = resolver;
        hud = h;
        gameStage = st;
        this.container = container;
        try {
            addMethod = container.getClass().getMethod("put", Object.class, Object.class);
            addMethod.setAccessible(true);
            removeMehod = container.getClass().getMethod("remove", Object.class);
            removeMehod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Item getItem(long uid, String itemId) {
        if (itemNames.isEmpty())
            init();
        if (itemId == null)
            return null;
        Item createdItem;
        switch (itemId) {
            case "deagle_44" -> {
                Pistol gun = new Pistol(uid, itemId, getNameForID(itemId));
                gun.setData(unBox, bodyResolver, hud, gameStage, this);
                createdItem = gun;
            }
            case "pistol_magazine" -> {
                GunMagazine magaz = new GunMagazine(uid, itemId, getNameForID(itemId));
                magaz.setData(unBox, bodyResolver, hud, gameStage, this);
                magaz.addGunTypes("deagle_44");
                createdItem = magaz;
            }
            case "m4" -> {
                AutoRifle gun = new AutoRifle(uid, itemId, getNameForID(itemId));
                gun.setData(unBox, bodyResolver, hud, gameStage, this);
                createdItem = gun;
            }
            case "m4_magazine" -> {
                GunMagazine magaz = new GunMagazine(uid, itemId, getNameForID(itemId));
                magaz.setData(unBox, bodyResolver, hud, gameStage, this);
                magaz.addGunTypes("m4");
                magaz.setCapacity(30);
                createdItem = magaz;
            }
            case "medkit" -> {
                Meds med = new Meds(uid, itemId, getNameForID(itemId));
                med.setData(unBox, bodyResolver, hud, gameStage, this);
                createdItem = med;
            }
            default -> {
                Item item = new Item(uid, itemId, getNameForID(itemId));
                item.setData(unBox, bodyResolver, hud, gameStage, this);
                createdItem = item;
            }
        }
        addToContainer(createdItem);
        return createdItem;
    }

    public Item getItem(ItemInfo info) {
        return getItem(info.uid, info.itemId);
    }

    public void onItemDispose(Item item){
        try {
            removeMehod.invoke(container, item.uid);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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

    private void addToContainer(Item item) {
        try {
            addMethod.invoke(container, item.uid, item);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
