package com.seven_gnomes.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

// one flying corn
public class Corn implements Component {
    public static final ComponentMapper<Corn> MAPPER = ComponentMapper.getFor(Corn.class);

    private final Vector2 velocity;
    private float life;

    public Corn(Vector2 velocity, float life) {
        this.velocity = velocity;
        this.life = life;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getLife() {
        return life;
    }

    public void setLife(float life) {
        this.life = life;
    }
}
