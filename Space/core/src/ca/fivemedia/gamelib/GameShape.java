package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public abstract class GameShape implements GameDrawable {

  protected boolean m_visible = true;
  protected float m_opacity = 1.0f;
  public ArrayList<GameAnimateable> m_animations;
  public float m_x, m_y, m_scaleX, m_scaleY;
  protected GameContainer m_parent = null;
  public boolean m_pause = false;

	public  GameShape () {
    super();
    m_animations = new ArrayList<GameAnimateable>();
  }

  public void dispose()
  {}

  public boolean isVisible()
  {
    return m_visible;
  }

  public void setVisible(boolean vis)
  {
    m_visible = vis;
  }

  public void update(float deltaTime)
  {

  }

  public void draw (SpriteBatch s)
  {
    //blank implementation as draws differently, use GameLayer custom draw to draw these objects.

  }

  public float getOpacity()
  {
    return m_opacity;
  }

  public void setOpacity(float o)
  {
    m_opacity = o;
  }

  public void runAnimation(GameAnimateable a)
  {
    a.run(this);
    m_animations.add(a);
  }

  public void stopAllAnimations()
  {
    Iterator iter = m_animations.iterator();
    while (iter.hasNext())
    {
      GameAnimateable a = (GameAnimateable) iter.next();
      a.stop();
      iter.remove();
    }
  }

  public void animate(float deltaTime)
  {
    if (!m_pause)
    {
      Iterator iter = m_animations.iterator();
      while (iter.hasNext())
      {
        GameAnimateable a = (GameAnimateable) iter.next();
        boolean notDone = a.step(deltaTime);
        if (!notDone)
        {
          iter.remove();
        }
      }
    }
  }

  public void setColor(float r, float g, float b, float a) { }

  public void setPosition(float x, float y) {
    m_x = x;
    m_y = y;
  }

  public float getX()
  {
    return m_x;
  }

  public float getY()
  {
    return m_y;
  }

  public void setScale(float s) { 
    m_scaleX = s;
    m_scaleY = s;
  }

  public void setScale(float sx, float sy) { 
    m_scaleX = sx;
    m_scaleY = sy;
  }

  public void setRotation(float a)
  {

  }
  
  public void rotate(float a)
  {

  }

  public void playSound(String soundName, float volume)
  {
    GameMain.getSingleton().playSound(soundName, volume);
  }

  public GameContainer getParent()
  {
    return m_parent;
  }

  public void setParent(GameContainer layer)
  {
    m_parent = layer;
  }

  public void pause()
  {
    m_pause = true;
  }

  public void resume()
  {
    m_pause = false;
  }
  
}

