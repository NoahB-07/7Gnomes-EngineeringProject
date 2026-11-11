package com.seven_gnomes.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

public class Velocity implements Component {
    public static final ComponentMapper<Velocity> MAPPER = ComponentMapper.getFor(Velocity.class);

    public Vector2 velocity;

    public Velocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }
}