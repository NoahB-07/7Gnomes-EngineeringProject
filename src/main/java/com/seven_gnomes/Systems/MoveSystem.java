package com.seven_gnomes.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.seven_gnomes.Components.Move;
import com.seven_gnomes.Components.Transform;

public class MoveSystem extends IteratingSystem {
    private Vector2 normalisedDirection = new Vector2();

    public MoveSystem() {
        super(Family.all(Move.class, Transform.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Move move = Move.MAPPER.get(entity);
        if (move.isRooted() || move.getDirection().isZero()) return;

        normalisedDirection.set(move.getDirection()).nor();
        Transform transform = Transform.MAPPER.get(entity);
        Vector2 currentPos = transform.getPosition();
        transform.getPosition().set(
            currentPos.x + move.getMaxSpeed() * normalisedDirection.x * deltaTime,
            currentPos.y + move.getMaxSpeed() * normalisedDirection.y * deltaTime);

    }
}