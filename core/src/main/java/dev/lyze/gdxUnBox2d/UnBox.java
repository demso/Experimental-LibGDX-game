package dev.lyze.gdxUnBox2d;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import dev.lyze.gdxUnBox2d.options.Box2dPhysicsOptions;
import java.util.Comparator;
import lombok.Getter;
import space.earlygrey.shapedrawer.ShapeDrawer;

// https://docs.unity3d.com/Manual/ExecutionOrder.html

/**
 * <p>
 * The main object of the library.
 * Holds all game objects and behaviours.
 * Additionally, calculates physics with Box2D.
 * </p>
 * <p>
 * Make sure to call {@link UnBox#preRender(float)},
 * {@link UnBox#render(Batch)}, {@link UnBox#postRender()} in your
 * {@link ApplicationListener#render()} loop in this order.
 * </p>
 */
public class UnBox {
    @Getter private final Box2dPhysicsOptions options = new Box2dPhysicsOptions();
    @Getter private final World world;

    private final OrderedMap<Body, Box2dBehaviour> bodyReferences = new OrderedMap<>();
    private final Box2dWorldContactListener contactListener;

    final OrderedMap<GameObject, Array<Behaviour>> gameObjects = new OrderedMap<>();
    final Array<Behaviour> behavioursToRender = new Array<>();
    private boolean invalidateRenderOrder;

    private final Array<GameObject> gameObjectsToAdd = new Array<>();
    private final Array<GameObject> gameObjectsToDestroy = new Array<>();

    final Array<Behaviour> behavioursToAdd = new Array<>();
    private final Array<Behaviour> behavioursToDestroy = new Array<>();

    /**
     * Instantiates an instance of this object with a Box2D World with (0, 0)
     * gravity.
     */
    public UnBox() {
        this(new World(new Vector2(0, 0), true));
    }

    /**
     * Instantiates an instance of this object.
     */
    public UnBox(World world) {
        this.world = world;
        world.setContactListener(contactListener = new Box2dWorldContactListener(this));
    }

    /**
     * Starts behaviours, calls {@link UnBox#fixedUpdate(float)},
     * {@link UnBox#updateGameObjects(float)} and
     * {@link UnBox#lateUpdateGameObjects(float)}.o
     * Call this at the beginning of your render loop.
     *
     * @param delta The delta time compared to the previous time.
     *              {@link Graphics#getDeltaTime()}
     */
    public void preRender(float delta) {
        startBehaviours();

        adjustExecutionOrder();

        fixedUpdate(delta);
        updateGameObjects(delta);
        lateUpdateGameObjects(delta);

        adjustRenderOrder();
    }

    public void adjustExecutionOrder() {
        for (int i = 0; i < gameObjects.orderedKeys().size; i++) {
            var gameObject = gameObjects.orderedKeys().get(i);

            if (gameObject.isInvalidateExecutionOrder()) {
                var behaviours = gameObjects.get(gameObject);

                behaviours.sort(Comparator.comparing(Behaviour::getExecutionOrder));
            }
        }
    }

    private void adjustRenderOrder() {
        if (!invalidateRenderOrder)
            return;

        invalidateRenderOrder = false;

        behavioursToRender.clear();

        for (int key = 0; key < gameObjects.orderedKeys().size; key++) {
            var gameObject = gameObjects.orderedKeys().get(key);
            var behaviours = gameObjects.get(gameObject);

            for (var i = 0; i < behaviours.size; i++) {
                var behaviour = behaviours.get(i);

                behavioursToRender.add(behaviour);
            }
        }

        behavioursToRender.sort(Comparator.comparing(Behaviour::getRenderOrder));
    }

    /**
     * Renders behaviours. Call this after {@link UnBox#preRender(float)}.
     *
     * @param batch The batch used to render the behaviours.
     */
    public void render(Batch batch) {
        renderGameObjects(batch);
    }

    /**
     * Renders behaviours with a shape renderer, therefore commonly used for
     * debugging. Call this after {@link UnBox#preRender(float)}.
     *
     * @param renderer The shape renderer used to render the behaviours.
     */
    public void debugRender(ShapeRenderer renderer) {
        debugRenderGameObjects(renderer);
    }

    /**
     * Renders behaviours with a shape drawer, therefore commonly used for
     * debugging. Call this after {@link UnBox#preRender(float)}.
     *
     * @param drawer The shape drawer used to render the behaviours.
     */
    public void debugRender(ShapeDrawer drawer) {
        debugRenderGameObjects(drawer);
    }

    /**
     * Instantiates and destroys pending game objects or behaviours. Call this after
     * {@link UnBox#render(Batch)}.
     */
    public void postRender() {
        instantiateGameObjects();
        instantiateBehaviours();

        destroyGameObjects();
        destroyBehaviours();

        for (int i = gameObjects.orderedKeys().size - 1; i >= 0; i--) {
            var gameObject = gameObjects.orderedKeys().get(i);
            var behaviours = gameObjects.get(gameObject);

            for (int j = behaviours.size - 1; j >= 0; j--)
                if (behaviours.get(j).getState() == BehaviourState.DESTROYED)
                    behaviours.removeIndex(j);

            if (gameObject.getState() == GameObjectState.DESTROYED)
                gameObjects.removeIndex(i);
        }
    }

