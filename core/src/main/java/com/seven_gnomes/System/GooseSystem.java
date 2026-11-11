package com.seven_gnomes.System;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.seven_gnomes.Components.Position;
import com.seven_gnomes.Components.Velocity;
import com.seven_gnomes.Components.Goose;
import com.seven_gnomes.GooseFactory;

public class GooseSystem extends IteratingSystem {
    private Engine engine;

    private static final float MAX_VELOCITY = 10f;
    private Vector2 targetPosition;

    private static final int MAX_GEESE = 40;
    private float spawnTimer = 0f; // change to timer class
    private int waveCounter = 0;
    private int gooseSpawnCount = 0;

    public GooseSystem() {
        super(Family.all(Goose.class, Position.class, Velocity.class).get());
    }

    private void spawnGooseWave(int amount) {
        for (int i = 0; i < amount; i++) {
            // need to create a pos here
            Vector2 spawnPosition = new Vector2();
            GooseFactory.createGoose(engine, null, spawnPosition);       
        }
    }

    private void moveTowardsTarget(Entity entity, float deltaTime) {
        Position position = Position.MAPPER.get(entity);
        Velocity velocity = Velocity.MAPPER.get(entity);
        //targetPosition = Player.MAPPER.get(playerEntity).getPosition();
        
        Vector2 direction = targetPosition.cpy().sub(position.getPosition()).nor();
        velocity.setVelocity(direction.scl(MAX_VELOCITY));
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
    }
    
    @Override
    public void update(float deltaTime) {
        // Use the actual timer class here
        spawnTimer += deltaTime;

        if (spawnTimer >= 5f && engine.getEntities().size() < MAX_GEESE) {
            spawnGooseWave(gooseSpawnCount);
            waveCounter++; // gooseSpawnCount could be dynamic e.g. clamp(waveCounter * 2 + 5, 10, 40)
            spawnTimer = 0f;
        }
    }

    @Override 
    protected void processEntity(Entity entity, float deltaTime) {
        Position position = Position.MAPPER.get(entity);
        Velocity velocity = Velocity.MAPPER.get(entity);

        position.setPosition(position.getPosition().add(velocity.getVelocity().scl(deltaTime)));
        //need to implement player to get goose to go towards them
    }
}
