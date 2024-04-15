package com.mygdx.game.gamestate.UI;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.*;
import com.mygdx.game.gamestate.objects.Item;

public class ItemInfoPopUp extends Table {
    Image itemImage;
    Label itemName;
    VerticalGroup itemPropsVGroup;
    Label itemDesc;
    public ItemInfoPopUp(Item item, float x, float y){
        super(SecondGDXGame.skin);
        this.setBackground("default-pane");

        itemImage = new Image(item.tile.getTextureRegion());
        itemImage.setScaling(Scaling.fill);
        add(itemImage).minSize(50);

        itemName = new Label(item.itemName, SecondGDXGame.skin);
        itemName.setFontScale(0.45f);
        add(itemName);

        row();

        itemPropsVGroup = new VerticalGroup();;
        add(itemPropsVGroup).width(50);
        itemDesc = new Label(item.description, SecondGDXGame.skin);
        Label.LabelStyle ls = new Label.LabelStyle(itemDesc.getStyle());
        ls.font = SecondGDXGame.skin.getFont("default14font");
        itemDesc.setStyle(ls);
        itemDesc.setWrap(true);
        add(itemDesc).minWidth(130).maxWidth(130);

        this.align(Align.top);
        this.pad(5);
        this.pack();
        this.setPosition(x,y-getHeight());
    }
}
