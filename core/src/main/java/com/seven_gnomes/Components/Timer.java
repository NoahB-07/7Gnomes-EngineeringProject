package com.seven_gnomes.Components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Timer {

    public final float waitTime;
    int secondTracker;
    int minuteTracker;
    float timeSinceLastUpdate;
    public Label timerLabel;
    public Table timerTable;
    final FitViewport viewport;
    boolean pause;


    public Timer(final FitViewport viewport){

        this.viewport = viewport;
        waitTime = 1f;
        secondTracker = 0;
        minuteTracker = 5;
        timeSinceLastUpdate = 0f;
        pause = false;

        timerLabel = new Label(String.format("%02d : %02d", minuteTracker, secondTracker), new Label.LabelStyle(new BitmapFont(), Color.RED));
        timerLabel.setFontScale(0.1f);

        timerTable = new Table();
        timerTable.setVisible(true);
        timerTable.setPosition(viewport.getWorldWidth()*0.1f, viewport.getWorldHeight()*0.98f);
        timerTable.add(timerLabel);

    }

    //method to make timer work
    public void update(float deltaTime) {
        if(!pause){
            timeSinceLastUpdate += deltaTime;

            if(timeSinceLastUpdate >= waitTime) {
                timeSinceLastUpdate = 0f;

                if (secondTracker == 0) {
                    if (minuteTracker == 0) {
                        //game over
                    } else {
                        minuteTracker--;
                        secondTracker = 59;
                    }
                } else {
                    secondTracker--;
                }

                timerLabel.setText(String.format("%02d : %02d", minuteTracker, secondTracker));
            }
        }
    }
    public Table getTimer(){
        return timerTable;

    }

    public void pause(){
        pause = true;
    }

    public void unpause(){
        pause = false;
    }

}
