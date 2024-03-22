package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.*;

public class InventoryHUD extends Table {
    Button itemButton;
    Player player;
    public InventoryHUD(Player player, float x, float y){
        super(SecondGDXGame.skin);
        this.player = player;
        this.setTouchable(Touchable.enabled);
        this.setBackground("default-pane");
        this.setSize(400,300);

        refill();

        this.align(Align.top);
        this.pad(5);
        this.setPosition(x,y-getHeight());
    }

    public void refill(){
        this.clear();
        for (Item curItem : player.getInventoryItems()){
            itemButton = new Button(SecondGDXGame.skin);
            itemButton.align(Align.left);

            Image img = new Image(new TextureRegionDrawable(curItem.tile.getTextureRegion()));
            img.setScaling(Scaling.fill);
            itemButton.add(img).minSize(20).pad(0,0,0,10).align(Align.left);

            Label itemName = new Label(curItem.itemName, SecondGDXGame.skin);
            Label.LabelStyle ls = new Label.LabelStyle(itemName.getStyle());
            ls.font = SecondGDXGame.skin.getFont("default14font");
            itemName.setStyle(ls);
            itemButton.add(itemName).expandX().align(Align.left);

            add(itemButton).growX().align(Align.left);

            row();
        }
    }
}
