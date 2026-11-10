package com.seven_gnomes.System;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.seven_gnomes.Components.PositionComponent;
import com.seven_gnomes.Components.VelocityComponent;

public class GooseManagerSystem extends EntitySystem {
    private final Engine engine;
    //private final TextureRegion texture;

    private float spawnTimer = 0f; // change to timer class
    private final Array<Entity> allGeese = new Array<>();
    private int waveCounter = 0;
    private int waveGooseCount = 0;
    private static final int MAX_GEESE = 10;

    public GooseManagerSystem(Engine engine) {
        this.engine = engine;
    }

    private void spawnGoose(int amount) {
        for (int i = 0; i < amount; i++) {
            Entity goose = engine.createEntity();

            // need to create a pos here
            goose.add(new PositionComponent(new Vector2(0, 0)));
            goose.add(new VelocityComponent(new Vector2(0, 0)));
            engine.addEntity(goose);
            allGeese.add(goose);
        }
    }

    // if geese dont have 1 hp add damageGoose method

    private void killGoose(Entity goose) {
        engine.removeEntity(goose);
        allGeese.removeValue(goose, true);
    }

    @Override
    public void update(float deltaTime) {
        spawnTimer += deltaTime;

        if (spawnTimer >= 5f && engine.getEntities().size() < MAX_GEESE) {
            spawnGoose(waveGooseCount);
            waveCounter++;
            spawnTimer = 0f;
        }
    }
    
}
