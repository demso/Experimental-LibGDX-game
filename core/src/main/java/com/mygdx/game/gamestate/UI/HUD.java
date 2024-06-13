package com.mygdx.game.gamestate.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.mygdx.game.Utils;
import com.mygdx.game.gamestate.UI.inventory.*;
import com.mygdx.game.gamestate.player.ClientPlayer;
import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.objects.bodies.userdata.SimpleUserData;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.tiles.Storage;
import dev.lyze.gdxUnBox2d.GameObject;

public class HUD extends Stage {
    boolean debug = false;
    public PlayerInventoryHUD playerInventoryHud;
    public StorageInventoryHUD storageInventoryHUD;
    public InventoryTools tools;
    HorizontalGroup panels;
    public GameState gameState;
    ObjectMap<Item, ItemInfoPopUp> itemPopups =  new ObjectMap<>();
    private Array<Actor> esClosablePopups = new Array<>();
    public ArrayMap<String, String> debugEntries = new ArrayMap<>();
    Label label;
    Skin skin;
    GameObject gameObject;
    volatile boolean showRequestSatisfied;
    volatile int showRequestX, showRequestY;
    Item[] showRequestItems;
    public ClientPlayer clientPlayer;
    InfoPanel infoPanel;

    public void showItemInfoWindow(Item item){
        getActors().removeValue(itemPopups.get(item), true);
        Vector3 itemPos = getCamera().unproject(gameState.gameStage.getCamera().project(new Vector3(item.getPosition(), 0)));
        ItemInfoPopUp popup = new ItemInfoPopUp(item,itemPos.x,Gdx.graphics.getHeight()-itemPos.y);
        itemPopups.put(item, popup);
        showPopup(popup);
    }

    public void hideItemInfoWindow(Item item){
        closePopup(itemPopups.get(item));
    }

    public void showRequestStorageInventoryHUD(Storage storage){
        storageInventoryHUD.requestShowOnStorage(storage);
    }

    public void onStoredItemsReceived(int recX, int recY, Item[] items){
        showRequestX = recX;
        showRequestY = recY;
        showRequestItems = items;
        showRequestSatisfied = true;
    }

    private void showStorageInventoryHUD(Storage storage) {
        if (storageInventoryHUD.isVisible()){
            storageInventoryHUD.refill();
            return;
        }
        panels.addActor(storageInventoryHUD);

        tools.setVisible(true);
        panels.addActor(tools);

        storageInventoryHUD.onShow(storage);
        showPlayerInventoryHud();
        if (GameState.instance.clientPlayer.getClosestObject() == null || GameState.instance.clientPlayer.getClosestObject().getUserData() != storage)
            playerInventoryHud.storageInventoryNear();
        storageInventoryHUD.setVisible(true);
        if (!storageInventoryHUD.isBottomEdge())
            setScrollFocus(storageInventoryHUD);
        addEscClosable(panels);
        updatePanels();
    }
    //boolean if should player's inventory be closed
    public void closeStorageInventoryHUD(boolean closePlayer){
        if (!storageInventoryHUD.isVisible())
            return;
        panels.removeActor(storageInventoryHUD);

        panels.removeActor(tools);
        tools.setVisible(false);

        if (closePlayer)
            closePlayerInventoryHud();
        storageInventoryHUD.setVisible(false);
        storageInventoryHUD.onClose();
        esClosablePopups.removeValue(storageInventoryHUD, true);
        if (GameState.instance.clientPlayer.getClosestObject() == null || GameState.instance.clientPlayer.getClosestObject().getUserData() != storageInventoryHUD.getStorage())
            playerInventoryHud.storageInventoryFar();
        updatePanels();
    }

    public void toggleStorageInventoryHUD(Storage storage, boolean offPlayersInv){
        if (storageInventoryHUD.isVisible())
            closeStorageInventoryHUD(offPlayersInv);
        else
            showRequestStorageInventoryHUD(storage);
    }

