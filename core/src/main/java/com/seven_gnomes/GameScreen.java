package com.seven_gnomes;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;
import com.seven_gnomes.Input.GameControllerState;
import com.seven_gnomes.Input.KeyboardController;
import com.seven_gnomes.Systems.ControllerSystem;
import com.seven_gnomes.Systems.MoveSystem;
import com.seven_gnomes.Systems.RenderSystem;
import com.seven_gnomes.Tiled.TiledAshleyUtils;
import com.seven_gnomes.Tiled.TiledService;

import java.util.function.Consumer;

public class GameScreen extends ScreenAdapter {

    private final TiledService tiledService;
    private final TiledAshleyUtils tiledAshleyUtils;
    private final KeyboardController keyboardController;
    private final GameMain game;

    //if fps drops change engine to pooled engine
    private final Engine engine;



    public GameScreen(GameMain game) {
        this.game = game;
        tiledService = new TiledService(game.getAssetService());
        engine = new Engine();
        tiledAshleyUtils = new TiledAshleyUtils(engine,game.getAssetService());
        keyboardController = new KeyboardController(GameControllerState.class,engine);

        engine.addSystem(new ControllerSystem());
        engine.addSystem(new MoveSystem());
        engine.addSystem(new RenderSystem(game.getBatch(),game.getViewport(), game.getCamera()));
    }

    @Override
    public void show() {
        game.setInputProcessor(keyboardController);
        keyboardController.setActiveState(GameControllerState.class);

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
