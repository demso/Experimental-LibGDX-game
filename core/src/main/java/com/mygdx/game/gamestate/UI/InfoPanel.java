package com.mygdx.game.gamestate.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.HandyHelper;
import com.mygdx.game.gamestate.UI.console.sjconsole.LogLevel;
import com.mygdx.game.gamestate.objects.items.Item;
import com.mygdx.game.gamestate.objects.items.guns.Gun;
import com.mygdx.game.gamestate.player.ClientPlayer;
import com.mygdx.game.gamestate.tiledmap.loader.TileResolver;
import lombok.Setter;

public class InfoPanel extends Group {
    Label hpLabel;
    Label ammoLabel;
    Image hpImage;
    Image ammoImage;
    Skin skin = SecondGDXGame.instance.skin1x;
    Skin skin2x = SecondGDXGame.instance.skin;
    HUD hud;
    ClientPlayer player;

    public void update(float delta){
        if (player == null){
            HandyHelper.instance.log("[InfoPanel:update] Player is null", LogLevel.ERROR);
            return;
        }
        if (player.isAlive()) {
            Item item = player.equipedItem;
            if (item instanceof Gun gun && gun.hasMagazine()){
                ammoLabel.setText(gun.getMagazine().getCurrentAmount() + "");
                showAmmo(true);
            } else {
                showAmmo(false);
            }
            hpLabel.setText(Math.round(player.getHp()) + "");
        } else {
            showAmmo(false);
            showHP(false);
        }
    }

    public void showHP(boolean is){
        hpLabel.setVisible(is);
        hpImage.setVisible(is);
    }
    public void showAmmo(boolean is){
        ammoLabel.setVisible(is);
        ammoImage.setVisible(is);
    }

    public void refresh(){
        player = hud.clientPlayer;
    }

    public InfoPanel(HUD hud) {
        this.hud = hud;


        hpLabel = new Label(11 + "", skin2x);
        hpLabel.setColor(Color.WHITE);
        hpLabel.setPosition(46, 45);

        ammoLabel = new Label(10 + "", skin);
        ammoLabel.setPosition(105, 25);


        hpImage = new Image(new Texture("visual/textures/heart_icon.png"));
        hpImage.setSize(100, 100);
        hpImage.setPosition(12, 10);

        ammoImage = new Image(TileResolver.getTile("pistol_magazine").getTextureRegion());
        ammoImage.setSize(60, 60);
        ammoImage.setPosition(65, 7);

        addActor(hpImage);
        addActor(ammoImage);
        this.setSize(300, 200);
        addActor(hpLabel);
        addActor(ammoLabel);
    }
}