    public void showPlayerInventoryHud(){
        if (playerInventoryHud.isVisible())
            return;
        panels.addActorAt(0, playerInventoryHud);
        playerInventoryHud.onShow(GameState.instance.clientPlayer);

        playerInventoryHud.setVisible(true);
        setScrollFocus(playerInventoryHud);
        addEscClosable(panels);
        updatePanels();
    }


    public void closePlayerInventoryHud(){
        if (!playerInventoryHud.isVisible())
            return;
        panels.removeActor(playerInventoryHud);

        playerInventoryHud.setVisible(false);
        playerInventoryHud.onClose();
        esClosablePopups.removeValue(playerInventoryHud, true);
        updatePanels();
    }

    public void togglePlayerInventoryHUD(){
        if (playerInventoryHud.isVisible())
            closePlayerInventoryHud();
        else
            showPlayerInventoryHud();
    }

    Vector2 oldSPos = new Vector2(),
            oldPPos = new Vector2(),
            offsetS = new Vector2(),
            offsetP = new Vector2(),
            temZeroVector = new Vector2(0, 0);

    public void updatePanels(){
        if (storageInventoryHUD.isVisible()){
            temZeroVector.set(0,0);
            oldSPos.set(storageInventoryHUD.localToStageCoordinates(temZeroVector));
        }
        if (playerInventoryHud.isVisible()){
            temZeroVector.set(0,0);
            oldPPos.set(playerInventoryHud.localToStageCoordinates(temZeroVector));
        }
        storageInventoryHUD.onPositionChanging();
        storageInventoryHUD.layout();
        storageInventoryHUD.validate();
        playerInventoryHud.onPositionChanging();
        playerInventoryHud.layout();
        playerInventoryHud.validate();

        tools.setHeight(storageInventoryHUD.getPrefHeight());
        tools.update();
        tools.layout();
        tools.validate();

        panels.layout();
        panels.validate();

        panels.setPosition(Math.round((Gdx.graphics.getWidth()/2f - (panels.getPrefWidth()-tools.getPrefWidth()/2f)/2f)), Math.round(Gdx.graphics.getHeight() / 2f), Align.bottomLeft);


        panels.validate();

        if (storageInventoryHUD.isVisible()){
            temZeroVector.set(0,0);
            storageInventoryHUD.onPositionChanged(storageInventoryHUD.localToStageCoordinates(temZeroVector).sub(oldSPos));
        }
        if (playerInventoryHud.isVisible()){
            temZeroVector.set(0,0);
            playerInventoryHud.onPositionChanged(playerInventoryHud.localToStageCoordinates(temZeroVector).sub(oldPPos));
        }

    }

    public void updateInvHUDContent(){
        if (playerInventoryHud.isVisible())
            playerInventoryHud.refill();
        if (storageInventoryHUD.isVisible())
            storageInventoryHUD.refill();
    }

    public void showPopup(Actor actor){
        addEscClosable(actor);
        //addActor(actor); ItemPopup не показывается изза этого
    }

    public void closePopup(Actor actor){
        esClosablePopups.removeValue(actor, true);
        //getActors().removeValue(actor, true); заменить на removeActor()
    }

    public boolean closeTopPopup(){
        if (esClosablePopups.contains(panels, true) && panels.getChildren().size == 0)
            esClosablePopups.removeValue(panels, true);
        
        if (esClosablePopups.isEmpty())
            return false;

        Actor ac = esClosablePopups.pop();

        if (ac == panels){
            closeStorageInventoryHUD(true);
            closePlayerInventoryHud();
            //panels.setVisible(false);
            return true;
        }

        if (ac instanceof ContextMenu){
            playerInventoryHud.closeItemContextMenu((ContextMenu) ac);
            return true;
        }

        getActors().removeValue(ac, true);

        return true;
    }

    public void updateOnResize(int width, int height){
        getViewport().update(width, height, true);
        updatePanels();
    }

