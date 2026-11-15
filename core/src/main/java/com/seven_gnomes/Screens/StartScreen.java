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

/**
 * Start screen displays when the program begins
 */
public class StartScreen extends ScreenAdapter{
    private final Texture startBackground;
    private final SpriteBatch batch;
    private final FitViewport viewport;
    private OrthographicCamera camera;
    private final float worldHeight;
    private final float worldWidth;
    final GameMain game;
    private final Stage stage;
    private final ImageButton startButton;
    private final ImageButton quitButton;


    /**
     * Constructor of start screen
     * @param game hands the current game
     */
    public StartScreen(final GameMain game) {
        this.game = game;
        Engine engine = new Engine();
        viewport = game.getViewport();
        batch =game.getBatch();

        // Camera
        camera = game.getCamera();

        //camera.zoom += 0.5f;

        engine.addSystem(new RenderSystem(batch,viewport, camera));
        startBackground= new Texture("Start.png");
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();

        stage = new Stage(viewport, batch);
    


        
        ImageButton.ImageButtonStyle startStyle = new ImageButton.ImageButtonStyle();
        startStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture("StartButton.png")));

        startButton = new ImageButton(startStyle);

        ImageButton.ImageButtonStyle quitStyle = new ImageButton.ImageButtonStyle();
        quitStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture("Quit.png")));

        quitButton = new ImageButton(quitStyle);

        startButton.setSize(worldWidth * 0.25f,worldHeight*0.07f );
        startButton.setPosition(worldWidth / 2f - startButton.getWidth() / 2f,worldHeight*0.7f - startButton.getHeight() / 2f);
        
        quitButton.setSize(worldWidth * 0.17f,worldHeight *0.07f);
        quitButton.setPosition(worldWidth / 2f - quitButton.getWidth() / 2f,worldHeight*0.56f - quitButton.getHeight() / 2f);
        stage.addActor(quitButton);
        stage.addActor(startButton);





    }

    /**
     * Render start screen
     * @param delta The time in seconds since the last render.
     */
   @Override
    public void render(float delta) {
        draw();

    }

    /**
     * Call to set as current screen
     */
    @Override
    public void show() {
        game.setInputProcessor(stage);
         startButton.addListener(new InputListener(){

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                stage.dispose();
                camera.zoom -= 0.5f;

               game.setScreen(GameScreen.class);

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


    /**
     * Draw components to start screen
     */
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
