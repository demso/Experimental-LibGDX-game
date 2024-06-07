package com.mygdx.game.gamestate.objects.items;

import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Null;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import lombok.Getter;

import static com.mygdx.game.gamestate.GameState.instance;

public class Item implements BodyData, Interactable {
    public TiledMapTile tile;
    @Getter
    public Body physicalBody;
    public Table mouseHandler;
    protected boolean isEquipped = false;
    @Getter Player owner;

    public GameState gameState;
    public long uid;
    public String itemId = "{No tile name}"; //string item identifier
    public String itemName = "{No name item}";
    public String description = "First you must develop a Skin that implements all the widgets you plan to use in your layout. You can't use a widget if it doesn't have a valid style. Do this how you would usually develop a Skin in Scene Composer.";
    public float spriteWidth = 0.7f;
    public float spiteHeight = 0.7f;
    protected GameObject GO;
    protected @Getter SpriteBehaviour spriteBehaviour;

    public Item(TiledMapTile tile, String itemName){
        this.tile = tile;
        this.itemId = tile.getProperties().get("name", "no_name", String.class);
        this.gameState = instance;
        this.itemName = itemName;
    }

    public Item(String itemId, String itemName){
        this(TileResolver.getTile(itemId), itemName);
    }

    public Body allocate(Vector2 position){
        prepareForRendering();

        physicalBody = GameState.instance.bodyResolver.itemBody(position.x, position.y, this);
        new Box2dBehaviour(physicalBody, GO);
        GO.setEnabled(true);
        mouseHandler.setPosition(getPosition().x - mouseHandler.getWidth()/2f, getPosition().y - mouseHandler.getHeight()/2f);
        return physicalBody;
    }

    protected void prepareForRendering(){
        if (GO == null)
            GO = new GameObject(itemName, false, instance.unbox);

        if (spriteBehaviour == null)
            spriteBehaviour = new SpriteBehaviour(GO, spriteWidth, spiteHeight, tile.getTextureRegion(), Globals.DEFAULT_RENDER_ORDER);

        if (mouseHandler == null) {
            mouseHandler = new Table();
            mouseHandler.setSize(spriteWidth - 0.1f, spiteHeight - 0.1f);
            mouseHandler.setTouchable(Touchable.enabled);
            mouseHandler.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    gameState.hud.debugEntries.put(itemId + "_ClickListener", "Pointing at " + itemId + " at " + getPosition());
                    gameState.hud.showItemInfoWindow(Item.this);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, @Null Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    gameState.hud.debugEntries.removeKey(itemId + "_ClickListener");
                    gameState.hud.hideItemInfoWindow(Item.this);
                }
            });
        }

        gameState.gameStage.addActor(mouseHandler);
    }

    public void removeFromWorld(){
        if (mouseHandler != null){
            gameState.gameStage.getActors().removeValue(mouseHandler, true);
        }
        if (physicalBody != null){
            clearPhysicalBody();
        }
    }

    public Vector2 getPosition(){
        return physicalBody.getPosition();
    }

    public void clearPhysicalBody(){
        GO.setEnabled(false);
        physicalBody = null;
        GO.destroy(GO.getBox2dBehaviour());
    }

    @Override
    public String getName() {
        return itemName == null ? "" : itemName;
    }

    @Override
    public Object getData() {
        return this;
    }

    @Override
    public void interact(Player player) {
        instance.clientPlayer.takeItem(this);
    }

    public void onTaking(Player player){
        owner = player;
    }

    public void onEquip(Player player){
        isEquipped = true;
        owner = player;
        removeFromWorld();
    }
    
    public void unequip(){
        isEquipped = false;
    }

    public boolean isEquipped(){
        return isEquipped;
    }

    public void dispose(){
        unequip();
        GO.destroy();
    }
}
