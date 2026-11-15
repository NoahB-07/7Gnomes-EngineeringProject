package com.seven_gnomes.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

/**
 * Maps goose components for easy access
 */
public class Goose implements Component {
    public static final ComponentMapper<Goose> MAPPER = ComponentMapper.getFor(Goose.class);
}
