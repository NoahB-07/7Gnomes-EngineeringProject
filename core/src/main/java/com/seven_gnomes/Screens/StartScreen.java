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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.seven_gnomes.GameMain;
import com.seven_gnomes.Systems.RenderSystem;

public class StartScreen extends ScreenAdapter{
    private final Texture startBackground;
    private final SpriteBatch batch;
    private final FitViewport viewport;
    private final float worldHeight;
    private final float worldWidth;
    final GameMain game;
    private final Stage stage;
    private final ImageButton startButton;


public StartScreen(final GameMain game) {
        this.game = game;
    Engine engine = new Engine();
        viewport = game.getViewport();
        batch =game.getBatch();
        OrthographicCamera camera = game.getCamera();
        engine.addSystem(new RenderSystem(batch,viewport, camera));
        startBackground = game.getBackground();
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();

        stage = new Stage(viewport, batch);
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));


        startButton = new ImageButton(skin);

        startButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("StartButton.png"))));
        startButton.setSize(worldWidth * 0.19f,worldHeight *0.07f);
        startButton.setPosition(worldWidth / 2f - startButton.getWidth() / 2f,worldHeight*0.63f - startButton.getHeight() / 2f);
        stage.addActor(startButton);





    }
   @Override
    public void render(float delta) {
        draw();

    }

 @Override
    public void show() {
        game.setInputProcessor(stage);
         startButton.addListener(new InputListener(){

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                stage.dispose();
               game.setScreen(GameScreen.class);

			    dispose();
                return true;
            }
        });
    }



   private void draw() {

    ScreenUtils.clear(Color.BLACK);
    viewport.apply();
    batch.setProjectionMatrix(viewport.getCamera().combined);
    batch.begin();
    batch.draw(startBackground, 0, 0, worldWidth, worldHeight); // draw the background
    batch.end();
    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();

}


}
