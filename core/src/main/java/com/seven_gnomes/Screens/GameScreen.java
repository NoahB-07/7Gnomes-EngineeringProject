package com.seven_gnomes.Screens;

import java.util.function.Consumer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.seven_gnomes.Assets.Inventory;
import com.seven_gnomes.Assets.Item;
import com.seven_gnomes.Components.Graphic;
import com.seven_gnomes.Components.Timer;
import com.seven_gnomes.Components.Transform;
import com.seven_gnomes.GameMain;
import com.seven_gnomes.Input.Controller;
import com.seven_gnomes.Input.GameControllerState;
import com.seven_gnomes.Input.IdleControllerState;
import com.seven_gnomes.Input.KeyboardController;
import com.seven_gnomes.Systems.ControllerSystem;
import com.seven_gnomes.Systems.GooseSystem;
import com.seven_gnomes.Systems.MoveSystem;
import com.seven_gnomes.Systems.RenderSystem;
import com.seven_gnomes.Tiled.TiledAshleyUtils;
import com.seven_gnomes.Tiled.TiledService;
import com.seven_gnomes.map.BuildingMap;
import com.seven_gnomes.map.MapAsset;
import com.seven_gnomes.map.MapMarker;


public class GameScreen extends ScreenAdapter {

    private final TextButton pauseButton;
    private final TextButton resumeButton;
    private final TextButton restartButton;
    private final SpriteBatch batch;
    private final float worldHeight;
    private final float worldWidth;
    private final Stage stage;
    private final Stage pstage;
    private final Texture pauseScreen;
    private final Texture gooseTexture;
    private final Engine engine;
    private final TiledService tiledService;
    private final TiledAshleyUtils tiledAshleyUtils;
    final GameMain game;
    final Timer timer;
    final Inventory inventory;
    private boolean paused;
    private BuildingMap buildingMap;
    private final ImmutableArray<Entity> players;
    private final KeyboardController keyboardController;
    private final Rectangle playerFeet = new Rectangle();
    private final Texture placeholderIcon;
    private final Texture keyTexture;
    private final IntMap<Entity> keysOnMap = new IntMap<>();
    private final IntSet keysHeld = new IntSet();
    private final Rectangle keyBounds = new Rectangle();
    private boolean escaped;



