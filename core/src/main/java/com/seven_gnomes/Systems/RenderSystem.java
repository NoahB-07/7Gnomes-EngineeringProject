package com.seven_gnomes.Systems;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.seven_gnomes.Components.Graphic;
import com.seven_gnomes.Components.Transform;
import com.seven_gnomes.GameMain;

/**
 * Render system to render everything onto the screen
 */
public class RenderSystem extends SortedIteratingSystem implements Disposable {

    private final OrthogonalTiledMapRenderer mapRenderer;
    private final SpriteBatch batch;
    private final Viewport viewport;
    private final OrthographicCamera camera;
    private int[] belowLayers = new int[0];
    private int[] aboveLayers = new int[0];

    /**
     * Initialises the base variables
     * @param batch the sprite batch used
     * @param viewport the viewport for the game
     * @param camera the camera to show what to render
     */
    public RenderSystem(SpriteBatch batch, Viewport viewport, OrthographicCamera camera){
        super(
            Family.all(Transform.class, Graphic.class).get(),
            Comparator.comparing(Transform.MAPPER::get)
        );
        this.batch = batch;
        this.viewport = viewport;
        this.camera = camera;
        mapRenderer =  new OrthogonalTiledMapRenderer(null, GameMain.UNIT_SCALE,batch);
    }

    @Override
    public void update(float deltaTime) {
        viewport.apply();
        batch.setColor(Color.WHITE);
        mapRenderer.setView(camera);
        mapRenderer.render(belowLayers);

        forceSort();
        batch.begin();
        super.update(deltaTime);
        batch.end();

        // tops of tall stuff go over the player
        mapRenderer.render(aboveLayers);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Transform transform = Transform.MAPPER.get(entity);
        Graphic graphic = Graphic.MAPPER.get(entity);

        if (graphic.getRegion() == null){
            return;
        }

        Vector2 position  = transform.getPosition();
        Vector2 scaling = transform.getScaling();
        Vector2 size = transform.getSize();

        batch.setColor(graphic.getColor());
        batch.draw(
            graphic.getRegion(),
            position.x - (1f - scaling.x) * size.x * 0.5f,
            position.y - (1f - scaling.y) * size.y * 0.5f,
            size.x * 0.5f, size.y * 0.5f,
            size.x, size.y,
            scaling.x, scaling.y,
            transform.getRotation()
            );

        batch.setColor(Color.WHITE);

    }

    /**
     * Sets the map
     * @param tiledMap the given map to set to
     */
    public void setMap(TiledMap tiledMap){
        mapRenderer.setMap(tiledMap);
        // above layer gets drawn after entities and the rest before
        int above = tiledMap.getLayers().getIndex("above");
        int count = tiledMap.getLayers().getCount();
        belowLayers = new int[above < 0 ? count : count - 1];
        int n = 0;
        for (int i = 0; i < count; i++) {
            if (i != above) belowLayers[n++] = i;
        }
        aboveLayers = above < 0 ? new int[0] : new int[] {above};
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
    }
}
