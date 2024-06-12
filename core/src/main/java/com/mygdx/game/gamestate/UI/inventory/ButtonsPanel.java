package com.mygdx.game.gamestate.UI.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.UI.HUD;
import com.mygdx.game.gamestate.objects.items.Item;

import java.util.Arrays;
import java.util.Comparator;

public class ButtonsPanel extends Table {
    public enum Action{
        TakeAll(0, "Take all"), StoreAll(1, "Store all");

        public final float order;
        public final String name;

        Action(float order, String nam) {
            name = nam;
            this.order = (int) order;
        }

        public int compare(Action action) {
            int ret = -1;
            if (action.order > this.order) ret = 1;
            if (action.order == this.order) ret = 0;
            return ret;
        }


        @Override
        public String toString() {
            return name;
        }
    }

    OrderedMap<Action, Button> allActions = new OrderedMap<>();
    OrderedMap<Action, Button> actions = new OrderedMap<>();

    Skin skin;
    Label.LabelStyle ls;
    HUD hud;
    public ButtonsPanel(HUD hud, Action... actions) {
        super(SecondGDXGame.skin);
        this.hud = hud;
        skin = getSkin();
        this.setBackground("default-pane");
        this.pad(5);
        this.align(Align.top);
        setName("Inventory tools buttons panel");

        ls = new Label.LabelStyle(new Label("", getSkin()).getStyle());
        ls.font = SecondGDXGame.skin.getFont("default14font");

        addListener(new InputListener(){
            @Override
            public boolean handle(Event e){
                super.handle(e);
                return true;
            }
        });

        createActions(actions);

    }

    public void createActions(Action... actions){
        Arrays.sort(actions, Action::compare);
        for (Action action : actions) {
            createButton(action);
        }
    }

    Button createButton(Action action){
        Button button = new Button(skin);
        button.setName("Buttons panel \"" + action.toString()+ "\" button");
        button.addListener(createListener(action));

        Label label = new Label(action.toString(), getSkin());
        label.setAlignment(Align.center, Align.center);

        label.setStyle(ls);
        button.add(label).minWidth(80).expandX().align(Align.left);

        addButton(button);

        actions.put(action, button);
        allActions.put(action,button);

//        index++;

        return button;
    }

    void addButton(Button button){
        add(button).growX().align(Align.left);
        this.row().padTop(2);

        pack();
    }

    ClickListener createListener(Action action){
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                action(action);
            }
        };
    }

    void action(Action action){
        switch (action){
            case TakeAll:
                Item[] items = hud.storageInventoryHUD.getStorage().getInventoryItems().toArray(Item.class);
                for (Item item : items){
                    hud.storageInventoryHUD.takeAction(item);
//                    try {
//                        Thread.yield();
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
                }
                break;
            case StoreAll:
                items = hud.playerInventoryHud.getStorage().getInventoryItems().toArray(Item.class);
                for (Item item : items)
                    hud.playerInventoryHud.storeAction(item);
//                try {
//                    Thread.yield();
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
                break;
        }
    }

    public void disableActions(Action... actions){
        for (Action action : actions)
            this.actions.remove(action);
        validate();
    }

    public void enableActions(Action... actions){
        for (Action action : actions){
            if (allActions.get(action) == null)
                continue;
            this.actions.put(action, allActions.get(action));
        }
    }

    public void update(){
        clearChildren();

        var actionKeys = actions.orderedKeys();
        actionKeys.sort(Action::compare);

        for (Action action : actionKeys)
            addButton(actions.get(action));
//        layout();
        validate();
    }
}
