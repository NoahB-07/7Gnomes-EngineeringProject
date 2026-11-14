package com.seven_gnomes.Assets;

import com.badlogic.gdx.graphics.Texture;

public class Item {
    private String name;
    private Texture icon;

    public Item(String name, Texture icon) {
        this.name = name;
        this.icon = icon;

    }

    public Texture getIcon() {
        return icon;
    }

    public void use() {

    }

}
