package com.seven_gnomes.Entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Dynamic implements Idynamic{

    protected Sprite sprite;

    public Dynamic(TextureAtlas atlas, String textureName){
        sprite = atlas.createSprite(textureName);
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    @Override
    public void draw(SpriteBatch batch,Viewport viewport){}

    public void setSpriteSize(float width, float height){
        sprite.setSize(25,25);
    }


    @Override
    public void move() {

    }
}
