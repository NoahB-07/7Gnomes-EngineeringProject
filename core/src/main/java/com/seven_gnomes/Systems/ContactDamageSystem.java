package com.seven_gnomes.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.seven_gnomes.Components.Goose;
import com.seven_gnomes.Components.Health;
import com.seven_gnomes.Components.Transform;
import com.seven_gnomes.Input.Controller;

/** 
 * Contact system for damaging when geese are touching the player take one health a second not every frame
 */ 
public class ContactDamageSystem extends IteratingSystem {
    public static final int PLAYER_HEALTH = 30;
    private static final int DAMAGE = 1;
    private static final float HIT_GAP = 1f;
    // box2d keeps geese just outside the player so reach a tiny bit
    private static final float REACH = 0.05f;

    private final Rectangle playerBounds = new Rectangle();
    private final Rectangle gooseBounds = new Rectangle();
    private ImmutableArray<Entity> geese;
    private float cooldown;

    public ContactDamageSystem() {
        super(Family.all(Controller.class, Health.class, Transform.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        geese = engine.getEntitiesFor(Family.all(Goose.class, Transform.class).get());
    }

    @Override
    public void update(float deltaTime) {
        cooldown = Math.max(0f, cooldown - deltaTime);
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity player, float deltaTime) {
        Health health = Health.MAPPER.get(player);
        if (cooldown > 0f || health.getCurrentHealth() <= 0 || !touchingGoose(player)) return;
        health.setCurrentHealth(Math.max(0, health.getCurrentHealth() - DAMAGE));
        cooldown = HIT_GAP;
        Gdx.app.log("Health", "goose hit player health now " + health.getCurrentHealth() + " of " + health.getMaxHealth());
    }

    /**
     * Get if the player is touching the goose
     * @param player the player entity
     * @return whether contact has been made
     */
    private boolean touchingGoose(Entity player) {
        Transform transform = Transform.MAPPER.get(player);
        playerBounds.set(
            transform.getPosition().x - REACH,
            transform.getPosition().y - REACH,
            transform.getSize().x + REACH * 2f,
            transform.getSize().y + REACH * 2f);
        for (int i = 0; i < geese.size(); i++) {
            Transform goose = Transform.MAPPER.get(geese.get(i));
            gooseBounds.set(goose.getPosition().x, goose.getPosition().y, goose.getSize().x, goose.getSize().y);
            if (gooseBounds.overlaps(playerBounds)) return true;
        }
        return false;
    }
}
