package com.seven_gnomes.Tiled;

import java.util.function.Consumer;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.seven_gnomes.Assets.AssetService;
import com.seven_gnomes.map.MapAsset;

public class TiledService {

    private final AssetService assetService;
    private TiledMap currentMap;

    private Consumer<TiledMap> mapChangeConsumer;
    private Consumer<TiledMapTileMapObject> loadObjectConsumer;

    public TiledService(AssetService assetService){
        this.assetService = assetService;
        mapChangeConsumer = null;
        loadObjectConsumer = null;
        currentMap = null;
    }

    public TiledMap loadMap(MapAsset mapAsset){

        TiledMap map = assetService.load(mapAsset);
        map.getProperties().put("mapAsset",mapAsset);
        return map;
    }

    public void setMap(TiledMap map){
        if (currentMap != null){
            assetService.unload(currentMap.getProperties().get("mapAsset", MapAsset.class));
        }
        currentMap = map;
        loadMapObjects(map);
        if (mapChangeConsumer != null){
            mapChangeConsumer.accept(map);
        }
    }

    public void loadMapObjects(TiledMap map){
        for(MapLayer layer: map.getLayers()) {
            if ("Objects".equals(layer.getName())){
                loadObjectLayer(layer);
            }
        }

    }

    public void loadObjectLayer(MapLayer objectLayer){
        if(loadObjectConsumer == null) return;
        for (MapObject mapObject : objectLayer.getObjects()) {
            if(mapObject instanceof TiledMapTileMapObject tileMapObject){
                loadObjectConsumer.accept(tileMapObject);
            }else{
                throw new GdxRuntimeException("Unsupported object" + mapObject.getClass().getSimpleName());
            }
        }
    }

    public void setMapChangeConsumer(Consumer<TiledMap> mapChangeConsumer) {
        this.mapChangeConsumer = mapChangeConsumer;
    }

    public void setLoadObjectConsumer(Consumer<TiledMapTileMapObject> loadObjectConsumer) {
        this.loadObjectConsumer = loadObjectConsumer;
    }
}
