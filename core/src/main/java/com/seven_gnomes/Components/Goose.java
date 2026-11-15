package com.seven_gnomes.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

// Just used as an id for a goose
public class Goose implements Component {
    public static final ComponentMapper<Goose> MAPPER = ComponentMapper.getFor(Goose.class);
}
