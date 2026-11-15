package com.seven_gnomes.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.seven_gnomes.Components.Corn;
import com.seven_gnomes.Components.Health;
import com.seven_gnomes.Components.Sandwich;

public class SandwichSystem extends IteratingSystem {
    public static final int START_SANDWICH = 0;

    private int sandwich = START_SANDWICH;

    public SandwichSystem() {
        super(Family.all(Sandwich.class, Health.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime){}

    public void heal(Entity entity){
        if (sandwich <= 0) return;
        Health health = Health.MAPPER.get(entity);
        health.addHealth(3);
        sandwich--;
    }

    public void addSandwiches(int amount){sandwich += amount;}

    public int getSandwich() {
        return sandwich;
    }
}
