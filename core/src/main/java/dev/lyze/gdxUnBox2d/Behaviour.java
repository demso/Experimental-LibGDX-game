package dev.lyze.gdxUnBox2d;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class Behaviour {
    @Getter private final GameObject gameObject;

    private float renderOrder, executionOrder;

    /**
     * Lifecycle state of the behaviour.
     */
    @Getter @Setter(AccessLevel.PACKAGE) private BehaviourState state = BehaviourState.NOT_IN_SYSTEM;

    public Behaviour(GameObject gameObject) {
        this.gameObject = gameObject;

        gameObject.getUnBox().addBehaviour(this);
    }

    /**
     * This function is always called right after adding a behaviour to a game
     * object in the same frame even when the game object is disabled.
     */
    public abstract void awake();

    /**
     * This function is called before the first frame update when the script is
     * enabled.
     */
    public abstract void start();

    /**
     * This method runs at a fixed delta time unrelated to framerate, therefore it
     * can be executed multiple times a frame if needed.
     * All physics calculations and other behaviour update methods occur immediately
     * after fixedUpdate.
     */
    public abstract void fixedUpdate();

    /**
     * Update is called once a frame.
     *
     * @param delta The delta time from last to current frame.
     *              {@link Graphics#getDeltaTime()}
     */
    public abstract void update(float delta);

    /**
     * This method is called once per frame after {@link Behaviour#update(float)} is
     * finished.
     * For example, you can use this to adjust the cameras position after a players
     * position changed.
     *
     * @param delta The delta time from last to current frame.
     *              {@link Graphics#getDeltaTime()}
     */
    public abstract void lateUpdate(float delta);

    /**
     *
     * @param other       The other object this object collides with.
     * @param contact     Infos about the collision.
     * @param oldManifold {@link Manifold}
     * @return True to cancel running the event
     */

    public abstract boolean onCollisionPreSolve(Behaviour other, Contact contact, Manifold oldManifold);

    /**
     *
     * @param other   The other object this object collides with.
     * @param contact Infos about the collision.
     * @param impulse {@link ContactImpulse}
     * @return True to cancel running the event
     */
    public abstract boolean onCollisionPostSolve(Behaviour other, Contact contact, ContactImpulse impulse);

    /**
     * This method gets called when a Box2D collision happens with this object.
     *
     * @param other   The other object this object collides with.
     * @param contact Infos about the collision.
     */
    public abstract void onCollisionEnter(Behaviour other, Contact contact);

    /**
     * This method gets continuously called once per fixed update when a collision
     * still occurs.
     *
     * @param other The other object this object collides with.
     */
    public abstract void onCollisionStay(Behaviour other);

    /**
     * This method gets called when a Box2D collision stops with this object.
     *
     * @param other   The other object this object stopped colliding with.
     * @param contact Infos about the collision.
     */
    public abstract void onCollisionExit(Behaviour other, Contact contact);

    /**
     * A method which draws the behaviour onto the screen once a frame.
     *
     * @param batch The batch which is used to draw the behaviour.
     */
    public abstract void render(Batch batch);

    /**
     * A method which uses a {@link ShapeRenderer} to draw the behaviour to the
     * screen once a frame.
     * Therefore, most commonly used to draw debug infos.
     *
     * @param render The shape renderer which is used to draw the behaviour.
     */
    public abstract void debugRender(ShapeRenderer render);

    /**
     * A method which uses a {@link ShapeDrawer} to draw the behaviour to the screen
     * once a frame.
     * Therefore, most commonly used to draw debug infos.
     *
     * @param drawer The shape renderer which is used to draw the behaviour.
     */
    public abstract void debugRender(ShapeDrawer drawer);

    /**
     * When a game object gets enabled with {@link GameObject#setEnabled(boolean)}
     * this method gets immediately called.
     */
    public abstract void onEnable();

    /**
     * This method gets called in three ways:
     * <ul>
     * <li>When a game object gets disabled with
     * {@link GameObject#setEnabled(boolean)} this method gets immediately
     * called.</li>
     * <li>When a game object gets destroyed and the game object is enabled.</li>
     * <li>When a game object is enabled and this behaviour got removed from that
     * game object.</li>
     * </ul>
     */
    public abstract void onDisable();

    /**
     * This function is called when the behaviour gets cleaned up in
     * {@link UnBox#postRender()}.
     * For example if you {@link UnBox#destroy(GameObject)} the game object or the
     * behaviour itself.
     */
    public abstract void onDestroy();

    /**
     * Marks the behaviour for deletion at the end of the current frame.
     */
    public void destroy() {
        getGameObject().getUnBox().destroy(this);
    }

    public UnBox getUnBox() {
        return getGameObject().getUnBox();
    }

    /**
     * Set how late this behaviour gets drawn.
     *
     * @param renderOrder The bigger the number the later the behaviour gets drawn.
     */
    public void setRenderOrder(float renderOrder) {
        this.renderOrder = renderOrder;

        getUnBox().invalidateRenderOrder();
    }

    /**
     * Returns how late this behaviour gets drawn.
     *
     * @return The bigger the number the later the behaviour gets drawn.
     */
    public float getRenderOrder() {
        return renderOrder;
    }

    /**
     * Sets how late this behaviour gets executed in the update methods per game
     * object.
     *
     * @param executionOrder The bigger the number the later the behaviour gets
     *                       drawn.
     */
    public void setExecutionOrder(float executionOrder) {
        this.executionOrder = executionOrder;

        getGameObject().invalidateExecutionOrder();
    }

    /**
     * Returns how late this behaviour gets executed in the update methods per game
     * object.
     *
     * @return The bigger the number the later the behaviour gets drawn.
     */
    public float getExecutionOrder() {
        return executionOrder;
    }
}
