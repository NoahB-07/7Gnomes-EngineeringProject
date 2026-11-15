package com.seven_gnomes;

import com.badlogic.gdx.graphics.Texture;

/**
 * Base class for all items in the game
 */
public class Item {
    private String name;
    private Texture icon;

    /**
     * Constructor for items
     * @param name name of item
     * @param icon assets texture for item
     */
    public Item(String name, Texture icon) {
        this.name = name;
        this.icon = icon;

    }

    /**
     * Gets the item's icon
     * @return item's texture
     */
    public Texture getIcon() {
        return icon;
    }

    /**
     * Use item
     */
    public void use() {

    }

}
