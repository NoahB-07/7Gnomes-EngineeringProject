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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.seven_gnomes.Assets.Inventory;
import com.seven_gnomes.Assets.Item;
import com.seven_gnomes.Components.*;
import com.seven_gnomes.GameMain;
import com.seven_gnomes.Input.Controller;
import com.seven_gnomes.Input.GameControllerState;
import com.seven_gnomes.Input.IdleControllerState;
import com.seven_gnomes.Input.KeyboardController;
import com.seven_gnomes.Systems.*;
import com.seven_gnomes.Tiled.TiledAshleyUtils;
import com.seven_gnomes.Tiled.TiledService;
import com.seven_gnomes.map.BuildingMap;
import com.seven_gnomes.map.MapAsset;
import com.seven_gnomes.map.MapMarker;


public class GameScreen extends ScreenAdapter {

    private final ImageButton pauseButton;
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
    private static final int LOCKER_CORN = 30;
    private static final int LOCKER_SANDWICH = 1;
    private final Texture cornTexture;
    private final Texture sandwichTexture;
    private final Item cornItem;
    private final Item sandwichItem;
    private int cornShown = -1;
    private final Texture keyTexture;
    private final Animation<TextureRegion> keySpin;
    private float keyTime;
    private final IntMap<Entity> keysOnMap = new IntMap<>();
    private final IntSet keysHeld = new IntSet();
    private final Rectangle keyBounds = new Rectangle();
    private boolean escaped;
    private int sandwichAmount;



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

        sandwichTexture = new Texture("sandwich.png");
        cornTexture = new Texture("popcorn_bowl.png");
        keyTexture = new Texture("key.png");
        // key png is 12 spinning frames side by side
        keySpin = new Animation<>(0.18f, TextureRegion.split(keyTexture, keyTexture.getWidth() / 12, keyTexture.getHeight())[0]);
        gooseTexture = new Texture("temp_goose_sprite.png");

        // Camera
        OrthographicCamera camera = game.getCamera();

        // ECS
        engine = new Engine();
        tiledAshleyUtils = new TiledAshleyUtils(engine,game.getAssetService());
        engine.addSystem(new SandwichSystem());
        engine.addSystem(new ControllerSystem(game));
        engine.addSystem(new MoveSystem());
        engine.addSystem(new GooseSystem(timer, new TextureRegion(gooseTexture)));
        engine.addSystem(new CornSystem(new TextureRegion(new Texture("corn.png"))));
        engine.addSystem(new ContactDamageSystem());
        engine.addSystem(new RenderSystem(batch, viewport, camera));

        keyboardController = new KeyboardController(GameControllerState.class, engine, inventory);
        players = engine.getEntitiesFor(Family.all(Controller.class, Transform.class).get());

