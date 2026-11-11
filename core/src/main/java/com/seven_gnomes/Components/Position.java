package com.seven_gnomes.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

public class Position implements Component {
    public static final ComponentMapper<Position> MAPPER = ComponentMapper.getFor(Position.class);

    public Vector2 position;

    public Position(Vector2 position) {
        this.position = position;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}