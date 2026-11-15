package com.seven_gnomes.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Creates graphics and set their region and colour
 */
public class Graphic implements Component {
    public static final ComponentMapper<Graphic> MAPPER = ComponentMapper.getFor(Graphic.class);

    private TextureRegion region;
    private final Color color;

    /**
     * Constructor for Graphic, initialising the region and colour
     * @param region region of the graphic
     * @param color colour of the graphic
     */
    public Graphic(TextureRegion region, Color color) {
        this.region = region;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public TextureRegion getRegion() {
        return region;
    }

    public void setRegion(TextureRegion region) {
        this.region = region;
    }
}