        /// buttons
        stage = new Stage(viewport, batch);
        pstage = new Stage(viewport, batch);
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));


        ImageButton.ImageButtonStyle pauseStyle = new ImageButton.ImageButtonStyle();
        pauseStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture("pausebutton.png")));

        pauseButton = new ImageButton(pauseStyle);

        pauseButton.setSize(worldWidth * 0.07f,worldHeight*0.08f );
        pauseButton.setPosition(worldWidth *0.65f ,worldHeight*0.7f);

        resumeButton = new TextButton("RESUME", skin, "text");
        restartButton = new TextButton("RESTART", skin, "text");

        resumeButton.setSize(worldWidth * 0.25f,worldHeight *0.07f);
        resumeButton.setPosition(worldWidth*0.25f ,worldHeight*0.4f);

        restartButton.setSize(worldWidth * 0.2f,worldHeight *0.07f);
        restartButton.setPosition(worldWidth*0.3f,worldHeight*0.35f);

        resumeButton.getStyle().fontColor = Color.WHITE;

        resumeButton.getLabel().setFontScale(0.12f);
        restartButton.getLabel().setFontScale(0.12f);


        stage.addActor(pauseButton);
        stage.addActor(timer.getTimer());

        stage.addActor(inventory.getInventBar());
        for(Image slot : inventory.getInventorySlots()){
            stage.addActor(slot);
        }
        // corn lives in first inventory slot with how many under it
        cornItem = new Item("corn", cornTexture);
        sandwichItem = new Item("sandwich", sandwichTexture);

        inventory.addItem(cornItem);
        inventory.addItem(sandwichItem);
        for (Label count : inventory.getSlotCounts()) {
            stage.addActor(count);
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
        engine.getSystem(CornSystem.class).setMap(buildingMap);
        tiledService.setMap(tiledMap);
        // hidden health for now no bar drawn
        for (int i = 0; i < players.size(); i++) {
            players.get(i).add(new Health(ContactDamageSystem.PLAYER_HEALTH, ContactDamageSystem.PLAYER_HEALTH));
        }
        spawnKeys();
        game.setInputProcessor(stage, keyboardController);






        restartButton.addListener(new InputListener(){

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                stage.dispose();
                game.create();
			    dispose();
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
        updateCornSlot();
        updateSandwichSlot();
        resetCamera();
    if(!paused){
        stage.act(delta);
        stage.draw();
    }
 if(paused){
        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.7f);
        batch.draw(pauseScreen,8, 8, worldWidth/2, worldHeight/2);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
        pstage.act(delta);
        pstage.draw();
    }


    }

    @Override
    public void dispose() {
        cornTexture.dispose();
        sandwichTexture.dispose();
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

    // standing under a locker gives corn
    private void checkLockers() {
        if (buildingMap == null || players.size() == 0) return;
        updatePlayerFeet();
        MapMarker locker = buildingMap.useLockerAt(playerFeet);
        if (locker != null) {
            engine.getSystem(CornSystem.class).addCorn(LOCKER_CORN);
            engine.getSystem(SandwichSystem.class).addSandwiches(LOCKER_SANDWICH);
            Gdx.app.log("Locker", "got " + LOCKER_SANDWICH + " sandwich from " + locker.getName());
            Gdx.app.log("Locker", "got " + LOCKER_CORN + " corn from " + locker.getName());
        }
    }

    private void updateCornSlot() {
        int corn = engine.getSystem(CornSystem.class).getCorn();
        if (cornShown == corn) return;
        cornShown = corn;
        cornItem.setCount(corn);
        inventory.updateIcons();
    }

    private void updateSandwichSlot(){
        int sandwich = engine.getSystem(SandwichSystem.class).getSandwich();
        sandwichItem.setCount(sandwich);
        inventory.updateIcons();
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
        TextureRegion region = keySpin.getKeyFrame(0f);
        for (MapMarker spot : buildingMap.getKeySpots()) {
            // same pixel size as the map art
            Vector2 size = new Vector2(region.getRegionWidth() * GameMain.UNIT_SCALE, region.getRegionHeight() * GameMain.UNIT_SCALE);
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
        keyTime += Gdx.graphics.getDeltaTime();
        TextureRegion frame = keySpin.getKeyFrame(keyTime, true);
        for (MapMarker spot : buildingMap.getKeySpots()) {
            int number = spot.getInt("key", 0);
            Entity key = keysOnMap.get(number);
            if (key == null) continue;
            Graphic.MAPPER.get(key).setRegion(frame);
            Transform transform = Transform.MAPPER.get(key);
            keyBounds.set(transform.getPosition().x, transform.getPosition().y, transform.getSize().x, transform.getSize().y);
            if (keyBounds.overlaps(playerFeet)) {
                engine.removeEntity(key);
                keysOnMap.remove(number);
                keysHeld.add(number);
                buildingMap.unlockDoor(number);
                Gdx.app.log("Keys", "picked up key " + number + " and door " + number + " unlocked");
                // last key sets off geese from every spawn till you escape
                if (keysOnMap.size == 0) engine.getSystem(GooseSystem.class).startRapidSpawn(Float.MAX_VALUE);
            }
        }
        if (buildingMap.isAtExit(playerFeet)) {
            escaped = true;
            engine.getSystem(GooseSystem.class).stopRapidSpawn();
            Gdx.app.log("Keys", "escaped through the exit");
        }
    }

    public boolean hasEscaped() {
        return escaped;
    }

    public int getKeysHeld() {
        return keysHeld.size;
    }

    public int getPlayerHealth() {
        if (players.size() == 0 || Health.MAPPER.get(players.first()) == null) return 0;
        return Health.MAPPER.get(players.first()).getCurrentHealth();
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
