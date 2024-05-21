package com.mygdx.game.gamestate.UI.console;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.UI.console.sjconsole.*;

public class InGameConsole extends CustomConsole {
    public InGameConsole() {
        super();
    }

    public InGameConsole(Skin skin) {
        super(skin);
    }

    public InGameConsole(boolean useMultiplexer) {
        super(useMultiplexer);
    }

    public InGameConsole(Skin skin, boolean useMultiplexer) {
        super(skin, useMultiplexer);
    }

    public InGameConsole(Skin skin, boolean useMultiplexer, int keyID) {
        super(skin, useMultiplexer, keyID);
    }

    public InGameConsole(Skin skin, boolean useMultiplexer, int keyID, Class<? extends Window> windowClass, Class<? extends Table> tableClass, String tableBackground, Class<? extends TextField> textFieldClass, Class<? extends TextButton> textButtonClass, Class<? extends Label> labelClass, Class<? extends ScrollPane> scrollPaneClass) {
        super(skin, useMultiplexer, keyID, windowClass, tableClass, tableBackground, textFieldClass, textButtonClass, labelClass, scrollPaneClass);
    }



    @Override public void execCommand (String command) {
        if (disabled)
            return;

        log(command, LogLevel.COMMAND);

        String[] parts = command.split(" ");
        String methodName = parts[0];
        String[] sArgs = null;
        Array<String> tempArgs = new Array<String>();
        if (parts.length > 1) {
            for (int i = 1; i < parts.length; i++) {
                if (parts[i].equals("@p")){
                    if (i+2 >= parts.length || !(parts[i+1].startsWith("~") && parts[i+2].startsWith("~"))){
                        log("Very bad parameters. Check your code.", LogLevel.ERROR);
                        return;
                    }

                    if (parts[i+1].length() > 1){
                        tempArgs.add(String.valueOf(GameState.instance.player.getPosition().x + Float.parseFloat(parts[i+1].substring(1))));
                    } else
                        tempArgs.add(String.valueOf(GameState.instance.player.getPosition().x));

                    if (parts[i+2].length() > 1){
                        tempArgs.add(String.valueOf(GameState.instance.player.getPosition().y + Float.parseFloat(parts[i+2].substring(1))));
                    } else
                        tempArgs.add(String.valueOf(GameState.instance.player.getPosition().y));

                    i += 3;
                } else
                    tempArgs.add(parts[i]);
            }
        }

        sArgs = tempArgs.toArray(String.class);

        Class<? extends CommandExecutor> clazz = exec.getClass();
        Method[] methods = ClassReflection.getMethods(clazz);
        Array<Integer> possible = new Array<Integer>();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equalsIgnoreCase(methodName) && ConsoleUtils.canExecuteCommand(this, method)) {
                possible.add(i);
            }
        }

        if (possible.size <= 0) {
            log("No such method found.", LogLevel.ERROR);
            return;
        }

        int size = possible.size;
        int numArgs = sArgs == null ? 0 : sArgs.length;
        for (int i = 0; i < size; i++) {
            Method m = methods[possible.get(i)];
            Class<?>[] params = m.getParameterTypes();
            if (numArgs == params.length) {
                try {
                    Object[] args = null;

                    try {
                        if (sArgs != null) {
                            args = new Object[numArgs];

                            for (int j = 0, j1 = 0; j < params.length; j++, j1++) {
                                Class<?> param = params[j];
                                final String value = sArgs[j];

                                if (param.equals(String.class)) {
                                    args[j] = value;
                                } else if (param.equals(Boolean.class) || param.equals(boolean.class)) {
                                    args[j] = Boolean.parseBoolean(value);
                                } else if (param.equals(Byte.class) || param.equals(byte.class)) {
                                    args[j] = Byte.parseByte(value);
                                } else if (param.equals(Short.class) || param.equals(short.class)) {
                                    args[j] = Short.parseShort(value);
                                } else if (param.equals(Integer.class) || param.equals(int.class)) {
                                    args[j] = Integer.parseInt(value);
                                } else if (param.equals(Long.class) || param.equals(long.class)) {
                                    args[j] = Long.parseLong(value);
                                } else if (param.equals(Float.class) || param.equals(float.class)) {
                                    args[j] = Float.parseFloat(value);
                                } else if (param.equals(Double.class) || param.equals(double.class)) {
                                    args[j] = Double.parseDouble(value);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Error occurred trying to parse parameter, continue
                        // to next function
                        continue;
                    }

                    m.setAccessible(true);
                    m.invoke(exec, args);
                    return;
                } catch (ReflectionException e) {
                    String msg = e.getMessage();
                    if (msg == null || msg.length() <= 0) {
                        msg = "Unknown Error";
                        e.printStackTrace();
                    }
                    log(msg, LogLevel.ERROR);
                    if (consoleTrace) {
                        log(e, LogLevel.ERROR);
                    }
                    return;
                }
            }
        }

        log("Bad parameters. Check your code.", LogLevel.ERROR);
    }
}
