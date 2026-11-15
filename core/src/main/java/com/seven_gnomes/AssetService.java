package com.seven_gnomes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manages assets
 */
public class AssetService implements Disposable {
    private final AssetManager assetManager;

    public AssetService(FileHandleResolver fileHandleResolver){
        assetManager = new AssetManager(fileHandleResolver);
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());
    }

    /**
     * Loads a new asset immediately
     * @param asset asset to be loaded
     * @return asset descriptor
     * @param <T> asset type
     */
    public <T> T load(Asset<T> asset){
        assetManager.load(asset.getDescriptor());
        assetManager.finishLoading();
        return assetManager.get(asset.getDescriptor());
    }

    /**
     * Loads asset, adding to queue
     * @param asset asset to be loaded
     * @param <T> asset type
     */
    public <T> void queue(Asset<T> asset){
        assetManager.load(asset.getDescriptor());
    }

    /**
     * Gets asset
     * @param asset asset to be retrieved
     * @return asset descriptor
     * @param <T> asset type
     */
    public <T> T getAsset(Asset<T> asset){
        return assetManager.get(asset.getDescriptor());
    }

    /**
     * Remove asset and all dependencies
     * @param asset asset to be removed
     * @param <T> asset type
     */
    public <T> void unload(Asset<T> asset) {
        assetManager.unload(asset.getDescriptor().fileName);
    }

    /**
     * Updates assets held in asset manager
     * @return update success
     */
    public boolean update(){
        return assetManager.update();
    }

    /**
     * ?
     */
    public void debugDiagnositis(){
        Gdx.app.debug("AssetService", assetManager.getDiagnostics());
    }

    /**
     * Release all resources of AssetService
     */
    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
