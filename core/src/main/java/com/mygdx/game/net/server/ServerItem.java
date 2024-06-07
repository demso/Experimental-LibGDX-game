package com.mygdx.game.net.server;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.net.PlayerInfo;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import lombok.Getter;

public class ServerItem {
    @Getter
    public Body physicalBody;
    protected boolean isEquipped = false;
    @Getter
    long ownerId;
    public long uid;
    public String itemId = "{No tile name}"; //string item identifier
    public String itemName = "{No name item}";
    public String description = "First you must develop a Skin that implements all the widgets you plan to use in your layout. You can't use a widget if it doesn't have a valid style. Do this how you would usually develop a Skin in Scene Composer.";
    protected GameObject GO;
    public ServGameState gameState;//assign in factory

    public ServerItem(String itemId, String itemName){
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public Body allocate(Vector2 position){
        if (GO == null)
            GO = new GameObject(itemName, false, gameState.unbox);
        physicalBody = GameState.instance.bodyResolver.itemBody(position.x, position.y, this);
        new Box2dBehaviour(physicalBody, GO);
        GO.setEnabled(true);
        return physicalBody;
    }

    public void removeFromWorld(){
        if (physicalBody != null){
            GO.setEnabled(false);
            physicalBody = null;
            GO.destroy(GO.getBox2dBehaviour());
        }
    }

    public Vector2 getPosition(){
        return physicalBody.getPosition();
    }

    public String getName() {
        return itemName == null ? "" : itemName;
    }

    public Object getData() {
        return this;
    }

//    public void interact(Player player) {
//        player.takeItem(this);
//    }

    public void onTaking(PlayerInfo player){
        ownerId = player.id;
    }

    public void onEquip(PlayerInfo player){
        isEquipped = true;
        ownerId = player.id;
        removeFromWorld();
    }

    public void onUnequip(){
        isEquipped = false;
    }

    public boolean isEquipped(){
        return isEquipped;
    }

    public void dispose(){
        GO.destroy();
    }
}
