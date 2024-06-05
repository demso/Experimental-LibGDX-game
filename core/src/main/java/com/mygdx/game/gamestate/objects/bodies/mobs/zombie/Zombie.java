package com.mygdx.game.gamestate.objects.bodies.mobs.zombie;

import com.mygdx.game.gamestate.player.Player;
import com.mygdx.game.gamestate.tiledmap.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.gamestate.Globals;
import com.mygdx.game.gamestate.factories.MobsFactory;
import com.mygdx.game.gamestate.objects.bodies.mobs.Entity;
import com.mygdx.game.net.messages.server.ZombieMove;
import dev.lyze.gdxUnBox2d.GameObject;
import lombok.Getter;
import lombok.Setter;

public class Zombie extends Entity {
    @Getter int damage = 4;
    transient public GameObject zombieObject;
    @Getter @Setter float maxSpeed = 1.5f;
    @Getter @Setter float maxAttackCoolDown = 1f;
    @Getter @Setter float attackCoolDown = 0;
    transient public ZombieAIBehaviour zombieHandler;
    @Getter @Setter private Player target;
    public Zombie(TiledMapTile tile, long id, World world, Vector2 position){
        setId(id);
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
        fixtureDef.filter.categoryBits = Globals.ZOMBIE_CONTACT_FILTER;
        fixtureDef.filter.maskBits = (short) (fixtureDef.filter.maskBits & ~Globals.LIGHT_CONTACT_FILTER);

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
        //new SoutBehaviour("zombieLogger", false, zombieObject);
    }

    @Override
    public String getName(){
        return "zombie";
    }

    @Override
    public void serverUpdate(ZombieMove move){
        zombieHandler.serverUpdate(move);
    }

    @Override
    public Object getData() {
        return this;
    }

    @Override
    public float hurt(float damage) {
        return super.hurt(damage);
    }

    public void attack(Entity entity){
        if (attackCoolDown <= 0 && entity.getKind() == Kind.PLAYER && entity.isAlive()) {
            entity.hurt(getDamage());
            attackCoolDown = maxAttackCoolDown;
            //SecondGDXGame.instance.helper.log("["+getName()+ "] Hurted "+ entity.getName() + ", entity hp: "+ entity.getHp());
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (zombieObject != null)
            zombieObject.destroy();
    }
}
