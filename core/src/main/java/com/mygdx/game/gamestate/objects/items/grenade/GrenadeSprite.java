package com.mygdx.game.gamestate.objects.items.grenade;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.net.messages.client.GrenadeInfo;
import dev.lyze.gdxUnBox2d.GameObject;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GrenadeSprite extends SpriteBehaviour {
    ShapeDrawer shapeDrawer;
    public GrenadeSprite(GameObject gameObject, float width, float height, TextureRegion textureRegion, float renderOrder) {
        super(gameObject, width, height, textureRegion, renderOrder);

    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
        //Vector2 vec = getBodyPosition();
        //GameState.instance.shapeDrawer.circle(vec.x, vec.y, getUserData(Grenade.class).radius);
    }
}
