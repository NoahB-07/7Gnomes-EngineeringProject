package com.seven_gnomes.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.seven_gnomes.Components.Damage;
import com.seven_gnomes.Components.Health;

/**
 * Damage system for handling when an entity takes damage
 */
public class DamageSystem extends IteratingSystem {

    public DamageSystem() {
        super(Family.all(Health.class, Damage.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Health health = Health.MAPPER.get(entity);
    }
}
