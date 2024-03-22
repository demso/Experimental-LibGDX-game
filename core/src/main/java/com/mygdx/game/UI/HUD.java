package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.*;
import com.mygdx.game.tiledmap.BodyUserData;

public class HUD extends Stage {
    InventoryHUD invHUD;
    boolean isInventoryShowed;
    GameItself gameItself;
    ObjectMap<Item, ItemInfoPopUp> itemPopups =  new ObjectMap<>();
    public Array<InventoryHUD> esClosablePopups = new Array<>();
    public ArrayMap<String, String> debugEntries = new ArrayMap<>();
    Label label;
    Skin skin;

    public HUD(GameItself gi, ScreenViewport screenViewport, SpriteBatch batch) {
        super(screenViewport, batch);
        gameItself = gi;
        skin = gameItself.skin;

        label = new Label("", skin);
        label.setFontScale(0.5f);
        label.setWidth(350);
        label.setAlignment(Align.topLeft);
        addActor(label);
    }

    public void showItemInfoWindow(Item item){
        getActors().removeValue(itemPopups.get(item), true);
        Vector3 mousePosition = getCamera().unproject(new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0));
        Vector3 itemPos = getCamera().unproject(gameItself.gameStage.getCamera().project(new Vector3(item.position, 0)));
        ItemInfoPopUp popup = new ItemInfoPopUp(item,itemPos.x,Gdx.graphics.getHeight()-itemPos.y);
        addActor(popup);
        itemPopups.put(item, popup);
    }

    public void hideItemInfoWindow(Item item){
        getActors().removeValue(itemPopups.get(item), true);
    }

    public void showInventoryHUD(){
        invHUD = new InventoryHUD(gameItself.player, 0,0);
        invHUD.setPosition((Gdx.graphics.getWidth()-invHUD.getWidth())/2f,(Gdx.graphics.getHeight()-invHUD.getHeight())/2f, Align.bottomLeft);
        addActor(invHUD);
        isInventoryShowed = true;
        esClosablePopups.add(invHUD);
    }

    public void closeInventoryHUD(){
        getActors().removeValue(invHUD, true);
        isInventoryShowed = false;
        esClosablePopups.removeValue(invHUD, true);
    }

    public void toggleInventoryHUD(){
        if (isInventoryShowed)
            closeInventoryHUD();
        else
            showInventoryHUD();
    }

    public void updateInvHUD(){
        if (invHUD != null)
            invHUD.refill();
    }

    public void updateOnResize(){

    }

    Body clObj;
    StringBuilder labelText = new StringBuilder();
    public void drawTileDebugInfo() {
        labelText = new StringBuilder();
        labelText.append("Player velocity : ").append(gameItself.player.body.getLinearVelocity()).append("\n");
        clObj = gameItself.player.closestObject;
        labelText.append("Closest object : ").append(clObj == null ? null : clObj.getUserData() instanceof BodyUserData ? ((BodyUserData) clObj.getUserData()).bodyName + " " + clObj.getPosition() : clObj.getUserData()).append("\n\n");
        if (debugEntries.size > 0)
            debugEntries.values().forEach((kall) -> labelText.append(kall).append("\n\n"));
        Vector3 mouse_position = new Vector3(0,0,0);
        Vector3 tilePosition = gameItself.camera.unproject(mouse_position.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0));
        int tileX = (int)Math.floor(tilePosition.x);
        int tileY = (int)Math.floor(tilePosition.y);
        for (var x = 0; x < gameItself.map.getLayers().size(); x++){
            TiledMapTileLayer currentLayer = (TiledMapTileLayer)gameItself.map.getLayers().get(x);
            TiledMapTileLayer.Cell mcell = currentLayer.getCell(tileX, tileY);

            if(mcell != null){
                labelText.append("Rotation : ").append(mcell.getRotation()).append("\nFlip Horizontally : ").append(mcell.getFlipHorizontally()).append("\nFlip Vertically : ").append(mcell.getFlipVertically()).append("\nLayer : ").append(currentLayer.getName()).append("\nX : ").append(tileX).append("\n").append("Y : ").append(tileY).append("\n").append("ID : ").append(mcell.getTile().getId()).append("\n");
                var itrK = mcell.getTile().getProperties().getKeys();
                var itrV = mcell.getTile().getProperties().getValues();
                while (itrK.hasNext()){
                    labelText.append(itrK.next()).append(" : ").append(itrV.next()).append("\n");
                }
            }
            labelText.append("\n");
        }
        label.setText(labelText);
    }

    public void update(Boolean debug){
        float height = Gdx.graphics.getHeight();
        float width = Gdx.graphics.getWidth();
        label.setPosition(100, height - 100);
        if(debug)
            drawTileDebugInfo();
        else
            label.setText("");
    }
}
