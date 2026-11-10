package com.seven_gnomes;

import java.util.function.Consumer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;
import com.seven_gnomes.System.GooseManagerSystem;
import com.seven_gnomes.System.GooseSystem;
import com.seven_gnomes.System.RenderSystem;
import com.seven_gnomes.Tiled.TiledAshleyUtils;
import com.seven_gnomes.Tiled.TiledService;

public class GameScreen extends ScreenAdapter {

    private final TiledService tiledService;
    private final TiledAshleyUtils tiledAshleyUtils;

    //if fps drops change engine to pooled engine
    private final Engine engine;


    public GameScreen(GameMain game) {
        tiledService = new TiledService(game.getAssetService());
        engine = new Engine();
        tiledAshleyUtils = new TiledAshleyUtils(engine,game.getAssetService());

        engine.addSystem(new GooseManagerSystem(engine));
        engine.addSystem(new GooseSystem());
        engine.addSystem(new RenderSystem(game.getBatch(),game.getViewport(), game.getCamera()));
    }

    @Override
    public void show() {
        Consumer<TiledMap> renderConsumer = engine.getSystem(RenderSystem.class)::setMap;
        tiledService.setMapChangeConsumer(renderConsumer);
        tiledService.setLoadObjectConsumer(tiledAshleyUtils::onloadObject);

        TiledMap tiledMap = tiledService.loadMap(MapAsset.MAIN);
        tiledService.setMap(tiledMap);
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
