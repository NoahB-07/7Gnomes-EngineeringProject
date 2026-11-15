package com.seven_gnomes.Components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.seven_gnomes.GameMain;
import com.seven_gnomes.Screens.EndScreen;

public class Timer {

    public final float waitTime;
    public int secondTracker;
    public int minuteTracker;
    float timeSinceLastUpdate;
    public Label timerLabel;
    public Table timerTable;
    private FitViewport viewport;
    private OrthographicCamera camera;

    private boolean pause;
    final GameMain game;



    public Timer(final GameMain game){

        this.game = game;
        waitTime = 1f;
        secondTracker = 0;
        minuteTracker = 5;
        timeSinceLastUpdate = 0f;
        viewport = game.getViewport();
        camera = game.getCamera();

        timerLabel = new Label(String.format("%02d : %02d", minuteTracker, secondTracker), new Label.LabelStyle(new BitmapFont(), Color.RED));
        timerLabel.setFontScale(0.085f);

        timerTable = new Table();
        timerTable.setVisible(true);
        timerTable.setPosition(viewport.getWorldWidth()/3.2f, viewport.getWorldHeight()/1.35f);
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
                        camera.zoom += 0.5f;
                        game.setScreen(EndScreen.class);
                        
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
