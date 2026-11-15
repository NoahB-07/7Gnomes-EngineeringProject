package com.seven_gnomes.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class Sandwich implements Component {
    public static final ComponentMapper<Sandwich> MAPPER = ComponentMapper.getFor(Sandwich.class);

    public int sandwich;

    public Sandwich(int sandwich) {
        this.sandwich = sandwich;
    }

    public int getSandwich() {
        return sandwich;
    }

    public void setSandwich(int sandwich) {
        this.sandwich = sandwich;
    }
}
