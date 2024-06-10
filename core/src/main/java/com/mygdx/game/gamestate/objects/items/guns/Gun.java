package com.mygdx.game.gamestate.objects.items.guns;
import com.mygdx.game.gamestate.player.ClientPlayer;
import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Null;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.objects.bullet.Bullet;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;
import dev.lyze.gdxUnBox2d.BehaviourState;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;

public class Gun extends Item {
    GunSpriteBehaviour gunSpriteBehaviour;
    Vector2 bulletTempRotationVec = new Vector2(1,1);
    public Gun(long uid, TiledMapTile tile, String itemName) {
        super(uid, tile, itemName);
        spriteWidth = 0.4f;
        spiteHeight = 0.4f;
    }

    public Gun(long uid, String tileName, String itemName) {
        super(uid, tileName, itemName);
        spriteWidth = 0.4f;
        spiteHeight = 0.4f;
    }

    public void fireBullet(Player player){
        bulletTempRotationVec.setAngleDeg(player.itemRotation);
        gunSpriteBehaviour.onFire();
        new Bullet(TileResolver.getTile("bullet"), player.getPosition(), bulletTempRotationVec);
    }

    @Override
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

    @Override
    protected void prepareForRendering() {
        if (GO == null)
            GO = new GameObject(itemName, false, unBox);

        if (hud == null)
            return;

        if (gunSpriteBehaviour == null || gunSpriteBehaviour.getState().equals(BehaviourState.DESTROYED))
                gunSpriteBehaviour = new GunSpriteBehaviour(GO, this, spriteWidth, spiteHeight, tile.getTextureRegion(), Globals.DEFAULT_RENDER_ORDER);

        if (getOwner() instanceof ClientPlayer)
            gunSpriteBehaviour.setRenderOrder(Globals.PLAYER_RENDER_ORDER);
        else
            gunSpriteBehaviour.setRenderOrder(Globals.ANOTHER_PLAYER_RENDER_ORDER);

        if (isEquipped()) {}
        else {
            if (hud != null && mouseHandler == null) {
                mouseHandler = new Table();
                mouseHandler.setSize(spriteWidth - 0.1f, spiteHeight - 0.1f);
                mouseHandler.setTouchable(Touchable.enabled);
                mouseHandler.addListener(new ClickListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
                        super.enter(event, x, y, pointer, fromActor);
                        hud.debugEntries.put(itemId + "_ClickListener", "Pointing at " + itemId + " at " + getPosition());
                        hud.showItemInfoWindow(Gun.this);
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, @Null Actor toActor) {
                        super.exit(event, x, y, pointer, toActor);
                        hud.debugEntries.removeKey(itemId + "_ClickListener");
                        hud.hideItemInfoWindow(Gun.this);
                    }
                });
            }
            if (hud != null)
                gameStage.addActor(mouseHandler);
        }
    }

    @Override
    public void onEquip(Player player){
        super.onEquip(player);
        prepareForRendering();
        GO.setEnabled(true);
    }

    @Override
    public void onUnequip() {
        isEquipped = false;
        if (GO != null)
            GO.setEnabled(false);
    }


}
