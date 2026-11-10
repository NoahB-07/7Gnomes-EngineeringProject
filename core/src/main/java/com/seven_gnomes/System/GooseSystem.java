package com.seven_gnomes.System;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.seven_gnomes.Components.PositionComponent;
import com.seven_gnomes.Components.VelocityComponent;

public class GooseSystem extends IteratingSystem {
    //private Player player;
    private Vector2 targetPosition;
    private static final float MAX_VELOCITY = 10f;

    public GooseSystem() {
        super(Family.all(PositionComponent.class, VelocityComponent.class).get());
    }

    private void getTargetPosition() {
        // targetPosition = player.getPosition();
    }

    private void moveTowardsTarget(Entity entity, float deltaTime) {
        PositionComponent position = PositionComponent.MAPPER.get(entity);
        VelocityComponent velocity = VelocityComponent.MAPPER.get(entity);
        getTargetPosition();

        Vector2 direction = targetPosition.cpy().sub(position.getPosition()).nor();
        velocity.setVelocity(direction.scl(MAX_VELOCITY));
    }

    @Override 
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = PositionComponent.MAPPER.get(entity);
        VelocityComponent velocity = VelocityComponent.MAPPER.get(entity);

        position.setPosition(position.getPosition().add(velocity.getVelocity().scl(deltaTime)));
        //need to implement player to get goose to go towards them
    }
}
