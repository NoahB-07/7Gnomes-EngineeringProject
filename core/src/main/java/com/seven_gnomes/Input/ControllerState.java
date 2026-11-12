package com.seven_gnomes.Input;

public interface ControllerState {
    void keyDown(Command command);

    default void keyUp(Command command){

    }

    }
