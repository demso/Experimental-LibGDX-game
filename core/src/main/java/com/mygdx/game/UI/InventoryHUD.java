package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.*;

public class InventoryHUD extends Table {
    ImageButton itemButton;
    public InventoryHUD(Player player, float x, float y){
        super(SecondGDXGame.skin);
        this.setBackground("default-pane");
        this.setSize(400,300);

        for (Item curItem : player.getInventoryItems()){
            itemButton = new ImageButton(new TextureRegionDrawable(curItem.tile.getTextureRegion()));
            itemButton.getImage().setScaling(Scaling.fill);
            itemButton.getImageCell().minSize(20).pad(0,0,0,10).align(Align.left);
            itemButton.align(Align.left);
            Label itemName = new Label(curItem.itemName, SecondGDXGame.skin);
            Label.LabelStyle ls = new Label.LabelStyle(itemName.getStyle());
            ls.font = SecondGDXGame.skin.getFont("default14font");
            itemName.setStyle(ls);
            itemButton.add(itemName).expandX().align(Align.center);
            add(itemButton).growX().align(Align.left);

            row();
        }

        this.align(Align.top);
        this.pad(5);
        //this.pack();
        this.setPosition(x,y-getHeight());
    }
}
