package com.seven_gnomes.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

/**
 * Damage component for dealing damage to entities
 */
public class Damage implements Component {
    public static final ComponentMapper<Damage> MAPPER = ComponentMapper.getFor(Damage.class);


    public final int damageDealt;

    /**
     * Initialises damage dealt
     * @param damageDealt how much health to take away
     */
    public Damage(int damageDealt) {
        this.damageDealt = damageDealt;
    }

    /**
     * Gets the amount of damage dealt
     * @return damage dealt to entity
     */
    public int getDamageDealt() {
        return damageDealt;
    }
}
