package com.mygdx.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;

public class MyTmxMapLoader extends TmxMapLoader {
    GameItself gameItself;
    MyTmxMapLoader(GameItself gameItself){
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

    void initPhysics(){
        MyTiledMap mymap = (MyTiledMap) map;
        MapLayers mlayers = mymap.getLayers();
        var obstaclesLayer = (TiledMapTileLayer) mlayers.get("obstacles");

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

        Body fullBody;

        for(var i = 0; i < obstaclesLayer.getWidth(); i++)
            for(var j = 0; j < obstaclesLayer.getHeight(); j++){
                var cell = obstaclesLayer.getCell(i, j);
                if (cell != null && cell.getTile().getProperties().get("type") != null){
                    var df = cell.getTile().getProperties();
                    switch (cell.getTile().getProperties().get("type").toString()){
                        case "wall":
                            fullBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                            fullBody = mymap.world.createBody(fullBodyDef);
                            fullBody.createFixture(fullFixtureDef);
                            fullBody.setUserData(new BodyUserData(cell, "betonWall"));
                            mymap.staticObjects.add(fullBody);
                            break;
                        case "fullBody":
                            fullBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                            fullBody = mymap.world.createBody(fullBodyDef);
                            fullBody.createFixture(fullFixtureDef);
                            fullBody.setUserData(new BodyUserData(cell, "mareFullBody"));
                            mymap.staticObjects.add(fullBody);
                            break;
                        case "metalCloset":
                            metalClosetBodyDef.position.set(new Vector2(i+0.5f, j+0.3f));
                            Body metalClosetBody = mymap.world.createBody(metalClosetBodyDef);
                            metalClosetBody.createFixture(metalClosetFixtureDef);
                            metalClosetBody.setUserData(new BodyUserData(cell, "metalCloset"));
                            mymap.staticObjects.add(metalClosetBody);
                            break;
                        case "window":
                            boolean southWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_0 && cell.getFlipVertically() && cell.getFlipVertically();
                            boolean northWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_0 && !cell.getFlipVertically() && !cell.getFlipVertically();
                            boolean eastWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_270 && !cell.getFlipVertically() && !cell.getFlipVertically();
                            boolean westWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_90 && !cell.getFlipVertically() && !cell.getFlipVertically();
                            if(northWard){
                                windowHorBodyDef.position.set(new Vector2(i+0.5f, j+0.95f));
                                Body windowHorBody = mymap.world.createBody(windowHorBodyDef);
                                windowHorBody.createFixture(windowHorFixtureDef);
                                windowHorBody.setUserData(new BodyUserData(cell, "northWindow"));
                                mymap.staticObjects.add(windowHorBody);
                            }
                            else if(southWard){
                                windowHorBodyDef.position.set(new Vector2(i+0.5f, j+0.05f));
                                Body windowHorBody = mymap.world.createBody(windowHorBodyDef);
                                windowHorBody.createFixture(windowHorFixtureDef);
                                windowHorBody.setUserData(new BodyUserData(cell, "southWindow"));
                                mymap.staticObjects.add(windowHorBody);
                            }
                            else if(westWard){
                                windowVertBodyDef.position.set(new Vector2(i+0.05f, j+0.5f));
                                Body windowVertBody = mymap.world.createBody(windowVertBodyDef);
                                windowVertBody.createFixture(windowVertFixtureDef);
                                windowVertBody.setUserData(new BodyUserData(cell, "westWindow"));
                                mymap.staticObjects.add(windowVertBody);
                            }
                            else if(eastWard){
                                windowVertBodyDef.position.set(new Vector2(i+0.95f, j+0.5f));
                                Body windowVertBody = mymap.world.createBody(windowVertBodyDef);
                                windowVertBody.createFixture(windowVertFixtureDef);
                                windowVertBody.setUserData(new BodyUserData(cell, "eastWindow"));
                                mymap.staticObjects.add(windowVertBody);
                            }
                            break;
                        case "door":
                            fullBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                            fullBody = mymap.world.createBody(fullBodyDef);
                            fullBody.createFixture(fullFixtureDef);
                            fullBody.setUserData(new Door(gameItself,cell, fullBody, map.getTileSets().getTileSet("normalTerrain").getTile(160), map.getTileSets().getTileSet("normalTerrain").getTile(110), i, j));
                            mymap.staticObjects.add(fullBody);
                            break;
                    }
                }
            }
    }

}
