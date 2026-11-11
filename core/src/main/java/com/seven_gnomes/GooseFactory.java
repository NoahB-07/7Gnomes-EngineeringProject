package com.seven_gnomes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.seven_gnomes.Components.Goose;
import com.seven_gnomes.Components.Position;
import com.seven_gnomes.Components.Velocity;
import com.badlogic.gdx.graphics.Texture;

public class GooseFactory {
    public static Entity createGoose(Engine engine, Texture gooseTexture, Vector2 spawnPosition) {
        Entity goose = engine.createEntity();
        goose.add(new Goose());
        goose.add(new Position(spawnPosition));
        goose.add(new Velocity(new Vector2(0, 0)));
        engine.addEntity(goose);
        return goose;
    }
}
