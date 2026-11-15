package com.seven_gnomes.Screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.seven_gnomes.GameMain;
import com.seven_gnomes.Systems.RenderSystem;

public class EndScreen extends ScreenAdapter{
    private final Texture endBackground;
    private final SpriteBatch batch;
    private final FitViewport viewport;
    private final float worldHeight;
    private final float worldWidth;
    final GameMain game;
    private final Stage stage;
    private final ImageButton quitButton;
    private final ImageButton restartButton;


public EndScreen(final GameMain game) {
        this.game = game;
        Engine engine = new Engine();
        viewport = game.getViewport();
        batch =game.getBatch();
        OrthographicCamera camera = game.getCamera();
        engine.addSystem(new RenderSystem(batch,viewport, camera));
        endBackground = new Texture("End.png");
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
        quitButton.setPosition(worldWidth*0.78f - quitButton.getWidth() / 2f,worldHeight*0.5f - quitButton.getHeight() / 2f);
        stage.addActor(quitButton);
        restartButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Restart.png"))));
        restartButton.setSize(worldWidth * 0.49f,worldHeight *0.08f);
        restartButton.setPosition(worldWidth*0.8f - quitButton.getWidth() / 2f,worldHeight*0.35f - quitButton.getHeight() / 2f);
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

    ScreenUtils.clear(Color.BLACK);
    viewport.apply();
    batch.setProjectionMatrix(viewport.getCamera().combined);
    batch.begin();
    batch.draw(endBackground, 0, 0, worldWidth, worldHeight); // draw the background
    batch.end();
    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();

}


}