    Body clObj;
    StringBuilder labelText = new StringBuilder();
    public void drawTileDebugInfo() {
        setDebugAll(true);
        labelText = new StringBuilder();
        Vector2 vel = gameState.clientPlayer.getBody().getLinearVelocity();
        labelText.append("Player velocity : ").append(Utils.round(vel.x, 1)).append(", ").append(Utils.round(vel.y, 1)).append("\n");
        clObj = gameState.clientPlayer.getClosestObject();
        labelText.append("Closest object : ").append(clObj == null ? null : clObj.getUserData() instanceof SimpleUserData ? ((SimpleUserData) clObj.getUserData()).bodyName + " " + clObj.getPosition() : clObj.getUserData()).append("\n");
        if (gameState.clientPlayer.reloadProgress < 1)
            labelText.append("Reload progress: " + gameState.clientPlayer.reloadProgress + "\n");
        if (debugEntries.size > 0)
            debugEntries.values().forEach((kall) -> labelText.append(kall).append("\n\n"));
        Vector3 mouse_position = new Vector3(0,0,0);
        Vector3 tilePosition = gameState.camera.unproject(mouse_position.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0));
        int tileX = (int)Math.floor(tilePosition.x);
        int tileY = (int)Math.floor(tilePosition.y);
        for (var x = 0; x < gameState.map.getLayers().size(); x++){
            MapLayer currentLayer = gameState.map.getLayers().get(x);
            if (! (currentLayer instanceof TiledMapTileLayer))
                continue;
            TiledMapTileLayer.Cell mcell = ((TiledMapTileLayer)currentLayer).getCell(tileX, tileY);

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

    public void addEscClosable(Actor actor){
        if (esClosablePopups.contains(actor, true))
            return;
        esClosablePopups.add(actor);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float height = Gdx.graphics.getHeight();
        float width = Gdx.graphics.getWidth();
        label.setPosition(100, height - 100);
        if(GameState.instance.debug)
            drawTileDebugInfo();
        else {
            label.setText("");
            setDebugAll(false);
        }

        if (debug)
            SecondGDXGame.instance.helper.log("[HUD:act] " + esClosablePopups.toString());

        if (showRequestSatisfied){
            Vector2 vec = storageInventoryHUD.getStorage().getPosition();
            if (showRequestX == (int)Math.floor(vec.x) && showRequestY == (int)Math.floor(vec.y)) {
                storageInventoryHUD.getStorage().setInventoryItems(showRequestItems);
                showStorageInventoryHUD(storageInventoryHUD.getStorage());
            }
            showRequestSatisfied = false;
        }

        infoPanel.update(delta);
    }

    public void setClientPlayer(ClientPlayer pl){
        clientPlayer = pl;
        infoPanel.refresh();
    }

    @Override
    public void draw() {
        super.draw();
//        scopeRenderer.start();
//        scopeRenderer.render(scopeGroup);
//        scopeRenderer.end();
    }

    public HUD(GameState gi, ScreenViewport screenViewport, SpriteBatch batch) {
        super(screenViewport, batch);
        gameState = gi;
        gameObject = new GameObject("HUD", gameState.unbox);
        skin = gameState.skin;

        panels = new HorizontalGroup();

        playerInventoryHud = new PlayerInventoryHUD(this);
        playerInventoryHud.setVisible(false);

        storageInventoryHUD = new StorageInventoryHUD(this, ContextMenu.Action.Take, ContextMenu.Action.Equip, ContextMenu.Action.Drop, ContextMenu.Action.Description);
        storageInventoryHUD.setVisible(false);

        tools = new InventoryTools(this);
        tools.setVisible(false);

        label = new Label("", skin);
        label.setFontScale(0.5f);
        label.setWidth(350);
        label.setAlignment(Align.topLeft);

        addActor(label);
        panels.space(5);
        addActor(panels);
//
//        scopeRenderer = new ScopeRenderer(16);
//        scopeGroup = new Group(new ObjectScope("Settings", infoPanel));
    }

    public void init(){
        infoPanel = new InfoPanel(this);
        infoPanel.setPosition(0, 0, Align.bottomLeft);
        addActor(infoPanel);
    }
}
