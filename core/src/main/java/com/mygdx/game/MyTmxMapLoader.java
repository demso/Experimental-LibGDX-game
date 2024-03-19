package com.mygdx.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;

public class MyTmxMapLoader extends TmxMapLoader {
    MyTmxMapLoader(){
        super();
    }

    @Override
    public TiledMap load (String fileName, TmxMapLoader.Parameters parameter) {
        FileHandle tmxFile = resolve(fileName);

        this.root = xml.parse(tmxFile);

        ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();

        final Array<FileHandle> textureFiles = getDependencyFileHandles(tmxFile);
        for (FileHandle textureFile : textureFiles) {
            Texture texture = new Texture(textureFile, parameter.generateMipMaps);
            texture.setFilter(parameter.textureMinFilter, parameter.textureMagFilter);
            textures.put(textureFile.path(), texture);
        }

        TiledMap map = loadTiledMap(tmxFile, parameter, new ImageResolver.DirectImageResolver(textures));
        map.setOwnedResources(textures.values().toArray());
        return map;
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

                        switch (cell.getTile().getProperties().get("type").toString()){
                            case "wall":
                                fullBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                                fullBody = map.world.createBody(fullBodyDef);
                                fullBody.createFixture(fullFixtureDef);
                                fullBody.setUserData(new BodyUserData(cell, "betonWall"));
                                staticObjects.add(fullBody);
                                break;
                            case "fullBody":
                                fullBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                                fullBody = world.createBody(fullBodyDef);
                                fullBody.createFixture(fullFixtureDef);
                                fullBody.setUserData(new BodyUserData(cell, "mareFullBody"));
                                staticObjects.add(fullBody);
                                break;
                            case "metalCloset":
                                metalClosetBodyDef.position.set(new Vector2(i+0.5f, j+0.3f));
                                Body metalClosetBody = world.createBody(metalClosetBodyDef);
                                metalClosetBody.createFixture(metalClosetFixtureDef);
                                metalClosetBody.setUserData(new BodyUserData(cell, "metalCloset"));
                                staticObjects.add(metalClosetBody);
                                break;
                            case "window":
                                boolean southWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_0 && cell.getFlipVertically() && cell.getFlipVertically();
                                boolean northWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_0 && !cell.getFlipVertically() && !cell.getFlipVertically();
                                boolean eastWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_270 && !cell.getFlipVertically() && !cell.getFlipVertically();
                                boolean westWard = cell.getRotation() == TiledMapTileLayer.Cell.ROTATE_90 && !cell.getFlipVertically() && !cell.getFlipVertically();
                                if(northWard){
                                    windowHorBodyDef.position.set(new Vector2(i+0.5f, j+0.95f));
                                    Body windowHorBody = world.createBody(windowHorBodyDef);
                                    windowHorBody.createFixture(windowHorFixtureDef);
                                    windowHorBody.setUserData(new BodyUserData(cell, "northWindow"));
                                    staticObjects.add(windowHorBody);
                                }
                                else if(southWard){
                                    windowHorBodyDef.position.set(new Vector2(i+0.5f, j+0.05f));
                                    Body windowHorBody = world.createBody(windowHorBodyDef);
                                    windowHorBody.createFixture(windowHorFixtureDef);
                                    windowHorBody.setUserData(new BodyUserData(cell, "southWindow"));
                                    staticObjects.add(windowHorBody);
                                }
                                else if(westWard){
                                    windowVertBodyDef.position.set(new Vector2(i+0.05f, j+0.5f));
                                    Body windowVertBody = world.createBody(windowVertBodyDef);
                                    windowVertBody.createFixture(windowVertFixtureDef);
                                    windowVertBody.setUserData(new BodyUserData(cell, "westWindow"));
                                    staticObjects.add(windowVertBody);
                                }
                                else if(eastWard){
                                    windowVertBodyDef.position.set(new Vector2(i+0.95f, j+0.5f));
                                    Body windowVertBody = world.createBody(windowVertBodyDef);
                                    windowVertBody.createFixture(windowVertFixtureDef);
                                    windowVertBody.setUserData(new BodyUserData(cell, "eastWindow"));
                                    staticObjects.add(windowVertBody);
                                }
                                break;
                            case "door":
                                fullBodyDef.position.set(new Vector2(i+0.5f, j+0.5f));
                                fullBody = world.createBody(fullBodyDef);
                                fullBody.createFixture(fullFixtureDef);
                                fullBody.setUserData(new Door(this,cell, fullBody, map.getTileSets().getTileSet("normalTerrain").getTile(160), map.getTileSets().getTileSet("normalTerrain").getTile(110), i, j));
                                staticObjects.add(fullBody);
                                break;
                        }
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
}
