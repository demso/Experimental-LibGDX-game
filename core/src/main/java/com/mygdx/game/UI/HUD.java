package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.*;
import com.mygdx.game.UI.inventory.ContextMenu;
import com.mygdx.game.UI.inventory.InventoryHUD;
import com.mygdx.game.tiledmap.BodyUserData;

public class HUD extends Stage {
    InventoryHUD invHUD;
    ScrollPane invScroll;
    boolean isInventoryShowed;
    public GameItself gameItself;
    ObjectMap<Item, ItemInfoPopUp> itemPopups =  new ObjectMap<>();
    public Array<Actor> esClosablePopups = new Array<>();
    public ArrayMap<String, String> debugEntries = new ArrayMap<>();
    Label label;
    Skin skin;

    public HUD(GameItself gi, ScreenViewport screenViewport, SpriteBatch batch) {
        super(screenViewport, batch);
        gameItself = gi;
        skin = gameItself.skin;

        invHUD = new InventoryHUD(this, gameItself.player, 0,0);
        invScroll = new ScrollPane(invHUD, skin);
        ScrollPane.ScrollPaneStyle sps = invScroll.getStyle();
        sps.background = skin.getDrawable("default-pane");
        invScroll.setVisible(false);
        invScroll.setSize(400,300);
        invScroll.setName("InventoryScrollPane");
        invScroll.setFadeScrollBars(false);
        //invScroll.setBackground("default-pane");
        invScroll.addListener(new InputListener(){
            @Override
            public boolean handle(Event e){
                super.handle(e);
                return true;
            }
        });

        label = new Label("", skin);
        label.setFontScale(0.5f);
        label.setWidth(350);
        label.setAlignment(Align.topLeft);

        addActor(label);
        addActor(invScroll);
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
        invHUD.refill();
        invScroll.setPosition((Gdx.graphics.getWidth()-invScroll.getWidth())/2f,(Gdx.graphics.getHeight()-invScroll.getHeight())/2f, Align.bottomLeft);
        invScroll.setVisible(true);
        setScrollFocus(invScroll);
        isInventoryShowed = true;
        esClosablePopups.add(invScroll);
    }

    public void closeInventoryHUD(){
        if(invScroll != null)
            invScroll.setVisible(false);
        isInventoryShowed = false;
        esClosablePopups.removeValue(invScroll, true);
    }

    public void closeTopPopup(){
        Actor ac = esClosablePopups.pop();

        if (ac instanceof ScrollPane && ac.getName().equals(invScroll.getName())){
            closeInventoryHUD();
            return;
        }

        if (ac instanceof ContextMenu){
            invHUD.closeItemContextMenu((ContextMenu) ac);
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
        if (invHUD != null)
            invHUD.refill();
    }

    public void updateOnResize(int width, int height){
        getViewport().update(width, height, true);
        if (invScroll != null){
            invScroll.setPosition((Gdx.graphics.getWidth()-invScroll.getWidth())/2f,(Gdx.graphics.getHeight()-invScroll.getHeight())/2f, Align.bottomLeft);
        }
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
