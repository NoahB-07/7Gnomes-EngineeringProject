package com.seven_gnomes.Screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.seven_gnomes.Components.Timer;
import com.seven_gnomes.GameMain;
import com.seven_gnomes.Systems.RenderSystem;


public class EndScreen extends ScreenAdapter{
    private final Texture endBackground;
    private final Texture endLost;
    private final SpriteBatch batch;
    private final FitViewport viewport;
    private final float worldHeight;
    private final float worldWidth;
    final GameMain game;
    private final Stage stage;
    private final ImageButton quitButton;
    private final ImageButton restartButton;
    private int score;
    private final BitmapFont font;

public EndScreen(final GameMain game) {
    Engine engine = new Engine();

        this.game = game;
        viewport = game.getViewport();
        batch =game.getBatch();
        font = new BitmapFont();

        OrthographicCamera camera = game.getCamera();
        engine.addSystem(new RenderSystem(batch,viewport, camera));
        endBackground = new Texture("End.png");
        endLost = new Texture("EndLost.png");
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();

        stage = new Stage(viewport, batch);
        ImageButton.ImageButtonStyle restartStyle = new ImageButton.ImageButtonStyle();
        restartStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture("Restart.png")));

        restartButton = new ImageButton(restartStyle);

        ImageButton.ImageButtonStyle quitStyle = new ImageButton.ImageButtonStyle();
        quitStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture("Quit.png")));

        quitButton = new ImageButton(quitStyle);

        quitButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Quit.png"))));
        quitButton.setSize(worldWidth * 0.59f,worldHeight *0.07f);
        quitButton.setPosition(worldWidth*0.78f - quitButton.getWidth() / 2f,worldHeight*0.4f);
        stage.addActor(quitButton);
        restartButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Restart.png"))));
        restartButton.setSize(worldWidth * 0.49f,worldHeight *0.08f);
        restartButton.setPosition(worldWidth*0.8f - quitButton.getWidth() / 2f,worldHeight*0.25f);
        stage.addActor(restartButton);





    }
   @Override
    public void render(float delta) {
        draw();

    }

 @Override
    public void show() {
        game.setInputProcessor(stage);
        restartButton.addListener(new InputListener(){

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                stage.dispose();
                game.create();
			    dispose();
                return true;
            }
        });
        quitButton.addListener(new InputListener(){

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                stage.dispose();
			    dispose();
                System.exit(button);
                return true;
            }
        });
    }



   private void draw() {
    results();
    font.setColor(Color.YELLOW);

    ScreenUtils.clear(Color.BLACK);
    viewport.apply();
    batch.setProjectionMatrix(viewport.getCamera().combined);
    batch.begin();
    if(game.hasPlayerEscaped()){
        batch.draw(endBackground, 0, 0, worldWidth, worldHeight); // draw the background
    } else{
        batch.draw(endLost, 0, 0, worldWidth, worldHeight); // draw the background
    }
    font.draw(batch,""+ score, worldWidth * 0.89f, worldHeight * 0.63f);
    font.getData().setScale(0.15f);


    batch.end();
    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();

}
private void results(){

    int cornUsed = game.getCornUsed();
    if(game.hasPlayerEscaped()){
    score = 300 - game.getFinalTime() + cornUsed;
    }
    else{
        score = cornUsed;
    }

}



}

