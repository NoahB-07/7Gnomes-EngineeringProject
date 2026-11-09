package com.seven_gnomes;

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
    public FitViewport viewport;
    final GameMain game;

    private float worldHeight;
    private float worldWidth;
    
    public Timer(final GameMain game){
        this.game = game;
        viewport = game.getViewport();

        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();
        waitTime = 1f;
        secondTracker = 0;
        minuteTracker = 5;
        timeSinceLastUpdate = 0f;
        timerLabel = new Label(String.format("%02d : %02d", minuteTracker, secondTracker), new Label.LabelStyle(new BitmapFont(), Color.RED));
        timerLabel.setFontScale(0.1f);
        
        timerTable = new Table();
        timerTable.setVisible(true);
        timerTable.setPosition(worldWidth*0.1f, worldHeight*0.98f);
        timerTable.add(timerLabel);

    }

    //method to make timer work
    public void update(float deltaTime) {
        
        timeSinceLastUpdate += deltaTime;
        
        if(timeSinceLastUpdate >= waitTime) {
            timeSinceLastUpdate = 0f;
            
            if(secondTracker == 0){
                if(minuteTracker == 0){
                    //game over
                }else{
                    minuteTracker--;
                    secondTracker = 59;
                }
            }else{
                secondTracker--;
            }

            timerLabel.setText(String.format("%02d : %02d", minuteTracker, secondTracker));
            
        }
    }
    public Table getTimer(){
        return timerTable;

    }

    
}