    public GameScreen(final GameMain game, final Timer timer, final Inventory inventory ) {
        this.game = game;
        this.timer = timer;
        this.inventory = inventory;
        tiledService = new TiledService(game.getAssetService());
        FitViewport viewport = game.getViewport();
        batch =game.getBatch();
        worldWidth = viewport.getWorldWidth();
        worldHeight = viewport.getWorldHeight();
        pauseScreen=new Texture("Pause.png");

        // placeholder till items team has real icons
        placeholderIcon = new Texture("dot.png");
        keyTexture = new Texture("key.png");
        gooseTexture = new Texture("temp_goose_sprite.png");

        // Camera
        OrthographicCamera camera = game.getCamera();
        camera.zoom -= 0.5f;

        // ECS
        engine = new Engine();
        tiledAshleyUtils = new TiledAshleyUtils(engine,game.getAssetService());
        engine.addSystem(new ControllerSystem(game));
        engine.addSystem(new MoveSystem());
        engine.addSystem(new GooseSystem(timer, new TextureRegion(gooseTexture)));
        engine.addSystem(new RenderSystem(batch, viewport, camera));

        keyboardController = new KeyboardController(GameControllerState.class, engine, inventory);
        players = engine.getEntitiesFor(Family.all(Controller.class, Transform.class).get());

        /// buttons
        stage = new Stage(viewport, batch);
        pstage = new Stage(viewport, batch);
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
        buildingMap = new BuildingMap(tiledMap, GameMain.UNIT_SCALE);
        engine.getSystem(MoveSystem.class).setMap(buildingMap);
        engine.getSystem(GooseSystem.class).setMap(tiledMap, buildingMap);
        tiledService.setMap(tiledMap);
        spawnKeys();
        game.setInputProcessor(stage, keyboardController);






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
                game.setInputProcessor(pstage);
                pause();
                return true;
            }
        });
         resumeButton.addListener(new InputListener(){

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                paused=!paused;
                game.setInputProcessor(stage, keyboardController);
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
        followPlayer();
        engine.update(delta);
        checkLockers();
        checkKeys();
        resetCamera();
    if(!paused){
        stage.act(delta);
        stage.draw();
    }
 if(paused){
        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.7f);
        batch.draw(pauseScreen,0, 0, worldWidth, worldHeight);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
        pstage.act(delta);
        pstage.draw();
    }


    }

    @Override
    public void dispose() {
        placeholderIcon.dispose();
        keyTexture.dispose();
        gooseTexture.dispose();
        for (EntitySystem system: engine.getSystems()){
            if (system instanceof Disposable disposableSystem){
                disposableSystem.dispose();
            }
        }
    }

      @Override
    public void pause() {
        pstage.addActor(resumeButton);
        pstage.addActor(restartButton);
        pauseButton.remove();
        timer.pause();
        // stop moving while paused
        keyboardController.setActiveState(IdleControllerState.class);


    }

    @Override
    public void resume() {
         stage.addActor(pauseButton);
         restartButton.remove();
        resumeButton.remove();
        timer.unpause();
        keyboardController.setActiveState(GameControllerState.class);
    }

    // standing under a locker gives an item if theres room
    private void checkLockers() {
        if (buildingMap == null || players.size() == 0 || inventory.getItems().size() >= 5) return;
        updatePlayerFeet();
        MapMarker locker = buildingMap.useLockerAt(playerFeet);
        if (locker != null) {
            inventory.addItem(new Item(locker.getString("item", "place_holder"), placeholderIcon));
            Gdx.app.log("Locker", "got " + locker.getString("item", "") + " from " + locker.getName());
        }
    }

    // same feet box as move system
    private void updatePlayerFeet() {
        Transform transform = Transform.MAPPER.get(players.first());
        playerFeet.set(
            transform.getPosition().x + transform.getSize().x * 0.1f,
            transform.getPosition().y,
            transform.getSize().x * 0.8f,
            transform.getSize().y * 0.25f);
    }

    // put a key on each key spot in the map
    private void spawnKeys() {
        keysOnMap.clear();
        keysHeld.clear();
        escaped = false;
        TextureRegion region = new TextureRegion(keyTexture);
        for (MapMarker spot : buildingMap.getKeySpots()) {
            Vector2 size = new Vector2(0.8f, 0.4f);
            Vector2 centre = spot.getCenter();
            Entity key = engine.createEntity();
            key.add(new Transform(new Vector2(centre.x - size.x / 2f, centre.y - size.y / 2f), 0, size, new Vector2(1f, 1f), 0f));
            key.add(new Graphic(region, Color.WHITE.cpy()));
            engine.addEntity(key);
            keysOnMap.put(spot.getInt("key", 0), key);
        }
    }

    // walk over key to pick up and its door unlocks straight away
    private void checkKeys() {
        if (buildingMap == null || players.size() == 0 || escaped) return;
        updatePlayerFeet();
        for (MapMarker spot : buildingMap.getKeySpots()) {
            int number = spot.getInt("key", 0);
            Entity key = keysOnMap.get(number);
            if (key == null) continue;
            Transform transform = Transform.MAPPER.get(key);
            keyBounds.set(transform.getPosition().x, transform.getPosition().y, transform.getSize().x, transform.getSize().y);
            if (keyBounds.overlaps(playerFeet)) {
                engine.removeEntity(key);
                keysOnMap.remove(number);
                keysHeld.add(number);
                buildingMap.unlockDoor(number);
                Gdx.app.log("Keys", "picked up key " + number + " and door " + number + " unlocked");
            }
        }
        if (buildingMap.isAtExit(playerFeet)) {
            escaped = true;
            Gdx.app.log("Keys", "escaped through the exit");
        }
    }

    public boolean hasEscaped() {
        return escaped;
    }

    public int getKeysHeld() {
        return keysHeld.size;
    }

    // camera on player but stays inside map
    private void followPlayer() {
        if (buildingMap == null || players.size() == 0) return;
        Transform transform = Transform.MAPPER.get(players.first());
        OrthographicCamera camera = game.getCamera();
        camera.position.set(
            transform.getPosition().x + transform.getSize().x / 2f,
            transform.getPosition().y + transform.getSize().y / 2f, 0);
        buildingMap.clampCamera(camera);
    }

    // ui uses same camera so put it back
    private void resetCamera() {
        OrthographicCamera camera = game.getCamera();
        camera.position.set(worldWidth / 2f, worldHeight / 2f, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public void restart() {

    }
}
