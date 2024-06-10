package com.mygdx.game.gamestate.objects.items;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.factories.ItemsFactory;
import com.mygdx.game.gamestate.objects.tiles.Storage;
import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Null;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.objects.Interactable;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.GameObjectState;
import dev.lyze.gdxUnBox2d.UnBox;
import lombok.Getter;

public class Item implements BodyData, Interactable {
    public TiledMapTile tile;
    @Getter
    public Body physicalBody;
    public Table mouseHandler;
    protected boolean isEquipped = false;
    @Getter
     protected Storage owner;

    public UnBox unBox;
    public BodyResolver bodyResolver;
    public HUD hud;
    public long uid;
    public Stage gameStage;
    @Getter
    public String stringID = "{No tile name}"; //string item identifier
    public String itemName = "{No name item}";
    public String description = "First you must develop a Skin that implements all the widgets you plan to use in your layout. You can't use a widget if it doesn't have a valid style. Do this how you would usually develop a Skin in Scene Composer.";
    public float spriteWidth = 0.7f;
    public float spiteHeight = 0.7f;
    protected GameObject GO;
    protected @Getter SpriteBehaviour spriteBehaviour;
    ItemsFactory factory;


    public Item(long uid, TiledMapTile tile, String itemName){
        this.uid = uid;
        this.tile = tile;
        this.stringID = tile.getProperties().get("name", "no_name", String.class);
        this.itemName = itemName;
    }

    public Item(long uid, String iId, String itemName){
        this(uid, TileResolver.getTile(iId), itemName);
    }
    public void setData(UnBox box, BodyResolver resolver, HUD h, Stage st, ItemsFactory fac)
    {
        factory = fac;
        bodyResolver = resolver;
        unBox = box;
        gameStage = st;
        hud = h;
    }

    public Body allocate(Vector2 position){
        onDrop();
        prepareForRendering();

        physicalBody = bodyResolver.itemBody(position.x, position.y, this);
        new Box2dBehaviour(physicalBody, GO);
        GO.setEnabled(true);
        if (mouseHandler != null)
            mouseHandler.setPosition(getPosition().x - mouseHandler.getWidth()/2f, getPosition().y - mouseHandler.getHeight()/2f);
        return physicalBody;
    }

    protected void prepareForRendering(){
        if (unBox != null && GO == null)
            GO = new GameObject(itemName, false, unBox);

        if (hud != null && spriteBehaviour == null)
            spriteBehaviour = new SpriteBehaviour(GO, spriteWidth, spiteHeight, tile.getTextureRegion(), Globals.ITEMS_RENDER_ORDER);

        if (hud != null && mouseHandler == null) {
            mouseHandler = new Table();
            mouseHandler.setSize(spriteWidth - 0.1f, spiteHeight - 0.1f);
            mouseHandler.setTouchable(Touchable.enabled);
            mouseHandler.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    hud.debugEntries.put(stringID + "_ClickListener", "Pointing at " + stringID + " at " + getPosition());
                    hud.showItemInfoWindow(Item.this);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, @Null Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    hud.debugEntries.removeKey(stringID + "_ClickListener");
                    hud.hideItemInfoWindow(Item.this);
                }
            });
        }
        if (hud != null)
            gameStage.addActor(mouseHandler);
    }

    public void removeFromWorld(){
        if (mouseHandler != null){
            gameStage.getActors().removeValue(mouseHandler, true);
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
        player.takeItem(this);
    }

    public void onTaking(Storage storage){
        owner = storage;
    }

    public void onDrop(){
        onUnequip();
        owner = null;
    }

    public void onEquip(Player player){
        isEquipped = true;
        owner = player;

        removeFromWorld();
    }
    
    public void onUnequip(){
        if (!isEquipped())
            return;
        isEquipped = false;
        if (GO != null)
            GO.setEnabled(false);
    }

    public boolean isEquipped(){
        return isEquipped;
    }

    public void dispose(){
        removeFromWorld();
        if (owner != null)
            owner.removeItem(this);
        onDrop();
        if (GO != null && GO.getState() != GameObjectState.DESTROYED)
            GO.destroy();
        factory.onItemDispose(this);
    }

    @Override
    public String toString() {
        return "(" + uid + ") " + itemName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            if (((Item) obj).uid == uid)
                return true;
        }
        return super.equals(obj);
    }

}
