package com.mygdx.game.gamestate.objects.items.guns;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.player.ClientPlayer;
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
import lombok.Getter;
import lombok.Setter;

public class Gun extends Item {
    public enum FireType {
        AUTO,
        SEMI_AUTO
    }
    protected GunSpriteBehaviour gunSpriteBehaviour;
    protected Vector2 bulletTempRotationVec = new Vector2(1,1);
    protected GunMagazine insertedMagazine;
    @Getter
    protected float reloadTime = 2;
    @Getter @Setter
    protected FireType fireType = FireType.SEMI_AUTO;

    public Gun(long uid, String tileName, String itemName) {
        super(uid, tileName, itemName);
        spriteWidth = 0.4f;
        spiteHeight = 0.4f;
    }

    public void reload(GunMagazine mag){
        if (insertedMagazine != null){
            insertedMagazine.onUnInsert();
        }
        if (mag == null) {
            insertedMagazine = null;
            return;
        }
        mag.onInsert(this);
        insertedMagazine = mag;
    }

    public boolean hasMagazine(){
        return insertedMagazine != null;
    }

    public GunMagazine getMagazine(){
        return insertedMagazine;
    }

    public void fireBullet(){
        if (!isEquipped())
            return;
        if (insertedMagazine == null || insertedMagazine.getCurrentAmount() == 0){
            HandyHelper.instance.log("[Player("+ owner.getName()+"):fire] Not enough ammo (" + ((insertedMagazine == null) ? "no magazine in gun" : insertedMagazine.getCurrentAmount()) + ")");
            return;
        }
        insertedMagazine.onFire();
        bulletTempRotationVec.setAngleDeg(((Player)owner).itemRotation);
        gunSpriteBehaviour.onFire();
        new Bullet(TileResolver.getTile("bullet"), owner.getPosition(), bulletTempRotationVec);
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
            createSpriteBehaviour();
                //gunSpriteBehaviour = new GunSpriteBehaviour(GO, this, spriteWidth, spiteHeight, tile.getTextureRegion(), Globals.DEFAULT_RENDER_ORDER);

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
                        hud.debugEntries.put(stringID + "_ClickListener", "Pointing at " + stringID + " at " + getPosition());
                        hud.showItemInfoWindow(Gun.this);
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, @Null Actor toActor) {
                        super.exit(event, x, y, pointer, toActor);
                        hud.debugEntries.removeKey(stringID + "_ClickListener");
                        hud.hideItemInfoWindow(Gun.this);
                    }
                });
            }
            if (hud != null)
                gameStage.addActor(mouseHandler);
        }
    }

    protected void createSpriteBehaviour(){
        gunSpriteBehaviour = new GunSpriteBehaviour(GO, this, spriteWidth, spiteHeight, tile.getTextureRegion(), Globals.DEFAULT_RENDER_ORDER);
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
