package ca.fivemedia.gamelib;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GamePanel implements GameDrawable, GameContainer {

	protected ArrayList<GameDrawable> m_children = new ArrayList<GameDrawable>();
	float deltaTime;
  boolean m_visible = true;
  float m_opacity = 1.0f;
  public ArrayList<GameAnimateable> m_animations = new ArrayList<GameAnimateable>();
  float m_x = 0;
  float m_y = 0;
  protected GameContainer m_parent = null;
  public boolean m_pause = false;

	public  GamePanel () {
	    super();
	}

  public void dispose()
  {
      for (GameDrawable d : m_children)
      {
        d.dispose();
      }
  }

  @Override
  public void update(float deltaTime)
  {
  	if ((m_visible) && (!m_pause))
  	{
  	  for (GameDrawable d : m_children)
      {
	  	  if (d.isVisible())
	  		 d.update(deltaTime);
	    }
	  }
  }

  @Override
  public void draw(SpriteBatch s)
  {
  	if (m_visible)
  	{
      //TODO: clear background?
  	  for (GameDrawable d : m_children)
  	  {	
  	  	if (d.isVisible())
  	  	{
  			 d.draw(s);
  		  }
  	  }
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
      for (GameDrawable d : m_children)
      { 
        if (d.isVisible())
        {
         d.animate(deltaTime);
        }
      }
    }
  }

  public void setGameState(int s)
  {
    m_parent.setGameState(s);
  }

	public void add(GameDrawable obj)
	{
		m_children.add(obj);
    obj.setParent(this);
	}

	public void remove(GameDrawable obj)
	{
		m_children.remove(obj);
    obj.setParent(null);
	}

	public void removeAll()
	{
    this.dispose();
		m_children.clear();
	}

  public boolean isVisible()
  {
    return m_visible;
  }

  public void setVisible(boolean vis)
  {
    m_visible = vis;
  }

  public void playSound(String soundName, float volume)
  {
    GameMain.getSingleton().playSound(soundName, volume);
  }

  public long loopSound(String soundName, float volume)
  {
    return GameMain.getSingleton().loopSound(soundName, volume);
  }

  public void stopSound(String soundName)
  {
    GameMain.getSingleton().stopSound(soundName);
  }

  public void setColor(float r, float g, float b, float a) { }
  
  public void setPosition(float x, float y) { 
    m_x = x;
    m_y = y;
  }

  public void setScale(float s) { }
  public void setScale(float sx, float sy) { }
  
  public void rotate(float a)
  {

  }

  public void setRotation(float a)
  {

  }
  
  public void runAnimation(GameAnimateable a)
  {
  }

  public void stopAllAnimations()
  { /*
    Iterator iter = m_animations.iterator();
    while (iter.hasNext())
    {
      GameAnimateable a = (GameAnimateable) iter.next();
      a.stop();
      iter.remove();
    } */
  }

  public float getOpacity() { return 1.0f;}
  public void setOpacity(float o) {}

  public GameContainer getParent()
  {
    return m_parent;
  }

  public void setParent(GameContainer layer)
  {
    m_parent = layer;
  }

  public boolean isOnScreen(float xx, float yy)
  {
    return m_parent.isOnScreen(xx,yy);
  }

  public void pause()
  {
    m_pause = true;
  }

  public void resume()
  {
    m_pause = false;
  }

  public void setGlobal(String key, String value)
  {
      GameMain.getSingleton().setGlobal(key,value);
  }

  public String getGlobal(String key)
  {
      return GameMain.getSingleton().getGlobal(key);
  }

  public ArrayList<GameDrawable> getChildren()
  {
    return m_children;
  }

  public float getX()
  {
    return m_x;
  }

  public float getY()
  {
    return m_y;
  }

}