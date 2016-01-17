package ca.fivemedia.gamelib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public interface GameDrawable {
	public void draw(SpriteBatch s);
	public boolean isVisible();
	public void setVisible(boolean vis);
	public void update(float deltaTime);
	public void setColor(float r, float g, float b, float a);
	public void setPosition(float x, float y);
    public float getX();
    public float getY();
    public void setScale(float s);
    public void setScale(float sx, float sy);
    public void rotate(float a);
    public void setRotation(float a);
    public void runAnimation(GameAnimateable a);
    public void stopAllAnimations();
    public void animate(float deltaTime);
    public float getOpacity();
    public void setOpacity(float o);
    public void playSound(String soundName, float volume);
    public GameContainer getParent();
    public void setParent(GameContainer layer);
    public void pause();
    public void resume();
    public void dispose();
}