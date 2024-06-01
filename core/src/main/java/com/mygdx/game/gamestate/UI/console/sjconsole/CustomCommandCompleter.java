package com.mygdx.game.gamestate.UI.console.sjconsole;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.mygdx.game.gamestate.factories.MobsFactoryC;

public class CustomCommandCompleter {
    private ObjectSet<String> possibleEntry;
    private Array<String> parameters;
    private ObjectSet.ObjectSetIterator<String> iterator;
    private String setString;

    public CustomCommandCompleter () {
        possibleEntry = new ObjectSet<>();
        parameters = new Array<>();
        for (MobsFactoryC.Type type : MobsFactoryC.Type.values()){
            parameters.add(type.toString().toLowerCase());
        }
        setString = "";
    }

    public void set (CommandExecutor ce, String s, boolean isFunction) {
        reset();
        setString = s.toLowerCase();
        Array<Method> methods = getAllMethods(ce);
        if (isFunction)
            for (Method m : methods) {
                String name = m.getName();
                if (name.toLowerCase().startsWith(setString) && ConsoleUtils.canDisplayCommand(ce.console, m)) {
                    possibleEntry.add(name);
                }
            }
        else
            for (String pos : parameters){
                if (pos.toLowerCase().startsWith(setString)) {
                    possibleEntry.add(pos);
                }
            }

        iterator = new ObjectSet.ObjectSetIterator<>(possibleEntry);
    }

    public void reset () {
        possibleEntry.clear();
        setString = "";
        iterator = null;
    }

    public boolean isNew () {
        return possibleEntry.size == 0;
    }

    public boolean wasSetWith (String s) {
        return setString.equalsIgnoreCase(s);
    }

    public String next () {
        if (!iterator.hasNext) {
            iterator.reset();
            return setString;
        }
        return iterator.next();
    }

    private Array<Method> getAllMethods (CommandExecutor ce) {
        Array<Method> methods = new Array<>();
        Method[] ms = ClassReflection.getDeclaredMethods(ce.getClass());
        for (Method m : ms) {
            if (m.isPublic()) {
                methods.add(m);
            }
        }
        ms = ClassReflection.getDeclaredMethods(ce.getClass().getSuperclass());
        for (Method m : ms) {
            if (m.isPublic()) {
                methods.add(m);
            }
        }

        return methods;
    }
}
