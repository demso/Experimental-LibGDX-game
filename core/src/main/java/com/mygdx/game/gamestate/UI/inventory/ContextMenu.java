package com.mygdx.game.gamestate.UI.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.UI.HUD;

import java.util.Comparator;

public class ContextMenu extends Table {
    public enum ConAction {
        Drop, Store , Description, Equip, Take
    }
    public ObjectMap<ConAction, Button> allActions = new ObjectMap<>();//порядок и conaction
    public ObjectMap<ConAction, Button> actions = new ObjectMap<>();
    HUD hud;
    InventoryHUD inventory;
    public InputListener hideListener;
    ItemEntry itemEntry;
    Label.LabelStyle ls;
    String storageName;
    public ContextMenu(HUD hud, InventoryHUD ih, ConAction... actions){
        super(SecondGDXGame.skin);
        this.hud = hud;
        inventory = ih;
        storageName = inventory.getClass().equals(StorageInventoryHUD.class) ? "some storage" :
                inventory.getClass().equals(PlayerInventoryHUD.class) ? "player inventory" : "some other inventory";
        setName("Item context menu for " + storageName );

        this.setBackground("default-pane");
        this.setSize(150,300);
        this.pad(5);
        this.align(Align.top);

        ls = new Label.LabelStyle(new Label("", getSkin()).getStyle());
        ls.font = SecondGDXGame.skin.getFont("default14font");

        createEntries(actions);

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
                inventory.closeItemContextMenu(ContextMenu.this);
                return false;
            }
        };

        hud.addCaptureListener(hideListener);
    }

    public void setPosition(ItemEntry itemEntry, float x, float y) {
        super.setPosition(x, y, Align.topLeft);
        this.itemEntry = itemEntry;
    }

    int index = 0;
    Button createEntry(ConAction action){
        Button button = new Button(getSkin());
        button.setName("Inventory context menu \"" + action.toString()+ "\" button");
        button.addListener(createListener(action));

        Label label = new Label(action.toString(), getSkin());

        label.setStyle(ls);
        button.add(label).expandX().align(Align.left);
        button.setZIndex(index);

        addButton(button);

        actions.put(action, button);
        allActions.put(action,button);

        index++;

        return button;
    }

    void createEntries(ConAction... actions){
        for (ConAction action : actions){
            createEntry(action);
        }
    }

    ClickListener createListener(ConAction action){
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                inventory.contextAction(action, ContextMenu.this);
            }
        };
    }

    public void disableActions(ConAction... actions){
        for (ConAction action : actions)
            this.actions.remove(action);
        validate();
    }

    public void enableActions(ConAction... actions){
        for (ConAction action : actions){
            if (allActions.get(action) == null)
                continue;
            this.actions.put(action, allActions.get(action));
        }
    }

    private void addButton(Button button){
        this.add(button).growX().align(Align.left);

        this.row().padTop(2);

        pack();
    }

    public void update(){
        float oldWidth = getWidth();
        float oldHeight = getHeight();
        clearChildren();
        var buttons = actions.values().toArray();
        buttons.sort(Comparator.comparingInt(Actor::getZIndex));
        //SecondGDXGame.instance.helper.log(buttons.toString(", "), false);
        for (Button button : buttons)
            addButton(button);
        validate();
        super.setPosition(getX() - (getWidth() - oldWidth), getY() - (getHeight() - oldHeight), Align.bottomLeft);
    }

    @Override
    public String toString() {
        return "\n" + super.toString();
    }
}
