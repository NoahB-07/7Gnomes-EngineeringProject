package com.seven_gnomes.Components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.seven_gnomes.GameMain;

public class Timer {

    public final float waitTime;
    int secondTracker;
    int minuteTracker;
    float timeSinceLastUpdate;
    public Label timerLabel;
    public Table timerTable;
    public Stage stage;
    public FitViewport viewport;

    public Timer(SpriteBatch batch) {

        viewport = new FitViewport(GameMain.WORLD_WIDTH, GameMain.WORLD_HEIGHT);
        stage = new Stage(viewport, batch);

        waitTime = 1f;
        secondTracker = 0;
        minuteTracker = 5;
        timeSinceLastUpdate = 0f;
        minuteTracker = 0;
        timerLabel = new Label(String.format("%03d : %30d", minuteTracker, secondTracker), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timerLabel.setFontScale(1.5f);
        

        //this was to try and add to screen can be removed if not useful
        timerTable = new Table();
        stage.addActor(timerTable);
        timerTable.setVisible(true);


        timerTable.setBounds(0, 0, GameMain.WORLD_WIDTH, GameMain.WORLD_HEIGHT);
        timerTable.setFillParent(true);
        timerTable.top().right();
        timerTable.add(timerLabel).size(100,100).padTop(10).padRight(10);

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

            timerLabel.setText(String.format("%03d : %02d", minuteTracker, secondTracker));
            this.render();
        }
    }

    public void render(){ //render? maybe add to engine
		ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }
}
