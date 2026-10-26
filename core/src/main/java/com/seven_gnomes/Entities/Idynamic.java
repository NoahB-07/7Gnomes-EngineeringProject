package com.seven_gnomes.Entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public interface Idynamic {

    void draw(SpriteBatch batch);

    void draw(SpriteBatch batch,Viewport viewport);

    void move();
}
