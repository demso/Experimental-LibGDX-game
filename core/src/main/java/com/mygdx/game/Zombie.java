package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.behaviours.SpriteBehaviour;
import com.mygdx.game.behaviours.collision.ZombieCollisionBehaviour;
import dev.lyze.gdxUnBox2d.Box2dBehaviour;
import dev.lyze.gdxUnBox2d.GameObject;
import lombok.Getter;
import lombok.Setter;

public class Zombie extends Entity{
    @Getter int damage = 4;
    GameObject zombieObject;
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
        fixtureDef.filter.categoryBits = GameItself.ZOMBIE_CF;
        fixtureDef.filter.maskBits = (short) (fixtureDef.filter.maskBits & ~GameItself.LIGHT_CF);

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);

        MassData massData = new MassData();
        massData.mass = 60f;
        massData.center.set(new Vector2(0f,0f));

        body.setMassData(massData);
        body.setLinearDamping(10);
        body.setUserData(this);
        circle.dispose();

        zombieObject = new GameObject(getName(), GameItself.unbox);

        new Box2dBehaviour(body, zombieObject);
        new SpriteBehaviour(zombieObject, tile.getTextureRegion(), GameConstants.ZOMBIE_RO);
        new ZombieCollisionBehaviour(zombieObject);
        //new SoutBehaviour("zombieLogger", false, zombieObject);
    }

    @Override
    public String getName(){
        return "zombie";
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
