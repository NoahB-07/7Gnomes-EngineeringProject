package com.seven_gnomes.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
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
                case SELECT -> throwCorn(entity);
                case CANCEL -> pause();
                case USE_ITEM_ONE -> useItem(0);
                case USE_ITEM_TWO -> useItem(1);
                case USE_ITEM_THREE -> useItem(2);
                case USE_ITEM_FOUR -> useItem(3);
                case USE_ITEM_FIVE -> useItem(4);
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

    private void throwCorn(Entity entity) {
        CornSystem corn = getEngine().getSystem(CornSystem.class);
        if (corn != null) corn.shoot(entity);
    }

    private void useItem(int itemSlotNumber) {
    }

    private void pause() {
        game.getScreen().pause();
    }

    private void moveEntity(Entity entity, float x, float y) {
        Move move = Move.MAPPER.get(entity);
        if (move == null) return;

        move.getDirection().x += x;
        move.getDirection().y += y;
    }
}
