package com.seven_gnomes;

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
import com.seven_gnomes.System.RenderSystem;

/**
 * Start screen displays when the program begins
 */
public class StartScreen extends ScreenAdapter{
    private Texture startBackground;
    private SpriteBatch batch;
    private FitViewport viewport;
    private OrthographicCamera camera;
    private float worldHeight;
    private float worldWidth;
    private final Engine engine;
    final GameMain game;
    private Stage stage;
    private ImageButton startButton;

    /**
     * Constructor of start screen
     * @param game hands the current game
     */
    public StartScreen(final GameMain game) {
        this.game = game;
        engine = new Engine();
        viewport = game.getViewport();
        batch =game.getBatch();
        camera =game.getCamera();
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

            /**
             * Handles user input
             * @param event event that has triggered method call
             * @param x position of input
             * @param y position of input
             * @param pointer
             * @param button what was pressed
             * @return success of event input
             */
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                stage.dispose();
                game.setScreen(GameScreen.class);

			    dispose();
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
