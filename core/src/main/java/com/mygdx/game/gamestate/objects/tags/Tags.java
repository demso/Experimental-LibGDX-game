package com.mygdx.game.gamestate.objects.tags;


import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Tags {
    ObjectMap<String, Array> tags = new ObjectMap<>();

//    public void addTag(String tag, String value){
//        Array<String> array = tags.get(tag);
//        if (array == null) {
//            array = new Array<>();
//            array.add(value);
//            tags.put(tag, array);
//        } else {
//            array.add(value);
//        }
//    }
//
//    public Array<String> removeTag(String tag){
//        return tags.remove(tag);
//    }
//
//    public boolean removeValue(String tag, String... values){
//        return true;
//    }
//
//    public String[] getValues(String tag){
//        return tags.get(tag).items;
//    }
}
