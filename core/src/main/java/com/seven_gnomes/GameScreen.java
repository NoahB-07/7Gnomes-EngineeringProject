package com.seven_gnomes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.seven_gnomes.System.RenderSystem;

public class GameScreen extends ScreenAdapter {

    private final Game game;
    private final AssetService assetService;
    private final Viewport viewport;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;

    //if fps drops change engine to pooled engine
    private final Engine engine;


    public GameScreen(GameMain game) {
        this.game = game;
        assetService = game.getAssetService();
        viewport = game.getViewport();
        camera = game.getCamera();
        batch = game.getBatch();
        engine = new Engine();

        engine.addSystem(new RenderSystem(batch,viewport));
    }

    @Override
    public void show() {
        assetService.load(MapAsset.MAIN);
        engine.getSystem(RenderSystem.class).setMap(assetService.getAsset(MapAsset.MAIN));
    }

    @Override
    public void hide() {
        engine.removeAllEntities();
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1/30f);
        engine.update(delta);

    }

    @Override
    public void dispose() {
        for (EntitySystem system: engine.getSystems()){
            if (system instanceof Disposable disposableSystem){
                disposableSystem.dispose();
            }
        }
    }
}
