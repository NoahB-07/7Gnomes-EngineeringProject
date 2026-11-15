package com.seven_gnomes;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Inventory of the character showing what items they have and which one is selected
 */
public class Inventory {
    private ArrayList<Item> items;
    private Image inventoryBar;
    private Image[] inventorySlots;
    final FitViewport viewport;

    /**
     * Initialises the inventory and the 5 inventory slots
     * @param viewport viewport of the game
     */
    public Inventory(final FitViewport viewport) {
        this.viewport = viewport;
        items = new ArrayList<Item>(5);
        inventorySlots = new Image[5];
        for (int i = 0; i < 5; i++) {
            inventorySlots[i] = new Image();
        }
        inventoryBar = new Image(new Texture("InventoryBar.png"));
        inventoryBar.setScale(0.035f);
        inventoryBar.setPosition(viewport.getWorldWidth() * 0.25f, viewport.getWorldHeight() * 0.02f);
        inventorySlots[0].setPosition(25, 25);
        inventorySlots[1].setPosition(89, 25);
        inventorySlots[2].setPosition(153, 25);
        inventorySlots[3].setPosition(217, 25);
        inventorySlots[4].setPosition(281, 25);

    }

    /**
     * Add item to inventory
     * @param item item to be added
     */
    public void addItem(Item item) {
        items.add(item);

    }

    /**
     * Updating icons on the inventory visual
     */
    public void updateIcons() {
        for (int i = 0; i < 5; i++) {

            Item item = items.get(i);

            if (item != null) {

                inventorySlots[i].setDrawable(new TextureRegionDrawable(new TextureRegion(item.getIcon())));

            } else {

                inventorySlots[i].setDrawable(null);
            }
        }
    }

    /**
     * Remove item from inventory
     * @param item item to be removed
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Get items in the inventory
     * @return array of the items in inventory
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Use currently selected item in inventory
     * @param index index of selected item
     */
    public void useItem(int index) {
        Item item = items.get(index);
        if (item != null) {
            item.use();
            removeItem(item);

        }
    }

    /**
     * Gets the inventory visual
     * @return inventory visual
     */
    public Image getInventBar() {
        return inventoryBar;

    }

    /**
     * Gets all inventory slots
     * @return array of the visual for each inventory slot
     */
    public Image[] getInventorySlots() {
        return inventorySlots;
    }

}
