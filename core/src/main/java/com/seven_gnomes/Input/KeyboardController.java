package com.seven_gnomes.Input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import java.util.Map;

public class KeyboardController extends InputAdapter {

    private static final Map<Integer, Command> KEY_MAPPING = Map.ofEntries(
        Map.entry(Input.Keys.W, Command.UP),
        Map.entry(Input.Keys.A, Command.LEFT),
        Map.entry(Input.Keys.S, Command.DOWN),
        Map.entry(Input.Keys.D, Command.RIGHT),
        Map.entry(Input.Keys.SPACE, Command.SELECT),
        Map.entry(Input.Keys.ESCAPE, Command.CANCEL)
    );




    @Override
    public boolean keyDown(int keycode) {
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return super.keyUp(keycode);
    }
}
