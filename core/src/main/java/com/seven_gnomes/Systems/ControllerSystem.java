package com.seven_gnomes.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.seven_gnomes.Components.Health;
import com.seven_gnomes.Components.Move;
import com.seven_gnomes.GameMain;
import com.seven_gnomes.Input.Command;
import com.seven_gnomes.Input.Controller;

public class ControllerSystem extends IteratingSystem {

    public ControllerSystem(GameMain game) {
        super(Family.all(Controller.class).get());
        this.game = game;
    }
    public final GameMain game;


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Controller controller = Controller.MAPPER.get(entity);
        if (controller.getPressedCommands().isEmpty() && controller.getReleasedCommands().isEmpty()) return;
        for (Command command : controller.getPressedCommands()) {
            switch (command){
                case UP -> moveEntity(entity, 0f, 1f);
                case DOWN -> moveEntity(entity, 0f, -1f);
                case LEFT -> moveEntity(entity, -1f, 0f);
                case RIGHT -> moveEntity(entity, 1f, 0f);
                case USE_CORN -> throwCorn(entity);
                case PAUSE -> pause();
                case USE_BREAD -> useBread(entity);
            }
        }

        controller.getPressedCommands().clear();

        for (Command command : controller.getReleasedCommands()) {
            switch (command){
                case UP -> moveEntity(entity, 0f, -1f);
                case DOWN -> moveEntity(entity, 0f, 1f);
                case LEFT -> moveEntity(entity, 1f, 0f);
                case RIGHT -> moveEntity(entity, -1f, 0f);
            }
        }
        controller.getReleasedCommands().clear();
    }

    private void useBread(Entity entity) {
        SandwichSystem sandwich = getEngine().getSystem(SandwichSystem.class);
        if (sandwich != null){
            sandwich.heal(entity);
        }
    }

    private void throwCorn(Entity entity) {
        CornSystem corn = getEngine().getSystem(CornSystem.class);
        if (corn != null) {
            corn.shoot(entity);
        }
    }

    private void pause() {
    }

    private void moveEntity(Entity entity, float x, float y) {
        Move move = Move.MAPPER.get(entity);
        if (move == null) return;

        move.getDirection().x += x;
        move.getDirection().y += y;
    }
}
