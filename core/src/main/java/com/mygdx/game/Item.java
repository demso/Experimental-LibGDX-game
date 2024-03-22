package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Null;
import com.mygdx.game.UI.ItemInfoPopUp;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

import static net.dermetfan.gdx.physics.box2d.Box2DUtils.*;

public class Item extends Box2DSprite {
    public TiledMapTile tile;
    public Body physicalBody;
    public Table clickListener;
    public Item item;

    public GameItself gameItself;
    public String tileName = "Item name.";
    public String itemName = "10мм FMJ";
    public String description = "First you must develop a Skin that implements all the widgets you plan to use in your layout. You can't use a widget if it doesn't have a valid style. Do this how you would usually develop a Skin in Scene Composer.";
    public float spriteWidth = 0.7f;
    public float spiteHeight = 0.7f;
    private static final Vector2 vec2 = new Vector2();
    public Vector2 position;

    ItemInfoPopUp popup;

    Item(TiledMapTile tile){
        super(tile.getTextureRegion());
        this.tile = tile;
    }

    Item(TiledMapTile tile, GameItself gi, String itemName){
        super(tile.getTextureRegion());
        this.tile = tile;
        this.item = this;
        this.tileName = tile.getProperties().get("name", "no_name", String.class);
        this.gameItself = gi;
        this.itemName = itemName;
    }

    public Body allocate(World world, Vector2 position){
        BodyDef transparentBodyDef = new BodyDef();
        CircleShape transparentBox = new CircleShape();
        FixtureDef transparentFixtureDef = new FixtureDef();
        transparentBox.setRadius(0.2f);
        transparentFixtureDef.shape = transparentBox;
        transparentFixtureDef.filter.groupIndex = -10;

        transparentBodyDef.position.set(position);
        Body bb = world.createBody(transparentBodyDef);
        bb.createFixture(transparentFixtureDef);
        Filter filtr = bb.getFixtureList().get(0).getFilterData();
        filtr.maskBits = 0x0002;
        bb.getFixtureList().get(0).refilter();
        bb.setUserData(this);
        setPhysicalBody(bb);
        return bb;
    }
    public void pickup(){
        if (clickListener != null){
            gameItself.gameStage.getActors().removeValue(clickListener, true);
        }
        if (physicalBody != null){
            physicalBody.getWorld().destroyBody(physicalBody);
        }
    }

    public Body getPhysicalBody() {
        return physicalBody;
    }

    public void setPhysicalBody(Body physicalBody) {
        if(physicalBody != null) {
            this.physicalBody = physicalBody;
            position = physicalBody.getPosition();
            clickListener = new Table();
            clickListener.setSize(spriteWidth-0.1f,spiteHeight-0.1f);
            clickListener.setPosition(position.x-clickListener.getWidth()/2f, position.y-clickListener.getHeight()/2f);
            clickListener.setTouchable(Touchable.enabled);
            clickListener.addListener(new ClickListener(){
                @Override
                public void enter (InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                  //  gameItself.hudStage.getActors().removeValue(popup, true);
                    gameItself.debugEntries.put(tileName + "_ClickListener", "Pointing at "+tileName+ " at "+position);
                    gameItself.hudStage.showItemInfoWindow(item);
//                    Vector3 mousePosition = gameItself.hudStage.getCamera().unproject(new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0));
//                    popup = new ItemInfoPopUp(item,mousePosition.x,mousePosition.y);
//                    gameItself.hudStage.addActor(popup);
                }

                @Override
                public void exit (InputEvent event, float x, float y, int pointer, @Null Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    gameItself.debugEntries.removeKey(tileName + "_ClickListener");
                    gameItself.hudStage.hideItemInfoWindow(item);
                   // gameItself.hudStage.getActors().removeValue(popup, true);
                }
            });

            gameItself.gameStage.addActor(clickListener);
        }
    }

    public void clearPhysicalBody(){
        physicalBody = null;
    }

    @Override
    public void draw(Batch batch, Body body) {
        float width = width(body), height = height(body);
        vec2.set(minX(body) + width / 2, minY(body) + height / 2);
        vec2.set(body.getWorldPoint(vec2));
        draw(batch, vec2.x, vec2.y, spriteWidth, spiteHeight, body.getAngle());
    }
}
