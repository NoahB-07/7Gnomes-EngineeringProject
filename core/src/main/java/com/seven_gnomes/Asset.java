package com.seven_gnomes;

import com.badlogic.gdx.assets.AssetDescriptor;

/**
 * Interface for asset, requires each asset to have a descriptor
 * @param <T> type of asset
 */
public interface Asset<T> {
    AssetDescriptor<T> getDescriptor();
}
