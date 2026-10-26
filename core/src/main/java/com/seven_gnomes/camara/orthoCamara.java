package com.seven_gnomes.camara;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class orthoCamara implements ApplicationListener {

    private OrthographicCamera camara;

    private final float VIEWPORT_WIDTH = 100f;
    private final float VIEWPORT_HEIGHT = 100f;

    public Sprite characterSprite;
    public SpriteBatch batch;
    public TextureAtlas atlas;


    @Override
    public void create() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        //atlas = new TextureAtlas("atlas/game_atlas.atlas");

        camara = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT  * height/width);
        camara.position.set(0,0,0);
        camara.update();

        //characterSprite = atlas.createSprite("dot");
        batch = new SpriteBatch();
    }

    @Override
    public void resize(int width, int height) {
        camara.viewportHeight = VIEWPORT_HEIGHT;
        camara.viewportWidth = VIEWPORT_WIDTH * height/width;
        camara.update();
    }

    @Override
    public void render() {
        handleInput();
        camara.update();
        batch.setProjectionMatrix(camara.combined);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin();
        batch.end();
    }

    public void handleInput(){
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            camara.translate(0,3,0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            camara.translate(0,-3,0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            camara.translate(-3,0,0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            camara.translate(3,0,0);
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
