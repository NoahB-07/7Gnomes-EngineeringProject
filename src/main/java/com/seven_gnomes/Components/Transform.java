package com.seven_gnomes.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

public class Transform implements Component, Comparable<Transform> {
    public static final ComponentMapper<Transform> MAPPER = ComponentMapper.getFor(Transform.class);

    private final Vector2 position;
    private final int z;
    private final Vector2 size;
    private final Vector2 scaling;
    private final float rotation;


    public Transform(Vector2 position, int z, Vector2 size, Vector2 scaling, float rotation) {
        this.position = position;
        this.z = z;
        this.size = size;
        this.scaling = scaling;
        this.rotation = rotation;
    }

    @Override
    public int compareTo(Transform other) {
        if (z != other.z){
            return Float.compare(z,other.z);
        }
        if (position.y != other.position.y ){
            return Float.compare(position.y, other.position.y);
        }
        return Float.compare(position.x, other.position.x);

    }

    public Vector2 getPosition() {
        return position;
    }

    public int getZ() {
        return z;
    }

    public Vector2 getSize() {
        return size;
    }

    public Vector2 getScaling() {
        return scaling;
    }

    public float getRotation() {
        return rotation;
    }
}
