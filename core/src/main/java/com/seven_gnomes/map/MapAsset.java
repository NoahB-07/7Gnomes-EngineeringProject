package com.seven_gnomes.map;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.maps.tiled.BaseTiledMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.seven_gnomes.Assets.Asset;

/**
 * The map asset for the game used as the level
 */
public enum MapAsset implements Asset<TiledMap> {

    MAIN("building.tmx");

    private final AssetDescriptor<TiledMap> descriptor;

    MapAsset(String mapName){
        BaseTiledMapLoader.Parameters parameters = new BaseTiledMapLoader.Parameters();
        parameters.projectFilePath = "Maps/main.tiled-project";
        descriptor = new AssetDescriptor<>("Maps/"+mapName, TiledMap.class, parameters);
    }

    @Override
    public AssetDescriptor<TiledMap> getDescriptor() {
        return descriptor;
    }
}
