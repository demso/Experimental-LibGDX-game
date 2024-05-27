package com.mygdx.game.gamestate.objects.items.guns;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.factories.BodyResolver;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.gamestate.objects.bullet.Bullet;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;
import dev.lyze.gdxUnBox2d.BehaviourState;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

import static com.mygdx.game.gamestate.GameState.instance;

public class Gun extends Item {
    GunSpriteBehaviour gunSpriteBehaviour;
    public Gun(TiledMapTile tile, String itemName) {
        super(tile, itemName);
        spriteWidth = 0.4f;
        spiteHeight = 0.4f;
    }

    public Gun(String tileName, String itemName) {
        super(tileName, itemName);
        spriteWidth = 0.4f;
        spiteHeight = 0.4f;
    }

    public void fireBullet(Player player){
        (gunSpriteBehaviour).onFire();
        Vector3 mousePos = gameState.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 vv = new Vector2(mousePos.x-player.getPosition().x, mousePos.y-player.getPosition().y);
        new Bullet(TileResolver.getTile("bullet"), player.getPosition(), vv);
    }

    @Override
    public Body allocate(Vector2 position){
        prepareForRendering();

        physicalBody = BodyResolver.itemBody(position.x, position.y, this);
        new Box2dBehaviour(physicalBody, GO);
        GO.setEnabled(true);
        mouseHandler.setPosition(getPosition().x - mouseHandler.getWidth()/2f, getPosition().y - mouseHandler.getHeight()/2f);
        
        return physicalBody;
    }

    @Override
    protected void prepareForRendering() {
        if (GO == null)
            GO = new GameObject(itemName, false, instance.unbox);

        if (gunSpriteBehaviour == null || gunSpriteBehaviour.getState().equals(BehaviourState.DESTROYED))
                gunSpriteBehaviour = new GunSpriteBehaviour(GO, this, spriteWidth, spiteHeight, tile.getTextureRegion(), Globals.DEFAULT_RENDER_ORDER);

        if (isEquipped()) {
        } else {

            if (mouseHandler == null) {
                mouseHandler = new Table();
                mouseHandler.setSize(spriteWidth - 0.1f, spiteHeight - 0.1f);
                mouseHandler.setTouchable(Touchable.enabled);
                mouseHandler.addListener(new ClickListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
                        super.enter(event, x, y, pointer, fromActor);
                        gameState.hud.debugEntries.put(tileName + "_ClickListener", "Pointing at " + tileName + " at " + getPosition());
                        gameState.hud.showItemInfoWindow(Gun.this);
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, @Null Actor toActor) {
                        super.exit(event, x, y, pointer, toActor);
                        gameState.hud.debugEntries.removeKey(tileName + "_ClickListener");
                        gameState.hud.hideItemInfoWindow(Gun.this);
                    }
                });
            }

            gameState.gameStage.addActor(mouseHandler);
        }
    }

    @Override
    public void equip(Player player){
        super.equip(player);
        prepareForRendering();
        GO.setEnabled(true);
    }

    @Override
    public void unequip() {
        isEquipped = false;
        if (GO != null)
            GO.setEnabled(false);
    }


}
