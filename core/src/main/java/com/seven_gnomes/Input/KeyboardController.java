package com.seven_gnomes.Input;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.seven_gnomes.Assets.Inventory;

/**
 * Keyboard controller for mapping every key to a command
 */
public class KeyboardController extends InputAdapter {
    private static final Map<Integer, Command> KEY_MAPPING = Map.ofEntries(
        Map.entry(Input.Keys.W, Command.UP),
        Map.entry(Input.Keys.A, Command.LEFT),
        Map.entry(Input.Keys.S, Command.DOWN),
        Map.entry(Input.Keys.D, Command.RIGHT),
        Map.entry(Input.Keys.ESCAPE, Command.PAUSE),
        Map.entry(Input.Keys.SPACE, Command.USE_CORN),
        Map.entry(Input.Keys.E, Command.USE_BREAD),
        Map.entry(Input.Keys.NUM_3, Command.USE_ITEM_THREE),
        Map.entry(Input.Keys.NUM_4, Command.USE_ITEM_FOUR),
        Map.entry(Input.Keys.NUM_5, Command.USE_ITEM_FIVE)
    );


    private final boolean[] commandState;
    private final Map<Class<? extends ControllerState>, ControllerState> stateCache;
    private ControllerState activeState;
    private final Inventory inventory;

    public KeyboardController(Class<? extends ControllerState> initialState, Engine engine, Inventory inventory){
        stateCache = new HashMap<>();
        activeState = null;
        commandState = new boolean[Command.values().length];
        this.inventory = inventory;

        stateCache.put(IdleControllerState.class,new IdleControllerState());
        stateCache.put(GameControllerState.class, new GameControllerState(engine));
        setActiveState(initialState);
    }

    public void setActiveState(Class<? extends ControllerState> stateClass) {
        ControllerState controllerState = stateCache.get(stateClass);
        if (controllerState == null){
            throw new GdxRuntimeException("No state with class " + stateClass + " found in state cache");
        }

        for (Command command : Command.values()) {
            if( activeState != null && commandState[command.ordinal()]){
                activeState.keyUp(command);
            }
            commandState[command.ordinal()] = false;
        }
        activeState = controllerState;
    }


    @Override
    public boolean keyDown(int keycode) {
        Command command = KEY_MAPPING.get(keycode);
        if (command == null) return false;

        commandState[command.ordinal()] = true;
        activeState.keyDown(command);

        switch (keycode) {
            case Input.Keys.NUM_1 -> {
                inventory.useItem(0);
            }
            case Input.Keys.NUM_2 -> {
                inventory.useItem(1);
            }
            case Input.Keys.NUM_3 -> {
                inventory.useItem(2);
            }
            case Input.Keys.NUM_4 -> {
                inventory.useItem(3);
            }
            case Input.Keys.NUM_5 -> {
                inventory.useItem(4);
            }
            default -> {
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        Command command = KEY_MAPPING.get(keycode);
        if (command == null) return false;
        if (!commandState[command.ordinal()]) return false;

        commandState[command.ordinal()] = false;
        activeState.keyUp(command);
        return true;
    }
}

