package com.seven_gnomes.Assets;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Inventory {
    private final ArrayList<Item> items;
    private final Image inventoryBar;
    private final Image[] inventorySlots;
    private final Label[] slotCounts;
    final FitViewport viewport;

    public Inventory(final FitViewport viewport) {
        this.viewport = viewport;
        items = new ArrayList<Item>(5);
        inventorySlots = new Image[5];
        slotCounts = new Label[5];
        for (int i = 0; i < 5; i++) {
            inventorySlots[i] = new Image();
        }
        inventoryBar = new Image(new Texture("InventoryBar.png"));
        inventoryBar.setScale(0.07f);
        inventoryBar.setPosition(viewport.getWorldWidth() * 0.39f, viewport.getWorldHeight() * 0.26f);
        // bar png has 16px grey slots every 21px so put icons and numbers on them
        float scale = inventoryBar.getScaleX();
        BitmapFont font = new BitmapFont();
        font.setUseIntegerPositions(false);
        for (int i = 0; i < 5; i++) {
            float slotX = inventoryBar.getX() + 21 * i * scale;
            float slotY = inventoryBar.getY() + scale;
            inventorySlots[i].setBounds(slotX + 2 * scale, slotY + 2 * scale, 12 * scale, 12 * scale);
            slotCounts[i] = new Label("", new Label.LabelStyle(font, Color.WHITE));
            slotCounts[i].setFontScale(0.04f);
            slotCounts[i].setAlignment(Align.bottomRight);
            slotCounts[i].setBounds(slotX, slotY, 15 * scale, 15 * scale);
        }

    }

    public void addItem(Item item) {
        items.add(item);

    }

    public void updateIcons() {
        for (int i = 0; i < 5; i++) {

            Item item = i < items.size() ? items.get(i) : null;

            if (item != null) {

                inventorySlots[i].setDrawable(new TextureRegionDrawable(new TextureRegion(item.getIcon())));
                slotCounts[i].setText(item.getCount() >= 0 ? String.valueOf(item.getCount()) : "");

            } else {

                inventorySlots[i].setDrawable(null);
                slotCounts[i].setText("");
            }
        }
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void useItem(int index) {
        // empty slot did crash the game
        if (index >= items.size()) return;
        Item item = items.get(index);
        if (item != null) {
            item.use();
            removeItem(item);

        }
    }

    public Image getInventBar() {
        return inventoryBar;

    }

    public Image[] getInventorySlots() {
        return inventorySlots;
    }

    public Label[] getSlotCounts() {
        return slotCounts;
    }

}
