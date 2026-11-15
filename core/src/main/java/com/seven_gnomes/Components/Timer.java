package com.seven_gnomes.Components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Timer objects counts down from 5 minutes then ends the game
 */
public class Timer {

    public final float waitTime;
    public int secondTracker;
    public int minuteTracker;
    float timeSinceLastUpdate;
    public Label timerLabel;
    public Table timerTable;
    final FitViewport viewport;
    private boolean pause;
    private int secondsPassed;

    /**
     * Constructor for the timer
     * @param viewport used for creating the table holding the timer
     */
    public Timer(final FitViewport viewport){

        this.viewport = viewport;
        waitTime = 1f;
        secondTracker = 0;
        minuteTracker = 5;
        timeSinceLastUpdate = 0f;
        secondsPassed =0;

        timerLabel = new Label(String.format("%02d : %02d", minuteTracker, secondTracker), new Label.LabelStyle(new BitmapFont(), Color.RED));
        timerLabel.setFontScale(0.085f);

        timerTable = new Table();
        timerTable.setVisible(true);
        timerTable.setPosition(viewport.getWorldWidth()/3.2f, viewport.getWorldHeight()/1.35f);
        timerTable.add(timerLabel);

    }

    /**
     * Updates timer by minus 1 second after at least a second has passed
     * @param deltaTime is the length of time since last update call
     */
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
                        secondsPassed++;
                    }
                } else {
                    secondTracker--;
                }

                timerLabel.setText(String.format("%02d : %02d", minuteTracker, secondTracker));
            }
        }
    }

    /**
     * Gets the timer
     * @return Table holding timer
     */
    public Table getTimer(){
        return timerTable;

    }
    public int getTimePassed(){
        return secondsPassed;
    }

    /**
     * Pauses timer
     */
    public void pause(){
        pause = true;
    }


    /**
     * Unpauses timer
     */
    public void unpause(){
        pause = false;
    }

}