    private void startBehaviours() {
        for (int key = 0; key < gameObjects.orderedKeys().size; key++) {
            var gameObject = gameObjects.orderedKeys().get(key);

            if (gameObject.isEnabled()) {
                var behaviours = gameObjects.get(gameObject);

                for (var i = 0; i < behaviours.size; i++) {
                    var behaviour = behaviours.get(i);

                    if (behaviour.getState() == BehaviourState.AWAKENED) {
                        behaviour.start();
                        behaviour.setState(BehaviourState.ALIVE);
                    }
                }
            }
        }
    }

    private float accumulator = 0;

    private void fixedUpdate(float delta) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(delta, options.getMaxFixedFrameTime());
        accumulator += frameTime;
        while (accumulator >= options.getTimeStep()) {
            var timeStep = options.getTimeStep();

            updateFixedObjects();

            getWorld().step(timeStep, options.getVelocityIteration(), options.getPositionIterations());
            contactListener.update();

            accumulator -= timeStep;
        }

        if (options.isInterpolateMovement()) {
            interpolateMovement(accumulator);
        }
    }

    private final Array<Body> tempBodies = new Array<>();

    // https://web.archive.org/web/20230415221545/https://gamengineering.blogspot.com/2018/07/libgdx-tutorial-fix-your-time-step.html
    public void interpolateMovement(float accumulator) {
        float alpha = accumulator / options.getTimeStep();

        getWorld().getBodies(tempBodies);

        for (int i = 0; i < tempBodies.size; i++) {
            var body = tempBodies.get(i);

            if (body.getType() == BodyDef.BodyType.StaticBody)
                continue;

            var previousPos = body.getPosition();
            var previousAngle = body.getAngle();

            var posX = previousPos.x * (1f - alpha) + previousPos.x * alpha;
            var posY = previousPos.y * (1f - alpha) + previousPos.y * alpha;
            var angle = previousAngle * (1f - alpha) + previousAngle * alpha;

            body.setTransform(posX, posY, angle);
        }
    }

    private void updateFixedObjects() {
        for (int key = 0; key < gameObjects.orderedKeys().size; key++) {
            var gameObject = gameObjects.orderedKeys().get(key);

            if (gameObject.isEnabled()) {
                var behaviours = gameObjects.get(gameObject);

                for (var i = 0; i < behaviours.size; i++)
                    behaviours.get(i).fixedUpdate();
            }
        }
    }

    private void updateGameObjects(float delta) {
        for (int key = 0; key < gameObjects.orderedKeys().size; key++) {
            var gameObject = gameObjects.orderedKeys().get(key);

            if (gameObject.isEnabled()) {
                var behaviours = gameObjects.get(gameObject);

                for (var i = 0; i < behaviours.size; i++)
                    behaviours.get(i).update(delta);
            }
        }
    }

    private void lateUpdateGameObjects(float delta) {
        for (int key = 0; key < gameObjects.orderedKeys().size; key++) {
            var gameObject = gameObjects.orderedKeys().get(key);

            if (gameObject.isEnabled()) {
                var behaviours = gameObjects.get(gameObject);

                for (var i = 0; i < behaviours.size; i++)
                    behaviours.get(i).lateUpdate(delta);
            }
        }
    }

    private void renderGameObjects(Batch batch) {
        for (int i = 0; i < behavioursToRender.size; i++) {
            var behaviour = behavioursToRender.get(i);

            if (behaviour.getGameObject().isEnabled())
                behaviour.render(batch);
        }
    }

    private void debugRenderGameObjects(ShapeRenderer renderer) {
        for (int i = 0; i < behavioursToRender.size; i++) {
            var behaviour = behavioursToRender.get(i);

            if (behaviour.getGameObject().isEnabled())
                behaviour.debugRender(renderer);
        }
    }

    private void debugRenderGameObjects(ShapeDrawer drawer) {
        for (int i = 0; i < behavioursToRender.size; i++) {
            var behaviour = behavioursToRender.get(i);

            if (behaviour.getGameObject().isEnabled())
                behaviour.debugRender(drawer);
        }
    }

    private void instantiateGameObjects() {
        for (int i = 0; i < gameObjectsToAdd.size; i++) {
            var gameObject = gameObjectsToAdd.get(i);

            gameObjects.put(gameObject, new Array<>());
            gameObject.setState(GameObjectState.ALIVE);

            invalidateRenderOrder = true;
        }

        gameObjectsToAdd.clear();
    }

    private void instantiateBehaviours() {
        for (int i = 0; i < behavioursToAdd.size; i++) {
            var behaviour = behavioursToAdd.get(i);

            gameObjects.get(behaviour.getGameObject()).add(behaviour);

            behaviour.awake();
            behaviour.setState(BehaviourState.AWAKENED);

            if (behaviour.getGameObject().isEnabled())
                behaviour.onEnable();

            invalidateRenderOrder = true;
            behaviour.getGameObject().invalidateExecutionOrder();
        }

        behavioursToAdd.clear();
    }

    private void destroyBehaviours() {
        for (int i = 0; i < behavioursToDestroy.size; i++) {
            var behaviour = behavioursToDestroy.get(i);

            if (behaviour.getGameObject().isEnabled())
                behaviour.onDisable();

            behaviour.onDestroy();
            behaviour.setState(BehaviourState.DESTROYED);

            invalidateRenderOrder = true;
            behaviour.getGameObject().invalidateExecutionOrder();
        }

        behavioursToDestroy.clear();
    }

    private void destroyGameObjects() {
        for (int i = 0; i < gameObjectsToDestroy.size; i++) {
            var gameObject = gameObjectsToDestroy.get(i);

            var behaviours = gameObjects.get(gameObject);
            for (int j = 0; j < behaviours.size; j++)
                destroy(behaviours.get(j));

            gameObject.setState(GameObjectState.DESTROYED);

            invalidateRenderOrder = true;
        }

        gameObjectsToDestroy.clear();
    }

    void addGameObject(GameObject go) {
        go.setState(GameObjectState.ADDING);

        gameObjectsToAdd.add(go);
    }

    void addBehaviour(Behaviour behaviour) {
        behaviour.setState(BehaviourState.AWAKING);

        behavioursToAdd.add(behaviour);
    }

    /**
     * Marks the behaviour for deletion at the end of the current frame.
     *
     * @param behaviour The behaviour instance to remove.
     */
    public void destroy(Behaviour behaviour) {
        if (behaviour.getState() == BehaviourState.DESTROYING || behaviour.getState() == BehaviourState.DESTROYED)
            return;

        behaviour.setState(BehaviourState.DESTROYING);

        behavioursToDestroy.add(behaviour);
    }

    /**
     * Marks the game object and all its behaviour for deletion at the end of the
     * current frame.
     *
     * @param go The behaviour instance to remove.
     */
    public void destroy(GameObject go) {
        if (go.getState() == GameObjectState.DESTROYING || go.getState() == GameObjectState.DESTROYED)
            return;

        go.setState(GameObjectState.DESTROYING);

        var behaviours = gameObjects.get(go);
        for (int i = 0; i < behaviours.size; i++)
            destroy(behaviours.get(i));

        gameObjectsToDestroy.add(go);
    }

    /**
     * Finds all behaviour instances with the specified type. Allocations an array
     * inside this method.
     *
     * @param behaviourClass The class type we want to search for.
     * @return The found behaviour or null.
     */
    public <T extends Behaviour> Array<T> findBehaviours(Class<T> behaviourClass) {
        return findBehaviours(behaviourClass, new Array<T>());
    }

    /**
     * Finds all behaviour instances with the specified type.
     *
     * @param behaviourClass The class type we want to search for.
     * @param tempStorage    A temporary array to store all behaviours in it.
     *                       Therefore, there's no array allocation happening in
     *                       this method.
     * @return All found behaviours or empty array.
     */
    public <T extends Behaviour> Array<T> findBehaviours(Class<T> behaviourClass, Array<T> tempStorage) {
        tempStorage.clear();

        for (int i = 0; i < behavioursToAdd.size; i++)
            if (behavioursToAdd.get(i).getClass().equals(behaviourClass))
                tempStorage.add((T) behavioursToAdd.get(i));

        for (int key = 0; key < gameObjects.orderedKeys().size; key++) {
            var gameObject = gameObjects.orderedKeys().get(key);
            var behaviours = gameObjects.get(gameObject);

            for (int i = 0; i < behaviours.size; i++) {
                var behaviour = behaviours.get(i);

                if (behaviour.getClass().equals(behaviourClass))
                    tempStorage.add((T) behaviour);
            }
        }

        return tempStorage;
    }

    /**
     * When a behaviours render order gets updated unBox doesn't get notified about
     * that, hence you need to call this method afterwards.
     */
    public void invalidateRenderOrder() {
        invalidateRenderOrder = true;
    }

    Body createObject(Behaviour behaviour, BodyDef objectToAdd) {
        var body = getWorld().createBody(objectToAdd);
        bodyReferences.put(body, (Box2dBehaviour) behaviour);

        return body;
    }

    Body overrideObject(Behaviour behaviour, Body object) {
        bodyReferences.put(object, (Box2dBehaviour) behaviour);

        return object;
    }

    void destroyObject(Body obj) {
        contactListener.destroy(bodyReferences.remove(obj));
        getWorld().destroyBody(obj);
    }

    /**
     * Helper method to find the behaviour based on a Box2D Body.
     *
     * @param bodyToFind The Box2D body to search for.
     * @return The GameObject if found, or null.
     */
    public Behaviour findBehaviour(Body bodyToFind) {
        for (int i = 0; i < bodyReferences.orderedKeys().size; i++) {
            var body = bodyReferences.orderedKeys().get(i);
            if (body.equals(bodyToFind))
                return bodyReferences.get(body);
        }

        return null;
    }
}
