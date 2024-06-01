package com.mygdx.game.gamestate.objects.tiles;

import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
import com.mygdx.game.gamestate.objects.bodies.userdata.SimpleUserData;
import com.mygdx.game.gamestate.tiledmap.loader.MyTiledMap;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.UnBox;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class TileInitializer {
    MyTiledMap mymap;
    BodyResolver bodyResolver;
    UnBox unBox;

    public TileInitializer(BodyResolver br, UnBox un, MyTiledMap map) {
        mymap = map;
        bodyResolver = br;
        unBox = un;
    }

    public void initTile(TiledMapTileLayer.Cell cell, int x, int y) {
        var tileProperties = cell.getTile().getProperties();
        Body body = null;
        String bodyType = null;
        BodyResolver.Direction direction = null;
        try {
            bodyType = tileProperties.get("body type", null, String.class);
            String name = tileProperties.get("name", null, String.class);

            direction = bodyResolver.getDirection(cell);

            if (bodyType != null) {
                body = bodyResolver.resolveTileBody(x, y, null, BodyResolver.Type.valueOf(bodyType), direction);
            } else {
                return;
            }
            ArrayList<String> nameEntries = new ArrayList<>(Arrays.asList(name.split("_")));

            BodyData bodyData = null;

            if (nameEntries.get(0).equals("t")) switch (nameEntries.get(1)) {
                case "door" -> {
                    Door door = new Door(cell, body);
                    if (nameEntries.contains("o")){
                        door.openTile = TileResolver.getTile(name);
                        int index = nameEntries.indexOf("o");
                        nameEntries.set(index, "c");
                        door.closedTile = TileResolver.getTile(String.join("_", nameEntries));
                        door.open();
                    }
                    else if (nameEntries.contains("c")){
                        door.closedTile = TileResolver.getTile(name);
                        int index = nameEntries.indexOf("c");
                        nameEntries.set(index, "o");
                        door.openTile = TileResolver.getTile(String.join("_", nameEntries));
                        door.close();
                    }
                    if (nameEntries.contains("boarded")) door.board();
                    if (nameEntries.contains("peep")) door.peep = true;
                    bodyData = door;
                } case "window" -> {
                    Window window = new Window(cell, body);
                    if (nameEntries.contains("o")) {
                        window.openTile = TileResolver.getTile(name);
                        int index = nameEntries.indexOf("o");
                        nameEntries.set(index, "c");
                        window.closedTile = TileResolver.getTile(String.join("_", nameEntries));
                        window.open();
                    } else if (nameEntries.contains("c")) {
                        window.closedTile = TileResolver.getTile(name);
                        int index = nameEntries.indexOf("c");
                        nameEntries.set(index, "o");
                        window.openTile = TileResolver.getTile(String.join("_", nameEntries));
                        window.close();
                        window.close();
                    }
                    bodyData = window;
                } case "closet"-> {
                    Closet closet = new Closet(cell, body);
                    bodyData = closet;
                } default -> {
                    bodyData = new SimpleUserData(cell, name);
                }
            }

            nameEntries.clear();

            body.setUserData(bodyData);
            cell.setData(bodyData);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            SecondGDXGame.instance.helper.log("[Error] [TileInitializer:96] Problem with creating tile \n Type: "
                    + cell.getTile().getProperties().get("type", null, String.class) + ", name "
                    + cell.getTile().getProperties().get("name", null, String.class) + ", body type "
                    + bodyType + ", direction " + direction + " at x: " + x + ", y: " + y + " \n Error message: "
                    + e.getLocalizedMessage() + "\n Stack trace: "
                    + sw);
            //e.printStackTrace();
        }

        if (body != null) {
            GameObject object = new GameObject(unBox);
            object.setName(((BodyData) body.getUserData()).getName());
            new Box2dBehaviour(body, object);

            mymap.staticObjects.add(body);
        }
    }
}
