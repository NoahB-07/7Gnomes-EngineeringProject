package com.seven_gnomes.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class Damage implements Component {
    public static final ComponentMapper<Damage> MAPPER = ComponentMapper.getFor(Damage.class);


    public final int damageDealt;


    public Damage(int damageDealt) {
        this.damageDealt = damageDealt;
    }

    public int getDamageDealt() {
        return damageDealt;
    }
}
