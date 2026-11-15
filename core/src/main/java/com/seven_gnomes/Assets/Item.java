package com.seven_gnomes.Assets;

import com.badlogic.gdx.graphics.Texture;

public class Item {
    private String name;
    private Texture icon;
    // how many stacked shown as a number under minus one means no number
    private int count = -1;

    public Item(String name, Texture icon) {
        this.name = name;
        this.icon = icon;

    }

    public Texture getIcon() {
        return icon;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void use() {

    }

}
