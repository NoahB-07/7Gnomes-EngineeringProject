package com.seven_gnomes.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seven_gnomes.Components.Move;
import com.seven_gnomes.Components.Transform;
import com.seven_gnomes.map.BuildingMap;

/**
 * System that handles movement for entities
 */
public class MoveSystem extends IteratingSystem {
    private final Vector2 normalisedDirection = new Vector2();
    private final Rectangle feet = new Rectangle();
    private BuildingMap map;

    /**
     * System handling movement
     */
    public MoveSystem() {
        super(Family.all(Move.class, Transform.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Move move = Move.MAPPER.get(entity);
        if (move.isRooted() || move.getDirection().isZero()) return;

        normalisedDirection.set(move.getDirection()).nor();
        Transform transform = Transform.MAPPER.get(entity);
        Vector2 currentPos = transform.getPosition();
        float dx = move.getMaxSpeed() * normalisedDirection.x * deltaTime;
        float dy = move.getMaxSpeed() * normalisedDirection.y * deltaTime;

        if (map == null) {
            currentPos.set(currentPos.x + dx, currentPos.y + dy);
            return;
        }

        // only feet hit walls so can walk up close to stuff
        Vector2 size = transform.getSize();
        float feetWidth = size.x * 0.8f;
        float offsetX = (size.x - feetWidth) / 2f;
        feet.set(currentPos.x + offsetX, currentPos.y, feetWidth, size.y * 0.25f);
        map.move(feet, dx, dy);
        currentPos.set(feet.x - offsetX, feet.y);
    }

    public void setMap(BuildingMap map) {
        this.map = map;
    }
}
