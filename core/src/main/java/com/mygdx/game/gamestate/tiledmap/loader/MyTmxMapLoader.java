package com.mygdx.game.gamestate.tiledmap.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.mygdx.game.*;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.factories.TileResolver;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
import com.mygdx.game.gamestate.objects.bodies.userdata.SimpleUserData;
import com.mygdx.game.gamestate.tiledmap.Door;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class MyTmxMapLoader extends TmxMapLoader {
    GameState gameState;
    public MyTmxMapLoader(GameState gameState){
        super();
        this.gameState = gameState;
    }

    @Override
    public MyTiledMap load (String fileName, TmxMapLoader.Parameters parameter) {
        FileHandle tmxFile = resolve(fileName);

        this.root = xml.parse(tmxFile);

        ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();

        final Array<FileHandle> textureFiles = getDependencyFileHandles(tmxFile);
        for (FileHandle textureFile : textureFiles) {
            Texture texture = new Texture(textureFile, parameter.generateMipMaps);
            texture.setFilter(parameter.textureMinFilter, parameter.textureMagFilter);
            textures.put(textureFile.path(), texture);
        }

        MyTiledMap map = loadTiledMap(tmxFile, parameter, new ImageResolver.DirectImageResolver(textures));
        map.setOwnedResources(textures.values().toArray());

        return map;
    }

    @Override
    protected MyTiledMap loadTiledMap (FileHandle tmxFile, TmxMapLoader.Parameters parameter, ImageResolver imageResolver) {
        this.map = new MyTiledMap(gameState);
        this.idToObject = new IntMap<>();
        this.runOnEndOfLoadTiled = new Array<>();

        if (parameter != null) {
            this.convertObjectToTileSpace = parameter.convertObjectToTileSpace;
            this.flipY = parameter.flipY;
        } else {
            this.convertObjectToTileSpace = false;
            this.flipY = true;
        }

        String mapOrientation = root.getAttribute("orientation", null);
        int mapWidth = root.getIntAttribute("width", 0);
        int mapHeight = root.getIntAttribute("height", 0);
        int tileWidth = root.getIntAttribute("tilewidth", 0);
        int tileHeight = root.getIntAttribute("tileheight", 0);
        int hexSideLength = root.getIntAttribute("hexsidelength", 0);
        String staggerAxis = root.getAttribute("staggeraxis", null);
        String staggerIndex = root.getAttribute("staggerindex", null);
        String mapBackgroundColor = root.getAttribute("backgroundcolor", null);

        MapProperties mapProperties = map.getProperties();
        if (mapOrientation != null) {
            mapProperties.put("orientation", mapOrientation);
        }
        mapProperties.put("width", mapWidth);
        mapProperties.put("height", mapHeight);
        mapProperties.put("tilewidth", tileWidth);
        mapProperties.put("tileheight", tileHeight);
        mapProperties.put("hexsidelength", hexSideLength);
        if (staggerAxis != null) {
            mapProperties.put("staggeraxis", staggerAxis);
        }
        if (staggerIndex != null) {
            mapProperties.put("staggerindex", staggerIndex);
        }
        if (mapBackgroundColor != null) {
            mapProperties.put("backgroundcolor", mapBackgroundColor);
        }
        this.mapTileWidth = tileWidth;
        this.mapTileHeight = tileHeight;
        this.mapWidthInPixels = mapWidth * tileWidth;
        this.mapHeightInPixels = mapHeight * tileHeight;

        if (mapOrientation != null) {
            if ("staggered".equals(mapOrientation)) {
                if (mapHeight > 1) {
                    this.mapWidthInPixels += tileWidth / 2;
                    this.mapHeightInPixels = mapHeightInPixels / 2 + tileHeight / 2;
                }
            }
        }

        XmlReader.Element properties = root.getChildByName("properties");
        if (properties != null) {
            loadProperties(map.getProperties(), properties);
        }

        Array<XmlReader.Element> tilesets = root.getChildrenByName("tileset");
        for (XmlReader.Element element : tilesets) {
            loadTileSet(element, tmxFile, imageResolver);
            root.removeChild(element);
        }

        for (int i = 0, j = root.getChildCount(); i < j; i++) {
            XmlReader.Element element = root.getChild(i);
            loadLayer(map, map.getLayers(), element, tmxFile, imageResolver);
        }

        // update hierarchical parallax scrolling factors
        // in Tiled the final parallax scrolling factor of a layer is the multiplication of its factor with all its parents
        // 1) get top level groups
        final Array<MapGroupLayer> groups = map.getLayers().getByType(MapGroupLayer.class);
        while (groups.notEmpty()) {
            final MapGroupLayer group = groups.first();
            groups.removeIndex(0);

            for (MapLayer child : group.getLayers()) {
                child.setParallaxX(child.getParallaxX() * group.getParallaxX());
                child.setParallaxY(child.getParallaxY() * group.getParallaxY());
                if (child instanceof MapGroupLayer) {
                    // 2) handle any child groups
                    groups.add((MapGroupLayer)child);
                }
            }
        }

        for (Runnable runnable : runOnEndOfLoadTiled) {
            runnable.run();
        }
        runOnEndOfLoadTiled = null;

        //load tile resolver
        map.getTileSets().forEach(tiledMapTiles -> tiledMapTiles.forEach(tiledMapTile -> {
            String tileName = tiledMapTile.getProperties().get("name", String.class);
            if (tileName != null)
                TileResolver.tilemapa.put(tileName, tiledMapTile.getId());
        }));

        initPhysics();

        TileResolver.tilesets = map.getTileSets();

        return (MyTiledMap) map;
    }

    void initPhysics(){
        MyTiledMap mymap = (MyTiledMap) map;
        MapLayers mlayers = mymap.getLayers();
        var obstaclesLayer = (TiledMapTileLayer) mlayers.get("obstacles");

        for(var i = 0; i < obstaclesLayer.getWidth(); i++)
            for(var j = 0; j < obstaclesLayer.getHeight(); j++){
                var cell = obstaclesLayer.getCell(i, j);
                if (cell != null && cell.getTile().getProperties().get("type") != null){
                    var tileProperties = cell.getTile().getProperties();
                    Body body = null;
                    String bodyType = tileProperties.get("body type", null,  String.class);
                    BodyResolver.Direction direction = BodyResolver.getDirection(cell);
                    try{
                        if (bodyType != null){
                            body = BodyResolver.resolveBody(i+0.5f, j+0.5f, new SimpleUserData(cell, bodyType), BodyResolver.Type.valueOf(bodyType), direction);
                        } else {
                            switch (cell.getTile().getProperties().get("type").toString()) {
                                case "wall" -> body = BodyResolver.fullBody(i + 0.5f, j + 0.5f, new SimpleUserData(cell, "betonWall"));
                                case "fullBody" -> body = BodyResolver.fullBody(i + 0.5f, j + 0.5f, new SimpleUserData(cell, "mereFullBody"));
                                case "metalCloset" -> body = BodyResolver.resolveBody(i + 0.5f, j + 0.3f, new SimpleUserData(cell, "metalCloset"), BodyResolver.Type.METAL_CLOSET_BODY, null);
                                case "window" -> {
                                    switch (direction) {
                                        case NORTH ->
                                            body = BodyResolver.resolveBody(i + 0.5f, j + 0.95f, new SimpleUserData(cell, "northWindow"), BodyResolver.Type.WINDOW, BodyResolver.Direction.NORTH);
                                        case SOUTH ->
                                            body = BodyResolver.resolveBody(i + 0.5f, j + 0.05f, new SimpleUserData(cell, "southWindow"), BodyResolver.Type.WINDOW, BodyResolver.Direction.SOUTH);
                                        case WEST ->
                                            body = BodyResolver.resolveBody(i + 0.05f, j + 0.5f, new SimpleUserData(cell, "westWindow"), BodyResolver.Type.WINDOW, BodyResolver.Direction.WEST);
                                        case EAST ->
                                            body = BodyResolver.resolveBody(i + 0.95f, j + 0.5f, new SimpleUserData(cell, "eastWindow"), BodyResolver.Type.WINDOW, BodyResolver.Direction.EAST);
                                    }
                                }
                                case "door" -> {
                                    body = BodyResolver.resolveBody(i + 0.5f, j + 0.5f, null, BodyResolver.Type.FULL_BODY, null);
                                    Door door = new Door(cell, body, map.getTileSets().getTile(13409), map.getTileSets().getTile(13358), i, j);
                                    body.setUserData(door);
                                }
                            }
                        }
                    }catch (Exception e) {
                        SecondGDXGame.helper.log("[MyTmxMapLoader] Problem with creating tile \n Type: "
                            + cell.getTile().getProperties().get("type", "notype", String.class) + ", name "
                            + cell.getTile().getProperties().get("name", "noname", String.class) + ",  body type "
                            + bodyType + ", direction "
                            + direction + " at x: "
                            + i + ", y: "
                            + j + " \n"
                            + e.getLocalizedMessage());
                    }
                    if (body != null){
                        GameObject object = new GameObject(GameState.Instance.unbox);
                        object.setName(((BodyData)body.getUserData()).getName());
                        new Box2dBehaviour(body, object);

                        mymap.staticObjects.add(body);
                    }
                }
            }

        //world borders
        BodyDef borderBodyDef = new BodyDef();
        borderBodyDef.type = BodyDef.BodyType.StaticBody;
        Vector2 mapSize = new Vector2((int)mymap.getProperties().get("width"),(int) mymap.getProperties().get("width"));
        borderBodyDef.position.set(0,0);
        Body borderBody = mymap.world.createBody(borderBodyDef);
        EdgeShape borderShape = new EdgeShape();
        FixtureDef borderFixture = new FixtureDef();
        borderFixture.shape = borderShape;
        Vector2 bl = new Vector2(0, 0);
        Vector2 br = new Vector2(mapSize.x, 0);
        Vector2 tl = new Vector2(0, mapSize.y);
        Vector2 tr = new Vector2(mapSize.x, mapSize.y);
        borderShape.set(bl, br);
        borderBody.createFixture(borderFixture);
        borderShape.set(tl, tr);
        borderBody.createFixture(borderFixture);
        borderShape.set(bl, tl);
        borderBody.createFixture(borderFixture);
        borderShape.set(br, tr);
        borderBody.createFixture(borderFixture);

        GameObject bordersObject = new GameObject("borderBody", GameState.Instance.unbox);

        new Box2dBehaviour(borderBody, bordersObject);

    }
}
