package com.mygdx.game.gamestate.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
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
import com.mygdx.game.gamestate.UI.inventory.ContextMenu;
import com.mygdx.game.gamestate.UI.inventory.PlayerInventoryHUD;
import com.mygdx.game.gamestate.UI.inventory.StorageInventoryHUD;
import com.mygdx.game.gamestate.objects.bodies.userdata.SimpleUserData;
import com.mygdx.game.gamestate.objects.Item;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.tiles.Storage;

public class HUD extends Stage {
    public PlayerInventoryHUD playerInventoryHud;
    public StorageInventoryHUD storageInventoryHUD;
    HorizontalGroup panels;
    public GameState gameState;
    ObjectMap<Item, ItemInfoPopUp> itemPopups =  new ObjectMap<>();
    private Array<Actor> esClosablePopups = new Array<>();
    public ArrayMap<String, String> debugEntries = new ArrayMap<>();
    Label label;
    Skin skin;

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

    public void showStorageInventoryHUD(Storage storage){
        if (storageInventoryHUD.isVisible()) {
            if (storageInventoryHUD.storage != storage){
                storageInventoryHUD.storage = storage;
                storageInventoryHUD.refill();
            }
            return;
        }
        panels.addActor(storageInventoryHUD);
        storageInventoryHUD.onShow(storage);
        showPlayerInventoryHud();
        if (GameState.Instance.player.getClosestObject() == null || GameState.Instance.player.getClosestObject().getUserData() != storage)
            playerInventoryHud.storageInventoryNear();
        storageInventoryHUD.setVisible(true);
        setScrollFocus(storageInventoryHUD);
        addEscClosable(panels);
        updatePanels();
    }
    //boolean if should player's inventory be closed
    public void closeStorageInventoryHUD(boolean closePlayer){
        if (!storageInventoryHUD.isVisible())
            return;
        panels.removeActor(storageInventoryHUD);
        if (closePlayer)
            closePlayerInventoryHud();
        storageInventoryHUD.setVisible(false);
        storageInventoryHUD.onClose();
        esClosablePopups.removeValue(storageInventoryHUD, true);
        if (GameState.Instance.player.getClosestObject() == null || GameState.Instance.player.getClosestObject().getUserData() != storageInventoryHUD.storage)
            playerInventoryHud.storageInventoryFar();
        updatePanels();
    }

    public void toggleStorageInventoryHUD(Storage storage, boolean offPlayersInv){
        if (storageInventoryHUD.isVisible())
            closeStorageInventoryHUD(offPlayersInv);
        else
            showStorageInventoryHUD(storage);
    }

    public void showPlayerInventoryHud(){
        if (playerInventoryHud.isVisible())
            return;
        panels.addActorAt(0, playerInventoryHud);
        playerInventoryHud.onShow(GameState.Instance.player);

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

    public void updatePanels(){
        Vector2 oldSPos = new Vector2(panels.getX() + storageInventoryHUD.getX(), panels.getY() + storageInventoryHUD.getY()),
                oldPPos = new Vector2(panels.getX() + playerInventoryHud.getX(), panels.getY() + playerInventoryHud.getY());

        panels.setPosition((Gdx.graphics.getWidth()- panels.getPrefWidth())/2f,(Gdx.graphics.getHeight() - panels.getMaxHeight())/2f, Align.bottomLeft);
        panels.validate();

        Vector2 newSPos = new Vector2(panels.getX() + storageInventoryHUD.getX(), panels.getY() + storageInventoryHUD.getY()),
                newPPos = new Vector2(panels.getX() + storageInventoryHUD.getX(), panels.getY() + storageInventoryHUD.getY());
        Vector2 offsetS = new Vector2(newSPos).sub(oldSPos),
                offsetP = new Vector2(newPPos).sub(oldPPos);

        if (storageInventoryHUD.isVisible()){
            storageInventoryHUD.onPositionChanged(offsetS);
            System.out.println(storageInventoryHUD.localToScreenCoordinates(new Vector2(Vector2.Zero)));
        }
        if (playerInventoryHud.isVisible()) playerInventoryHud.onPositionChanged(offsetP);

    }

    public void updateInvHUDContent(){
        if (playerInventoryHud.isVisible())
            playerInventoryHud.refill();
        if (storageInventoryHUD.isVisible())
            storageInventoryHUD.refill();
    }

    public void showPopup(Actor actor){
        addEscClosable(actor);
        //addActor(actor);
    }

    public void closePopup(Actor actor){
        esClosablePopups.removeValue(actor, true);
        //getActors().removeValue(actor, true);
    }

    public boolean closeTopPopup(){
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
        if(GameState.Instance.debug)
            drawTileDebugInfo();
        else
            label.setText("");
    }

    public HUD(GameState gi, ScreenViewport screenViewport, SpriteBatch batch) {
        super(screenViewport, batch);
        gameState = gi;
        skin = gameState.skin;

        panels = new HorizontalGroup();

        playerInventoryHud = new PlayerInventoryHUD(this);

        playerInventoryHud.setVisible(false);

        storageInventoryHUD = new StorageInventoryHUD(this, ContextMenu.ConAction.Put, ContextMenu.ConAction.Description, ContextMenu.ConAction.Equip, ContextMenu.ConAction.Take);
        storageInventoryHUD.setVisible(false);

        label = new Label("", skin);
        label.setFontScale(0.5f);
        label.setWidth(350);
        label.setAlignment(Align.topLeft);

        addActor(label);
        panels.space(5);
        addActor(panels);

        //setDebugAll(true);
    }
}
