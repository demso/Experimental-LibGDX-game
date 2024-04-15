package com.mygdx.game.gamestate.objects.bodies.mobs.zombie;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.gamestate.GameState;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.SecondGDXGame;
import com.mygdx.game.gamestate.objects.behaviours.SpriteBehaviour;
import com.mygdx.game.gamestate.factories.MobsFactory;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import lombok.Getter;
import lombok.Setter;

public class Zombie extends Entity {
    @Getter int damage = 4;
    GameObject zombieObject;
    @Getter @Setter float speed = 10f;
    @Getter @Setter float maxAttackCoolDown = 1f;
    @Getter @Setter float attackCoolDown = 0;
    public Zombie(TiledMapTile tile, World world, Vector2 position){
        setFriendliness(Friendliness.HOSTILE);
        setHp(10);
        setMaxHp(10);

        BodyDef bodyDef = MobsFactory.bodyDef(position.x, position.y, BodyDef.BodyType.DynamicBody);

        CircleShape circle = new CircleShape();
        circle.setRadius(0.2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.01f;
        fixtureDef.restitution = 0.01f;
        fixtureDef.filter.categoryBits = Globals.ZOMBIE_CF;
        fixtureDef.filter.maskBits = (short) (fixtureDef.filter.maskBits & ~Globals.LIGHT_CF);

        setBody(world.createBody(bodyDef));
        getBody().createFixture(fixtureDef);
        getBody().setFixedRotation(true);

        MassData massData = new MassData();
        massData.mass = 60f;
        massData.center.set(new Vector2(0f,0f));

        getBody().setMassData(massData);
        getBody().setLinearDamping(10);
        getBody().setUserData(this);
        circle.dispose();

        zombieObject = new GameObject(getName(), GameState.Instance.unbox);

        new Box2dBehaviour(getBody(), zombieObject);
        new SpriteBehaviour(zombieObject, tile.getTextureRegion(), Globals.ZOMBIE_RO);
        new ZombieCollisionBehaviour(zombieObject);
        new ZombieAIBehaviour(zombieObject);
        //new SoutBehaviour("zombieLogger", false, zombieObject);
    }

    @Override
    public String getName(){
        return "zombie";
    }

    @Override
    public Object getData() {
        return this;
    }

    @Override
    public int hurt(int damage) {
        System.out.println("Hurting zombie");
        return super.hurt(damage);
    }

    public void attack(Entity entity){
        if (attackCoolDown <= 0 && entity.getKind() == Kind.PLAYER && entity.isAlive()) {
            entity.hurt(getDamage());
            attackCoolDown = maxAttackCoolDown;
            SecondGDXGame.helper.log("["+getName()+ "] Hurted "+ entity.getName() + ", entity hp: "+ entity.getHp());
        }
    }

    @Override
    public void kill() {
        zombieObject.destroy();
    }
}
