package com.mygdx.game.gamestate.objects.items.guns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.objects.bullet.Bullet;
import com.mygdx.game.gamestate.objects.items.SimpleItem;
import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;

public class Gun extends SimpleItem {
    public Gun(TiledMapTile tile, String itemName) {
        super(tile, itemName);
        spriteBehaviour = new GunSpriteBehaviour(GO, 1f, 1f, tile.getTextureRegion(), Globals.DEFAULT_RENDER_ORDER);
        mouseHandler.setTouchable(Touchable.disabled);
    }

    public Gun(String tileName, String itemName) {
        super(tileName, itemName);
        spriteBehaviour = new GunSpriteBehaviour(GO, 1, 1, tile.getTextureRegion(), Globals.DEFAULT_RENDER_ORDER);
    }

    public void fireBullet(Player player){
        ((GunSpriteBehaviour)spriteBehaviour).fire();
        Vector3 mousePos = gameState.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 vv = new Vector2(mousePos.x-player.getPosition().x, mousePos.y-player.getPosition().y);
        new Bullet(TileResolver.getTile("bullet"), player.getPosition(), vv);
    }

    public void equip(Player player){
        ((GunSpriteBehaviour)spriteBehaviour).equip(player);
        GO.setEnabled(true);
    }
}
