package com.mygdx.game.gamestate.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.gamestate.UI.inventory.ContextMenu;
import com.mygdx.game.gamestate.UI.inventory.InventoryHUD;
import com.mygdx.game.gamestate.objects.bodies.userdata.SimpleUserData;
import com.mygdx.game.gamestate.objects.Item;
import com.mygdx.game.gamestate.GameState;

public class HUD extends Stage {
    InventoryHUD inventoryHUD;
    boolean isInventoryShowed;
    public GameState gameState;
    ObjectMap<Item, ItemInfoPopUp> itemPopups =  new ObjectMap<>();
    public Array<Actor> esClosablePopups = new Array<>();
    public ArrayMap<String, String> debugEntries = new ArrayMap<>();
    Label label;
    Skin skin;

    public void showItemInfoWindow(Item item){
        getActors().removeValue(itemPopups.get(item), true);
        Vector3 mousePosition = getCamera().unproject(new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0));
        Vector3 itemPos = getCamera().unproject(gameState.gameStage.getCamera().project(new Vector3(item.getPosition(), 0)));
        ItemInfoPopUp popup = new ItemInfoPopUp(item,itemPos.x,Gdx.graphics.getHeight()-itemPos.y);
        addActor(popup);
        itemPopups.put(item, popup);
    }

    public void hideItemInfoWindow(Item item){
        getActors().removeValue(itemPopups.get(item), true);
    }

    public void showInventoryHUD(){
        inventoryHUD.refill();
        inventoryHUD.setPosition((Gdx.graphics.getWidth()- inventoryHUD.getWidth())/2f,(Gdx.graphics.getHeight()- inventoryHUD.getHeight())/2f, Align.bottomLeft);
        inventoryHUD.setVisible(true);
        setScrollFocus(inventoryHUD);
        isInventoryShowed = true;
        esClosablePopups.add(inventoryHUD);
    }

    public void closeInventoryHUD(){
        if(inventoryHUD != null)
            inventoryHUD.setVisible(false);
        inventoryHUD.onClose();
        isInventoryShowed = false;
        esClosablePopups.removeValue(inventoryHUD, true);
    }

    public void closeTopPopup(){
        Actor ac = esClosablePopups.pop();

        if (ac instanceof ScrollPane && ac.getName().equals(inventoryHUD.getName())){
            closeInventoryHUD();
            return;
        }

        if (ac instanceof ContextMenu){
            inventoryHUD.closeItemContextMenu((ContextMenu) ac);
            return;
        }

        getActors().removeValue(ac, true);

    }

    public void toggleInventoryHUD(){
        if (isInventoryShowed)
            closeInventoryHUD();
        else
            showInventoryHUD();
    }

    public void updateInvHUDContent(){
        if (inventoryHUD != null)
            inventoryHUD.refill();
    }

    public void updateOnResize(int width, int height){
        getViewport().update(width, height, true);
        if (inventoryHUD != null){
            inventoryHUD.setPosition((Gdx.graphics.getWidth()- inventoryHUD.getWidth())/2f,(Gdx.graphics.getHeight()- inventoryHUD.getHeight())/2f, Align.bottomLeft);
        }
    }

    Body clObj;
    StringBuilder labelText = new StringBuilder();
    public void drawTileDebugInfo() {
        labelText = new StringBuilder();
        labelText.append("Player velocity : ").append(gameState.player.getBody().getLinearVelocity()).append("\n");
        clObj = gameState.player.closestObject;
        labelText.append("Closest object : ").append(clObj == null ? null : clObj.getUserData() instanceof SimpleUserData ? ((SimpleUserData) clObj.getUserData()).bodyName + " " + clObj.getPosition() : clObj.getUserData()).append("\n\n");
        if (debugEntries.size > 0)
            debugEntries.values().forEach((kall) -> labelText.append(kall).append("\n\n"));
        Vector3 mouse_position = new Vector3(0,0,0);
        Vector3 tilePosition = gameState.camera.unproject(mouse_position.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0));
        int tileX = (int)Math.floor(tilePosition.x);
        int tileY = (int)Math.floor(tilePosition.y);
        for (var x = 0; x < gameState.map.getLayers().size(); x++){
            TiledMapTileLayer currentLayer = (TiledMapTileLayer) gameState.map.getLayers().get(x);
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

    @Override
    public void act(float delta) {
        super.act(delta);

        float height = Gdx.graphics.getHeight();
        float width = Gdx.graphics.getWidth();
        label.setPosition(100, height - 100);
        if(GameState.Instance.debug)
            drawTileDebugInfo();
        else
            label.setText("");
    }

    public HUD(GameState gi, ScreenViewport screenViewport, SpriteBatch batch) {
        super(screenViewport, batch);
        gameState = gi;
        skin = gameState.skin;

        inventoryHUD = new InventoryHUD(this, gameState.player);
        inventoryHUD.setVisible(false);

        label = new Label("", skin);
        label.setFontScale(0.5f);
        label.setWidth(350);
        label.setAlignment(Align.topLeft);

        addActor(label);
        addActor(inventoryHUD);
    }
}
