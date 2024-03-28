package com.mygdx.game.tiledmap;

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
import com.mygdx.game.GameItself;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.UserName;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class MyTmxMapLoader extends TmxMapLoader {
    GameItself gameItself;
    public MyTmxMapLoader(GameItself gameItself){
        super();
        this.gameItself = gameItself;
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
        map.getTileSets().forEach(tiledMapTiles -> tiledMapTiles.forEach(tiledMapTile -> {
            String tileName = tiledMapTile.getProperties().get("name", String.class);
            if (tileName != null)
                gameItself.tilemapa.put(tileName, tiledMapTile.getId());
        }));

        return map;
    }

    @Override
    protected MyTiledMap loadTiledMap (FileHandle tmxFile, TmxMapLoader.Parameters parameter, ImageResolver imageResolver) {
        this.map = new MyTiledMap(gameItself);
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

        initPhysics();

        return (MyTiledMap) map;
    }

    @Override
    protected void loadTileLayer (TiledMap map, MapLayers parentLayers, XmlReader.Element element) {
        if (element.getName().equals("layer")) {
            int width = element.getIntAttribute("width", 0);
            int height = element.getIntAttribute("height", 0);
            int tileWidth = map.getProperties().get("tilewidth", Integer.class);
            int tileHeight = map.getProperties().get("tileheight", Integer.class);
            TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);

            loadBasicLayerInfo(layer, element);

            int[] ids = getTileIds(element, width, height);
            TiledMapTileSets tilesets = map.getTileSets();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int id = ids[y * width + x];
                    boolean flipHorizontally = ((id & FLAG_FLIP_HORIZONTALLY) != 0);
                    boolean flipVertically = ((id & FLAG_FLIP_VERTICALLY) != 0);
                    boolean flipDiagonally = ((id & FLAG_FLIP_DIAGONALLY) != 0);

                    TiledMapTile tile = tilesets.getTile(id & ~MASK_CLEAR);
                    if (tile != null) {
                        TiledMapTileLayer.Cell cell = createTileLayerCell(flipHorizontally, flipVertically, flipDiagonally);
                        cell.setTile(tile);
                        layer.setCell(x, flipY ? height - 1 - y : y, cell);
                    }
                }
            }
            XmlReader.Element properties = element.getChildByName("properties");
            if (properties != null) {
                loadProperties(layer.getProperties(), properties);
            }
            parentLayers.add(layer);
        }
    }

    void initPhysics(){
        MyTiledMap mymap = (MyTiledMap) map;
        MapLayers mlayers = mymap.getLayers();
        var obstaclesLayer = (TiledMapTileLayer) mlayers.get("obstacles");

        BodyTileResolver bodyResolver = new BodyTileResolver(mymap.world);

        BodyDef fullBodyDef = new BodyDef();
        PolygonShape fullBox = new PolygonShape();
        FixtureDef fullFixtureDef = new FixtureDef();
        fullBox.setAsBox(0.5f, 0.5f);
        fullFixtureDef.shape = fullBox;
        fullFixtureDef.filter.groupIndex = 0;

        BodyDef metalClosetBodyDef = new BodyDef();
        PolygonShape metalClosetBox = new PolygonShape();
        FixtureDef metalClosetFixtureDef = new FixtureDef();
        metalClosetBox.setAsBox(0.33f, 0.25f);
        metalClosetFixtureDef.shape = metalClosetBox;
        metalClosetFixtureDef.filter.groupIndex = 0;

        BodyDef windowVertBodyDef = new BodyDef();
        PolygonShape windowVertBox = new PolygonShape();
        FixtureDef windowVertFixtureDef = new FixtureDef();
        windowVertBox.setAsBox(0.05f, 0.5f);
        windowVertFixtureDef.shape = windowVertBox;
        windowVertFixtureDef.filter.groupIndex = -10;

        BodyDef windowHorBodyDef = new BodyDef();
        PolygonShape windowHorBox = new PolygonShape();
        FixtureDef windowHorFixtureDef = new FixtureDef();
        windowHorBox.setAsBox(0.5f, 0.05f);
        windowHorFixtureDef.shape = windowHorBox;
        windowHorFixtureDef.filter.groupIndex = -10;

        BodyDef transparentBodyDef = new BodyDef();
        PolygonShape transparentBox = new PolygonShape();
        FixtureDef transparentFixtureDef = new FixtureDef();
        transparentBox.setAsBox(0.5f, 0.5f);
        transparentFixtureDef.shape = transparentBox;
        transparentFixtureDef.filter.groupIndex = -10;

        for(var i = 0; i < obstaclesLayer.getWidth(); i++)
            for(var j = 0; j < obstaclesLayer.getHeight(); j++){
                var cell = obstaclesLayer.getCell(i, j);
                if (cell != null && cell.getTile().getProperties().get("type") != null){
                    var tileProperties = cell.getTile().getProperties();
                    Body body = null;
                    String bodyType = tileProperties.get("body type", null,  String.class);
                    if (bodyType != null){
                        try{
                        body = bodyResolver.resolveBody(i+0.5f, j+0.5f, new SimpleUserData(cell, bodyType), BodyTileResolver.Type.valueOf(bodyType));
                        }catch (Exception e) {
                            SecondGDXGame.helper.log("[MyTmxMapLoader] Wrong body type when reading \"body type\" property of the tile "
                                + cell.getTile().getProperties().get("type", "notype", String.class) + " "
                                + cell.getTile().getProperties().get("name", "noname", String.class) + " "
                                + bodyType + " at "
                                + i + " "
                                + j + " ");
                        }
                    } else {
                        switch (cell.getTile().getProperties().get("type").toString()) {
                            case "wall":
                                body = bodyResolver.fullBody(i + 0.5f, j + 0.5f, new SimpleUserData(cell, "betonWall"));
                                mymap.staticObjects.add(body);
                                break;
                            case "fullBody":
                                body = bodyResolver.fullBody(i + 0.5f, j + 0.5f, new SimpleUserData(cell, "mereFullBody"));
                                mymap.staticObjects.add(body);
                                break;
                            case "metalCloset":
//                                metalClosetBodyDef.position.set(new Vector2(i + 0.5f, j + 0.3f));
//                                body = mymap.world.createBody(metalClosetBodyDef);
//                                body.createFixture(metalClosetFixtureDef);
//                                body.setUserData(new SimpleUserData(cell, "metalCloset"));
                                body = bodyResolver.resolveBody(i + 0.5f, j + 0.3f, new SimpleUserData(cell, "metalCloset"), BodyTileResolver.Type.METAL_CLOSET_BODY);
                                mymap.staticObjects.add(body);
                                break;
                            case "window":
                                boolean southWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_0 && cell.getFlipVertically() && cell.getFlipVertically();
                                boolean northWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_0 && !cell.getFlipVertically() && !cell.getFlipVertically();
                                boolean eastWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_270 && !cell.getFlipVertically() && !cell.getFlipVertically();
                                boolean westWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_90 && !cell.getFlipVertically() && !cell.getFlipVertically();
                                if (northWard) {
//                                    windowHorBodyDef.position.set(new Vector2(i + 0.5f, j + 0.95f));
//                                    body = mymap.world.createBody(windowHorBodyDef);
//                                    body.createFixture(windowHorFixtureDef);
//                                    body.setUserData(new SimpleUserData(cell, "northWindow"));
                                    body = bodyResolver.resolveBody(i + 0.5f, j + 0.95f, new SimpleUserData(cell, "northWindow"), BodyTileResolver.Type.WINDOW, BodyTileResolver.Direction.NORTH);
                                    mymap.staticObjects.add(body);
                                } else if (southWard) {
//                                    windowHorBodyDef.position.set(new Vector2(i + 0.5f, j + 0.05f));
//                                    body = mymap.world.createBody(windowHorBodyDef);
//                                    body.createFixture(windowHorFixtureDef);
//                                    body.setUserData(new SimpleUserData(cell, "southWindow"));
                                    body = bodyResolver.resolveBody(i + 0.5f, j + 0.05f, new SimpleUserData(cell, "southWindow"), BodyTileResolver.Type.WINDOW, BodyTileResolver.Direction.SOUTH);
                                    mymap.staticObjects.add(body);
                                } else if (westWard) {
//                                    windowVertBodyDef.position.set(new Vector2(i + 0.05f, j + 0.5f));
//                                    body = mymap.world.createBody(windowVertBodyDef);
//                                    body.createFixture(windowVertFixtureDef);
//                                    body.setUserData(new SimpleUserData(cell, "westWindow"));
                                    body = bodyResolver.resolveBody(i + 0.05f, j + 0.5f, new SimpleUserData(cell, "westWindow"), BodyTileResolver.Type.WINDOW, BodyTileResolver.Direction.WEST);
                                    mymap.staticObjects.add(body);
                                } else if (eastWard) {
//                                    windowVertBodyDef.position.set(new Vector2(i + 0.95f, j + 0.5f));
//                                    body = mymap.world.createBody(windowVertBodyDef);
//                                    body.createFixture(windowVertFixtureDef);
//                                    body.setUserData(new SimpleUserData(cell, "eastWindow"));
                                    body = bodyResolver.resolveBody(i + 0.95f, j + 0.5f, new SimpleUserData(cell, "eastWindow"), BodyTileResolver.Type.WINDOW, BodyTileResolver.Direction.EAST);
                                    mymap.staticObjects.add(body);
                                }
                                break;
                            case "door":
//                                fullBodyDef.position.set(new Vector2(i + 0.5f, j + 0.5f));
//                                body = mymap.world.createBody(fullBodyDef);
//                                body.createFixture(fullFixtureDef);
//                                TiledMapTileSet ts = map.getTileSets().getTileSet("normal_terrain");
//                                body.setUserData(new Door(gameItself, cell, body, map.getTileSets().getTile(13409), map.getTileSets().getTile(13358), i, j));
                                body = bodyResolver.resolveBody(i + 0.5f, j + 0.5f, null, BodyTileResolver.Type.FULL_BODY);
                                body.setUserData(new Door(gameItself, cell, body, map.getTileSets().getTile(13409), map.getTileSets().getTile(13358), i, j));
                                mymap.staticObjects.add(body);
                                break;
                        }
                    }
                    if (body != null){
                        GameObject object = new GameObject(GameItself.unbox);
                        object.setName(((UserName)body.getUserData()).getName());
                        new Box2dBehaviour(body, object);
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

        GameObject bordersObject = new GameObject("borderBody", GameItself.unbox);

        new Box2dBehaviour(borderBody, bordersObject);

    }
}
