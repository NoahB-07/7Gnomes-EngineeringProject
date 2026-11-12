package com.seven_gnomes;

import java.util.function.Consumer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.seven_gnomes.Components.Timer;
import com.seven_gnomes.System.RenderSystem;
import com.seven_gnomes.Tiled.TiledAshleyUtils;
import com.seven_gnomes.Tiled.TiledService;


public class GameScreen extends ScreenAdapter {
    
    private TextButton pauseButton;
    private TextButton resumeButton;
    private TextButton restartButton;
    private SpriteBatch batch;
    private FitViewport viewport;
    private OrthographicCamera camera;
    private float worldHeight;
    private float worldWidth;
    private Stage stage;
    private Texture pauseScreen;
    private boolean paused;
    private final TiledService tiledService;
    private final TiledAshleyUtils tiledAshleyUtils;
    final GameMain game;
    final Timer timer;
    final Inventory inventory;


    //if fps drops change engine to pooled engine
    private final Engine engine;


    
    public GameScreen(final GameMain game, final Timer timer, final Inventory inventory ) {
        this.game = game;
        this.timer = timer;
        this.inventory = inventory;
        tiledService = new TiledService(game.getAssetService());
        engine = new Engine();
        tiledAshleyUtils = new TiledAshleyUtils(engine,game.getAssetService());
        viewport = game.getViewport();
        batch =game.getBatch();
        camera =game.getCamera();
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();
        pauseScreen=new Texture("Pause.png");
        engine.addSystem(new RenderSystem(batch,viewport, camera));

        /// buttons
        stage = new Stage(viewport, batch);
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        skin.getFont("default").getData().setScale(0.15f);
        
        pauseButton = new TextButton("Pause",skin,"text");
        resumeButton = new TextButton("RESUME", skin, "text");
        restartButton = new TextButton("RESTART", skin, "text");
        
        pauseButton.setSize(worldWidth * 0.19f,worldHeight *0.07f);
        pauseButton.setPosition(worldWidth -pauseButton.getWidth()*1.5f,worldHeight- pauseButton.getHeight()*1.5f);

        resumeButton.setSize(worldWidth * 0.5f,worldHeight *0.27f);
        resumeButton.setPosition(worldWidth*0.3f -pauseButton.getWidth()*1.5f,worldHeight*0.3f- pauseButton.getHeight()*1.5f);

        restartButton.setSize(worldWidth * 0.5f,worldHeight *0.27f);
        restartButton.setPosition(worldWidth*0.3f -pauseButton.getWidth()*1.5f,worldHeight*0.15f- pauseButton.getHeight()*1.5f);

        resumeButton.getStyle().fontColor = Color.WHITE;
        resumeButton.getLabel().setFontScale(0.25f);
        restartButton.getLabel().setFontScale(0.25f);


        stage.addActor(pauseButton);
        stage.addActor(timer.getTimer());
        stage.addActor(inventory.getInventBar());
        for(Image slot : inventory.getInventorySlots()){
            stage.addActor(slot);
        }
        

        ///pause end

        

    }

    @Override
    public void show() {
        Consumer<TiledMap> renderConsumer = engine.getSystem(RenderSystem.class)::setMap;
        tiledService.setMapChangeConsumer(renderConsumer);
        tiledService.setLoadObjectConsumer(tiledAshleyUtils::onloadObject);

        TiledMap tiledMap = tiledService.loadMap(MapAsset.MAIN);
        tiledService.setMap(tiledMap);
        game.setInputProcessor(stage);

        restartButton.addListener(new InputListener(){
            
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                restart();
                return true;
            }
        });
        pauseButton.addListener(new InputListener(){
            
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                paused=!paused;
                pause();
                return true;
            }
        });
         resumeButton.addListener(new InputListener(){
            
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                paused=!paused;
                resume();
                return true;
            }
        });
        
    }

    @Override
    public void hide() {
        engine.removeAllEntities();
    }

    @Override
    public void render(float delta) {
        

        delta = Math.min(delta, 1/30f);
        timer.update(delta);
        engine.update(delta);
    
    
 if(paused){
        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.7f); 
        batch.draw(pauseScreen,0, 0, worldWidth, worldHeight);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }
        stage.act(delta);
        stage.draw();

    }

    @Override
    public void dispose() {
        for (EntitySystem system: engine.getSystems()){
            if (system instanceof Disposable disposableSystem){
                disposableSystem.dispose();
            }
        }
    }

      @Override
    public void pause() {
        stage.addActor(resumeButton);
        stage.addActor(restartButton);
        pauseButton.remove();
        timer.pause();
        
    }

    @Override
    public void resume() {
         stage.addActor(pauseButton);
         restartButton.remove();
        resumeButton.remove();
        timer.unpause();
    }

    public void restart() {

    }
}
