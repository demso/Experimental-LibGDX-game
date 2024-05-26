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

import java.util.ArrayList;
import java.util.Arrays;

public class TileInitializer {
    MyTiledMap mymap;

    public TileInitializer(MyTiledMap map) {
        mymap = map;
    }

    public void initTile(TiledMapTileLayer.Cell cell, int x, int y) {
        var tileProperties = cell.getTile().getProperties();
        Body body = null;
        String bodyType = null;
        BodyResolver.Direction direction = null;
        try {
            bodyType = tileProperties.get("body type", null, String.class);
            String name = tileProperties.get("name", null, String.class);

            direction = BodyResolver.getDirection(cell);

            if (bodyType != null) {
                body = BodyResolver.resolveTileBody(x, y, null, BodyResolver.Type.valueOf(bodyType), direction);
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
            SecondGDXGame.helper.log("[Error] [TileInitializer] Problem with creating tile \n Type: " + cell.getTile().getProperties().get("type", null, String.class) + ", name " + cell.getTile().getProperties().get("name", null, String.class) + ",  body type " + bodyType + ", direction " + direction + " at x: " + x + ", y: " + y + " \n " + e.getLocalizedMessage());
        }

        if (body != null) {
            GameObject object = new GameObject(GameState.instance.unbox);
            object.setName(((BodyData) body.getUserData()).getName());
            new Box2dBehaviour(body, object);

            mymap.staticObjects.add(body);
        }
    }
}
