package com.seven_gnomes.Assets;

import com.badlogic.gdx.graphics.Texture;

/**
 * Base class for all items in the game
 */
public class Item {
    private final Texture icon;
    // how many stacked shown as a number under minus one means no number
    private int count = -1;

    /**
     * Constructor for items
     * @param name name of item
     * @param icon assets texture for item
     */
    public Item(String name, Texture icon) {
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
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void use() {

    }

}
