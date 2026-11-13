package com.seven_gnomes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.seven_gnomes.Components.Goose;
import com.seven_gnomes.Components.Move;
import com.seven_gnomes.Components.Transform;

public class GooseFactory {
    public static Entity createGoose(Engine engine, TextureRegion gooseTexture, Vector2 spawnPosition, int z, Vector2 size, Vector2 scaling) {
        Entity goose = engine.createEntity();
        goose.add(new Goose());
        goose.add(new Transform(spawnPosition.cpy(), z, size.cpy(), scaling.cpy(), 0f));
        goose.add(new Move(10f));
        engine.addEntity(goose);
        return goose;
    }
}
