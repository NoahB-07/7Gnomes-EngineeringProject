package com.seven_gnomes.Tiled;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.seven_gnomes.Assets.AssetService;
import com.seven_gnomes.Components.Graphic;
import com.seven_gnomes.Components.Move;
import com.seven_gnomes.Components.Transform;
import com.seven_gnomes.GameMain;
import com.seven_gnomes.Input.Controller;

public class TiledAshleyUtils {

    private final Engine engine;
    private final AssetService assetService;

    public TiledAshleyUtils(Engine engine, AssetService assetService) {
        this.engine = engine;
        this.assetService = assetService;
    }

   public void onloadObject(TiledMapTileMapObject tileMapObject){
        Entity entity = engine.createEntity();
        TiledMapTile tile = tileMapObject.getTile();
        TextureRegion textureRegion = getTextureRegion(tile);
        int z = tile.getProperties().get("z",1, Integer.class);

        entity.add(new Graphic(textureRegion, Color.WHITE.cpy()));
        addEntityTransform(
            tileMapObject.getX(), tileMapObject.getY(), z,
            textureRegion.getRegionWidth(), textureRegion.getRegionHeight(),
            tileMapObject.getScaleX(), tileMapObject.getScaleY(),
            entity);
        addEntityController(tileMapObject,entity);
        addEntityMove(tile, entity);

        engine.addEntity(entity);
   }

    private void addEntityMove(TiledMapTile tile, Entity entity) {
        float speed = tile.getProperties().get("speed", 0f,float.class);
        if (speed == 0 ) return;

        entity.add(new Move(speed));
    }

    private void addEntityController(TiledMapTileMapObject tileMapObject, Entity entity) {
        boolean controller = tileMapObject.getProperties().get("controller",false,boolean.class);
        if (!controller) return;

        entity.add(new Controller());
    }


    private void addEntityTransform(
       float x, float y, int z,
       float w, float h,
       float scaleX, float scaleY,
       Entity entity
   ){
        Vector2 position = new Vector2(x,y);
        Vector2 size = new Vector2(w,h);
        Vector2 scaling = new Vector2(scaleX,scaleY);

        position.scl(GameMain.UNIT_SCALE);
        size.scl(GameMain.UNIT_SCALE);

        entity.add(new Transform(position, z, size, scaling, 0f));
   }

    private TextureRegion getTextureRegion(TiledMapTile tile) {
        return tile.getTextureRegion();
    }
}
