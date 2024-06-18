package com.mygdx.game.gamestate.UI.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.objects.items.Item;

public class ItemEntry extends Button {

    ItemEntry itemEntry;
    StorageInventoryHUD inventoryHUD;
    Item item;

    ItemEntry(StorageInventoryHUD iHUD, Item item){
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
                super.clicked(e, x, y);
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    if (inventoryHUD instanceof PlayerInventoryHUD pihud)
                        pihud.storeAction(item);
                    else
                        inventoryHUD.takeAction(item);
                else
                    inventoryHUD.showItemContextMenu(itemEntry);
            }
        });
    }
}
