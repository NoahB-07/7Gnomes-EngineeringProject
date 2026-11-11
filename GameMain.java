package com.seven_gnomes;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.seven_gnomes.Components.Timer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameMain extends Game {

    static final float WORLD_WIDTH = 32f;
    static final float WORLD_HEIGHT = 32f;
    public static final float UNIT_SCALE = 1f/32f;


    private Texture startBackground;
    private Timer timer;
    private Inventory inventory;
    private SpriteBatch batch;
    private FitViewport viewport;
    private OrthographicCamera camera;
    private AssetService assetService;
    private GLProfiler glProfiler;
    private FPSLogger fpsLogger;
    private InputMultiplexer inputMultiplexer;

    private final Map<Class<? extends Screen>, Screen> screenCache = new HashMap<>();

    @Override
    public void create() {
        
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer );

        startBackground= new Texture("Start.png");
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        assetService = new AssetService(new InternalFileHandleResolver());
        glProfiler = new GLProfiler(Gdx.graphics);
        fpsLogger = new FPSLogger();
        timer = new Timer(viewport);
        inventory = new Inventory(viewport);
        glProfiler.enable();
        addScreen(new StartScreen(this));
        addScreen(new GameScreen(this, timer,inventory));
        setScreen(StartScreen.class);
    }


    public void addScreen(Screen screen){
        screenCache.put(screen.getClass(), screen);
    }

    public void setScreen(Class<? extends Screen> screenClass){
        Screen screen = screenCache.get(screenClass);
        if (screen == null){
            throw new GdxRuntimeException("No screen with class " + screenClass + " found in the screen cache");
        }
        super.setScreen(screen);
    }

    public SpriteBatch getBatch() {
        return batch;
    }
    public Texture getBackground(){
        return startBackground;
    }

    public FitViewport getViewport() {
        return viewport;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    @Override
    public void render() {
        glProfiler.reset();
        Gdx.gl.glClearColor(0f,0f,0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();

        Gdx.graphics.setTitle("Goose Game - Draw call: " + glProfiler.getDrawCalls());
        fpsLogger.log();
    }

    @Override
    public void dispose() {
        screenCache.values().forEach(Screen::dispose);
        screenCache.clear();
        assetService.debugDiagnositis();
        assetService.dispose();

        batch.dispose();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
    }


    public void setInputProcessor(InputProcessor ... processors) {
        inputMultiplexer.clear();
        if (processors == null) return;

        for (InputProcessor processor : processors) {
            inputMultiplexer.addProcessor(processor);
        }
    }
}
