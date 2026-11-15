package com.seven_gnomes.Systems;

import java.util.IdentityHashMap;
import java.util.Map;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.seven_gnomes.Components.Goose;
import com.seven_gnomes.Components.Move;
import com.seven_gnomes.Components.Timer;
import com.seven_gnomes.Components.Transform;
import com.seven_gnomes.GameMain;
import com.seven_gnomes.Assets.GooseFactory;
import com.seven_gnomes.Input.Controller;
import com.seven_gnomes.map.BuildingMap;

public class GooseSystem extends IteratingSystem implements Disposable {
    private Engine engine;

    private final Vector2 targetPosition = new Vector2();
    private final World physicsWorld;
    private final Map<Entity, Body> gooseBodies = new IdentityHashMap<>();
    private final Map<Entity, Body> playerBodies = new IdentityHashMap<>();
    private final Array<Body> mapBodies = new Array<>();

    private static final int MAX_GEESE = 100;
    private static final int INITIAL_SPAWN_COUNT = 5;
    private final Timer spawnTimer;
    private int waveCounter = 0;
    private int gooseSpawnCount = INITIAL_SPAWN_COUNT;
    private int totalGeeseSpawned = 0;
    private int activeGeese = 0;
    private boolean waveSpawned = false;
    private final TextureRegion gooseTexture;
    private final Array<Vector2> spawnPositions = new Array<>();


    public GooseSystem(Timer timer, TextureRegion gooseTexture) {
        super(Family.all(Goose.class, Transform.class, Move.class).get());
        Box2D.init();
        physicsWorld = new World(new Vector2(), true);
        this.spawnTimer = timer;
        this.gooseTexture = gooseTexture;
    }

    public void setMap(TiledMap map, BuildingMap buildingMap) {
        for (Body body : mapBodies) {
            physicsWorld.destroyBody(body);
        }
        mapBodies.clear();
        spawnPositions.clear();
        MapLayer spawnLayer = map.getLayers().get("spawns");

        for (MapObject obj: spawnLayer.getObjects()) {
            Object type = obj.getProperties().get("type");

            Rectangle bounds = ((RectangleMapObject) obj).getRectangle();
            Vector2 pos = new Vector2(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2).scl(GameMain.UNIT_SCALE);
            if ("goose_spawn".equals(type)) {
                spawnPositions.add(pos);
            }
        }

        for (Rectangle solid : buildingMap.getSolids()) {
            createMapFixture(solid);
        }
    }

    private void createMapFixture(Rectangle bounds) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = physicsWorld.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            bounds.width / 2f,
            bounds.height / 2f,
            new Vector2(bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f),
            0f
        );
        body.createFixture(shape, 0f);
        shape.dispose();
        mapBodies.add(body);
    }

    private void spawnGooseWave(int amount) {
        if (spawnPositions.size == 0) {
            throw new IllegalStateException("map has no goose spawn positions");
        }
        for (int i = 0; i < amount; i++) {
            Vector2 spawnPosition = spawnPositions.get(MathUtils.random(spawnPositions.size - 1));
            GooseFactory.createGoose(
                engine,
                gooseTexture,
                spawnPosition,
                2,
                new Vector2(24f * GameMain.UNIT_SCALE, 24f * GameMain.UNIT_SCALE),
                new Vector2(1f,1f)
            );
            totalGeeseSpawned++;
            activeGeese++;
        }
        gooseSpawnCount++;

        // Remove in final
        System.out.println("Spawned wave, total geese = " + totalGeeseSpawned);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.engine = engine;
    }

    @Override
    public void update(float deltaTime) {
        if (spawnTimer.secondTracker % 5 == 0 && activeGeese < MAX_GEESE && !waveSpawned) {
            spawnGooseWave(gooseSpawnCount);
            waveCounter++;
            waveSpawned= true;
        } else if (spawnTimer.secondTracker % 5 != 0) {
            waveSpawned = false;
        }

        ImmutableArray<Entity> players = engine.getEntitiesFor(Family.all(Controller.class, Transform.class).get());

        if (players.size() > 0) {
            targetPosition.set(Transform.MAPPER.get(players.first()).getPosition());
            super.update(deltaTime);
        }

        // player collisions
        for (Entity player : players) {
            Transform transform = Transform.MAPPER.get(player);
            Body body = playerBodies.get(player);
            if (body == null) {
                body = createPlayerBody(transform);
                playerBodies.put(player, body);
            } else {
                body.setTransform(
                    transform.getPosition().x + transform.getSize().x / 2f,
                    transform.getPosition().y + transform.getSize().y / 2f,
                    0f
                );
            }
        }
        //

        ImmutableArray<Entity> geese = engine.getEntitiesFor(Family.all(Goose.class, Transform.class, Move.class).get());
        for (Entity goose : geese) {
            Body body = gooseBodies.get(goose);
            if (body == null) {
                body = createGooseBody(goose);
                gooseBodies.put(goose, body);
            }

            Transform transform = Transform.MAPPER.get(goose);
            body.setTransform(
                transform.getPosition().x + transform.getSize().x / 2f,
                transform.getPosition().y + transform.getSize().y / 2f,
                0f
            );
            body.setLinearVelocity(0f, 0f);
        }

        physicsWorld.step(deltaTime, 6, 2);

        for (Entity goose : geese) {
            Body body = gooseBodies.get(goose);
            Transform transform = Transform.MAPPER.get(goose);
            transform.getPosition().set(
                body.getPosition().x - transform.getSize().x / 2f,
                body.getPosition().y - transform.getSize().y / 2f
            );
        }
    }

    private Body createGooseBody(Entity entity) {
        Transform transform = Transform.MAPPER.get(entity);
        Vector2 size = transform.getSize();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
            transform.getPosition().x + size.x / 2f,
            transform.getPosition().y + size.y / 2f
        );
        bodyDef.fixedRotation = true;
        Body body = physicsWorld.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / 2f, size.y / 2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.2f;
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    private Body createPlayerBody(Transform transform) {
        Vector2 size = transform.getSize();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
            transform.getPosition().x + size.x / 2f,
            transform.getPosition().y + size.y / 2f
        );
        Body body = physicsWorld.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / 2f, size.y / 2f);
        body.createFixture(shape, 0f);
        shape.dispose();
        return body;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Transform transform = Transform.MAPPER.get(entity);
        Move move = Move.MAPPER.get(entity);

        move.getDirection().set(targetPosition).sub(transform.getPosition());
    }

    @Override
    public void dispose() {
        physicsWorld.dispose();
    }
}
