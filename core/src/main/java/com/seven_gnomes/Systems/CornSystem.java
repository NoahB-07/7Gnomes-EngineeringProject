package com.seven_gnomes.Systems;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seven_gnomes.Components.Corn;
import com.seven_gnomes.Components.Goose;
import com.seven_gnomes.Components.Graphic;
import com.seven_gnomes.Components.Move;
import com.seven_gnomes.Components.Transform;
import com.seven_gnomes.GameMain;
import com.seven_gnomes.Input.Controller;
import com.seven_gnomes.map.BuildingMap;

// corn works like bullets and one hit kills a goose
public class CornSystem extends IteratingSystem {
    public static final int START_CORN = 20;
    private static final float SPEED = 12f;
    private static final float LIFE = 1.5f;
    private static final float STEP = 0.2f;
    private static final float TIP = 0.1f;

    private final TextureRegion cornRegion;
    private final Vector2 size;
    private final Vector2 aim = new Vector2(0f, -1f);
    private final Rectangle tip = new Rectangle();
    private final Rectangle cornBounds = new Rectangle();
    private final Rectangle gooseBounds = new Rectangle();
    private final Set<Entity> deadGeese = new HashSet<>();
    private ImmutableArray<Entity> geese;
    private ImmutableArray<Entity> players;
    private BuildingMap map;
    private int corn = START_CORN;

    public CornSystem(TextureRegion cornRegion) {
        super(Family.all(Corn.class, Transform.class).get());
        this.cornRegion = cornRegion;
        size = new Vector2(cornRegion.getRegionWidth() * GameMain.UNIT_SCALE, cornRegion.getRegionHeight() * GameMain.UNIT_SCALE);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        geese = engine.getEntitiesFor(Family.all(Goose.class, Transform.class).get());
        players = engine.getEntitiesFor(Family.all(Controller.class, Transform.class).get());
    }

    public void setMap(BuildingMap map) {
        this.map = map;
    }

    public int getCorn() {
        return corn;
    }

    public void addCorn(int amount) {
        corn += amount;
    }

    @Override
    public void update(float deltaTime) {
        // keep last way player walked so standing still still aims
        if (players.size() > 0) updateAim(players.first());
        deadGeese.clear();
        super.update(deltaTime);
    }

    // throw one corn from the player
    public void shoot(Entity player) {
        Transform transform = Transform.MAPPER.get(player);
        if (corn <= 0 || transform == null) return;
        updateAim(player);
        Vector2 pos = transform.getPosition();
        Vector2 body = transform.getSize();
        float x = pos.x + body.x / 2f;
        float y = pos.y + body.y * 0.4f;
        // feet never in wall so use them if hands are
        if (blockedAt(x, y)) y = pos.y + body.y * 0.125f;
        corn--;

        Entity kernel = getEngine().createEntity();
        kernel.add(new Transform(new Vector2(x - size.x / 2f, y - size.y / 2f), 3, size.cpy(), new Vector2(1f, 1f), 0f));
        kernel.add(new Graphic(cornRegion, Color.WHITE.cpy()));
        kernel.add(new Corn(new Vector2(aim.x * SPEED, aim.y * SPEED), LIFE));
        getEngine().addEntity(kernel);
    }

    private void updateAim(Entity player) {
        Move move = Move.MAPPER.get(player);
        if (move != null && !move.getDirection().isZero()) aim.set(move.getDirection()).nor();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Corn kernel = Corn.MAPPER.get(entity);
        kernel.setLife(kernel.getLife() - deltaTime);
        if (kernel.getLife() <= 0f) {
            getEngine().removeEntity(entity);
            return;
        }

        // small steps so fast corn cant skip walls
        Vector2 pos = Transform.MAPPER.get(entity).getPosition();
        Vector2 velocity = kernel.getVelocity();
        int steps = Math.max(1, (int) Math.ceil(velocity.len() * deltaTime / STEP));
        float stepTime = deltaTime / steps;
        for (int i = 0; i < steps; i++) {
            pos.add(velocity.x * stepTime, velocity.y * stepTime);
            if (blockedAt(pos.x + size.x / 2f, pos.y + size.y / 2f) || hitGoose(pos)) {
                getEngine().removeEntity(entity);
                return;
            }
        }
    }

    private boolean blockedAt(float x, float y) {
        if (map == null) return false;
        tip.set(x - TIP / 2f, y - TIP / 2f, TIP, TIP);
        return map.isBlocked(tip);
    }

    private boolean hitGoose(Vector2 pos) {
        cornBounds.set(pos.x, pos.y, size.x, size.y);
        for (int i = 0; i < geese.size(); i++) {
            Entity goose = geese.get(i);
            if (deadGeese.contains(goose)) continue;
            Transform transform = Transform.MAPPER.get(goose);
            gooseBounds.set(transform.getPosition().x, transform.getPosition().y, transform.getSize().x, transform.getSize().y);
            if (gooseBounds.overlaps(cornBounds)) {
                deadGeese.add(goose);
                getEngine().removeEntity(goose);
                return true;
            }
        }
        return false;
    }
}
