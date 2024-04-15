package com.mygdx.game.gamestate.objects;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
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
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.objects.bodies.userdata.BodyData;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class Item implements BodyData {
    public TiledMapTile tile;
    public Body physicalBody;
    public Table mouseHandler;
    public Item item;

    public GameState gameState;
    public String tileName = "Item name.";
    public String itemName = "10мм FMJ";
    public String description = "First you must develop a Skin that implements all the widgets you plan to use in your layout. You can't use a widget if it doesn't have a valid style. Do this how you would usually develop a Skin in Scene Composer.";
    public float spriteWidth = 0.7f;
    public float spiteHeight = 0.7f;
    GameObject GO;

    public Item(TiledMapTile tile, GameState gi, String itemName){
        this.tile = tile;
        this.item = this;
        this.tileName = tile.getProperties().get("name", "no_name", String.class);
        this.gameState = gi;
        this.itemName = itemName;

        mouseHandler = new Table();
        mouseHandler.setSize(spriteWidth-0.1f,spiteHeight-0.1f);
        mouseHandler.setTouchable(Touchable.enabled);
        mouseHandler.addListener(new ClickListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                gameState.hudStage.debugEntries.put(tileName + "_ClickListener", "Pointing at "+tileName+ " at "+getPosition());
                gameState.hudStage.showItemInfoWindow(item);
            }

            @Override
            public void exit (InputEvent event, float x, float y, int pointer, @Null Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                gameState.hudStage.debugEntries.removeKey(tileName + "_ClickListener");
                gameState.hudStage.hideItemInfoWindow(item);
            }
        });

        gameState.gameStage.addActor(mouseHandler);

        GO = new GameObject("bullet", false, GameState.Instance.unbox);
        new SpriteBehaviour(GO, spriteWidth, spiteHeight, tile.getTextureRegion(), Globals.DEFAULT_RO);
    }

    public Body allocate(Vector2 position){
        physicalBody = BodyResolver.itemBody(position.x, position.y, this);
        new Box2dBehaviour(physicalBody, GO);
        GO.setEnabled(true);
        mouseHandler.setPosition(getPosition().x- mouseHandler.getWidth()/2f, getPosition().y- mouseHandler.getHeight()/2f);
        return physicalBody;
    }

    public void removeFromWorld(){
        if (mouseHandler != null){
            gameState.gameStage.getActors().removeValue(mouseHandler, true);
        }
        if (physicalBody != null){
            clearPhysicalBody();
        }
    }

    public Body getPhysicalBody() {
        return physicalBody;
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
        return "item " + itemName;
    }

    @Override
    public Object getData() {
        return this;
    }
}
