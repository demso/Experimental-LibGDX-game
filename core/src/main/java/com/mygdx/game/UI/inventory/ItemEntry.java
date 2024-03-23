package com.mygdx.game.UI.inventory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.Item;
import com.mygdx.game.SecondGDXGame;

public class ItemEntry extends Button {

    ItemEntry itemEntry;
    InventoryHUD inventoryHUD;
    Item item;

    ItemEntry(InventoryHUD iHUD, Item item){
        super(SecondGDXGame.skin);
        itemEntry = this;
        inventoryHUD = iHUD;
        this.item = item;

        setName("Inventory button for item \""+ item.itemName +"\"");

        align(Align.left);

        Image img = new Image(new TextureRegionDrawable(item.tile.getTextureRegion()));
        img.setScaling(Scaling.fill);
        add(img).minSize(20).pad(0,0,0,10).align(Align.left);

        Label itemName = new Label(item.itemName, SecondGDXGame.skin);
        Label.LabelStyle ls = new Label.LabelStyle(itemName.getStyle());
        ls.font = SecondGDXGame.skin.getFont("default14font");
        itemName.setStyle(ls);
        add(itemName).expandX().align(Align.left);
        addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                itemEntry.getClickListener().clicked(e, x, y);
                inventoryHUD.showItemContextMenu(itemEntry);
            }
        });
    }
}
