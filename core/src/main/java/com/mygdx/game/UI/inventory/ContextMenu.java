package com.mygdx.game.UI.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.UI.HUD;

public class ContextMenu extends Table {
    enum ConAction {
        PUT,
        DESCRIPTION
    }
    HUD hud;
    InventoryHUD invHUD;
    public InputListener hideListener;
    ItemEntry itemEntry;
    public ContextMenu(HUD h, InventoryHUD ih, ItemEntry iEntry, float x, float y){
        super(SecondGDXGame.skin);
        hud = h;
        invHUD = ih;
        itemEntry = iEntry;

        this.setBackground("default-pane");
        this.setSize(150,300);
        this.pad(5);
        this.align(Align.top);

        Button button = new Button(getSkin());
        button.setName("Inventory context menu \"Throw\" button");

        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                invHUD.contextAction(ConAction.PUT, ContextMenu.this);
            }
        });

        Label label = new Label("Throw", getSkin());
        Label.LabelStyle ls = new Label.LabelStyle(label.getStyle());
        ls.font = SecondGDXGame.skin.getFont("default14font");
        label.setStyle(ls);
        button.add(label).expandX().align(Align.left);

        this.add(button).growX().align(Align.left);

        this.row().padTop(2);

        button = new Button(getSkin());
        button.setName("Inventory context menu \"Description\" button");

        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                invHUD.contextAction(ConAction.DESCRIPTION, ContextMenu.this);
            }
        });

        label = new Label("Description", getSkin());
        label.setStyle(ls);
        button.add(label).expandX().align(Align.left);

        this.add(button).growX().align(Align.left);

        this.row().padTop(2);

        button = new Button(getSkin());
        button.setName("Inventory context menu \"Equip\" button");

        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                invHUD.contextAction(ConAction.DESCRIPTION, ContextMenu.this);
                invHUD.player.equipItem(itemEntry.item);
                invHUD.closeItemContextMenu(ContextMenu.this);
            }
        });

        label = new Label("Equip", getSkin());
        label.setStyle(ls);
        button.add(label).expandX().align(Align.left);

        this.add(button).growX().align(Align.left);

        pack();

        //to avoid sending event to gameStage
        addListener(new InputListener(){
            @Override
            public boolean handle(Event e){
                super.handle(e);
                return true;
            }
        });


        hideListener = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                Actor target = event.getTarget();
                if (isAscendantOf(target)) return false;
                invHUD.closeItemContextMenu(ContextMenu.this);
                return false;
            }
        };

        this.setPosition(x, y, Align.topLeft);
        hud.addCaptureListener(hideListener);
    }
}
