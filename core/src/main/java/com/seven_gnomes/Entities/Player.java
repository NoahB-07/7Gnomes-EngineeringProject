package com.seven_gnomes.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Dictionary;
import java.util.Hashtable;

public class Player extends Dynamic implements Iplayer  {

    public Player(TextureAtlas atlas, String textureName) {
        super(atlas, textureName);
    }

    @Override
    public void draw(SpriteBatch batch, Viewport viewport) {
        super.draw(batch);
        sprite.setCenter(viewport.getCamera().position.x,viewport.getCamera().position.y);
    }

    @Override
    public void setSpriteSize(float width, float height) {
        super.setSpriteSize(width, height);
    }

    public void move(boolean x, boolean positive) {
        float delta = Gdx.graphics.getDeltaTime();
        float speed = 250;

        Dictionary<Boolean, Integer> dictionary = new Hashtable<>();
        dictionary.put(true,1);
        dictionary.put(false,-1);

        if (x){
            sprite.translateX(speed * delta * dictionary.get(positive));
        }else {
            sprite.translateY(speed * delta * dictionary.get(positive));
        }

    }
}
