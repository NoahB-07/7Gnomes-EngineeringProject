package com.seven_gnomes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameMain extends ApplicationAdapter {

    TextureAtlas atlas;
    Sprite characterSprite;
    Texture backgroundTexture;
    SpriteBatch batch;
    ScreenViewport viewport;
    OrthographicCamera camera;

    @Override
    public void create() {
        atlas = new TextureAtlas(Utils.getInternalPath("atlas/Game_Atlas.atlas"));
        characterSprite = atlas.createSprite("dot");
        characterSprite.setSize(25,25);
        backgroundTexture = new Texture("greybox.png");
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input(){
        float speed = 250;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            characterSprite.translateY(-speed * delta);
            camera.translate(0,-speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            characterSprite.translateX(-speed * delta);
            camera.translate(-speed * delta,0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            characterSprite.translateY(speed * delta);
            camera.translate(0,speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            characterSprite.translateX(speed * delta);
            camera.translate(speed * delta,0);
        }

    }

    private void logic(){}

    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);


        float backgroundScale = 0.25f;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float drawX = viewport.getCamera().position.x - (worldWidth / 2f);
        float drawY = viewport.getCamera().position.y - (worldHeight / 2f);
        float scaledX = backgroundTexture.getWidth() * backgroundScale;
        float scaledY = backgroundTexture.getHeight() * backgroundScale;

        float u = drawX / scaledX;
        float v = drawY / scaledY;
        float u2 = (drawX + worldWidth) / scaledX;
        float v2 = (drawY + worldHeight) / scaledY;

        batch.begin();

        batch.draw(backgroundTexture,drawX,drawY,worldWidth,worldHeight,u,v2,u2,v);
        characterSprite.draw(batch);
        characterSprite.setCenter(viewport.getCamera().position.x,viewport.getCamera().position.y);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
        backgroundTexture.dispose();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
    }
}
