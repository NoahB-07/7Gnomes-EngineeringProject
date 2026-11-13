package com.seven_gnomes.Systems;

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
import com.badlogic.gdx.utils.Array;
import com.seven_gnomes.Components.Goose;
import com.seven_gnomes.Components.Move;
import com.seven_gnomes.Components.Timer;
import com.seven_gnomes.Components.Transform;
import com.seven_gnomes.GameMain;
import com.seven_gnomes.GooseFactory;
import com.seven_gnomes.Input.Controller;

public class GooseSystem extends IteratingSystem {
    private Engine engine;

    private static final float MAX_VELOCITY = 10f;
    private Vector2 targetPosition = new Vector2();

    private static final int MAX_GEESE = 40;
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
        this.spawnTimer = timer;
        this.gooseTexture = gooseTexture;
    }

    public void setMap(TiledMap map) {
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
    }

    private void spawnGooseWave(int amount) {
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
        System.out.println("Spawned wave, total geese = " + totalGeeseSpawned);    
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.engine = engine;
    }
    
    @Override
    public void update(float deltaTime) {
        if (spawnTimer.getTime()[1] % 5 == 0 && activeGeese < MAX_GEESE && !waveSpawned) {
            spawnGooseWave(gooseSpawnCount);
            waveCounter++;
            waveSpawned= true;
        } else if (spawnTimer.getTime()[1] % 5 != 0) {
            waveSpawned = false;
        }

        ImmutableArray<Entity> players = engine.getEntitiesFor(Family.all(Controller.class, Transform.class).get());

        if (players.size() > 0) {
            targetPosition.set(Transform.MAPPER.get(players.first()).getPosition());
            super.update(deltaTime);
        }
    }

    @Override 
    protected void processEntity(Entity entity, float deltaTime) {
        Transform transform = Transform.MAPPER.get(entity);
        Move move = Move.MAPPER.get(entity);

        move.getDirection().set((targetPosition).sub(transform.getPosition()));
    }
}
