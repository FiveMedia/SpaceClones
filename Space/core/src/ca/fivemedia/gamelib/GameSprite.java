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
import box2dLight.*;

public class GameSprite extends Sprite implements GameDrawable {

  boolean m_visible = true;
  float m_opacity = 1.0f;
  public ArrayList<GameAnimateable> m_animations = new ArrayList<GameAnimateable>();
  GameAnimateable m_nextAnimation = null;
  protected GameContainer m_parent = null;
  public boolean m_pause = false;
  public int m_groupId = -1;

  public GameSprite(Texture texture)
  {
    super(texture);

  }

  public GameSprite()
  {
    super();
  }

	public  GameSprite (TextureRegion region) {
    super(region);
  }

  public void setGroupId(int groupId)
  {
    m_groupId = groupId;
  }

  public int getGroupId()
  {
    return m_groupId;
  }

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

  @Override
  public void dispose()
  {

  }

  @Override
  public void draw(SpriteBatch s)
  {
    //Gdx.app.log("INFO", "Draw GameSprite called. m_opacity = " + m_opacity);
    super.draw(s, m_opacity);
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
    m_nextAnimation = null;
  }

  public void chainAnimations(GameAnimateable a, GameAnimateable b)
  {
    a.run(this);
    m_animations.add(a);
    m_nextAnimation = b;
  }

  public void stopAllAnimations()
  {
    m_nextAnimation = null;
    Iterator iter = m_animations.iterator();
    while (iter.hasNext())
    {
      GameAnimateable a = (GameAnimateable) iter.next();
      if (a.ignoreStop() == false)
      {
        a.stop();
        iter.remove();
      }
    }
  }

  public void animate(float deltaTime)
  {
    if (!m_pause)
    {
      if (m_animations.size() == 0)
        return;

      boolean animationRemoved = false;

      if (m_animations.size() == 1)
      {
        GameAnimateable a = (GameAnimateable) m_animations.get(0);
        boolean notDone = a.step(deltaTime);
        if (!notDone)
        {
          m_animations.remove(0);
          animationRemoved = true;
        }
      } else
      {
        
        Iterator iter = m_animations.iterator();
        while (iter.hasNext())
        {
          GameAnimateable a = (GameAnimateable) iter.next();
          boolean notDone = a.step(deltaTime);
          if (!notDone)
          {
            iter.remove();
            animationRemoved = true;
          }
        }
      }

      if (animationRemoved)
      {
        if (m_nextAnimation != null)
        {
          this.runAnimation(m_nextAnimation);
        }
      }
    }
  }

  public Light getLight()
  {
    return null;
  }

  public void playSound(String soundName, float volume)
  {
    GameMain.getSingleton().playSound(soundName, volume);
  }

  public long loopSound(String soundName, float volume)
  {
    return GameMain.getSingleton().loopSound(soundName, volume);
  }

  public long loopSoundManageVolume(String soundName, GameSprite sprite, GameSprite player, float max, float min)
  {
    return GameMain.getSingleton().loopSoundManageVolume(soundName, sprite, player, max, min);
  }

  public long playSound(String soundName, GameSprite player, GameSprite target, float max, float min)
  {
    return GameMain.getSingleton().playSound(soundName, player, target, max, min);
  }
    

  public void stopSound(String soundName)
  {
    GameMain.getSingleton().stopSound(soundName);
  }

  public void stopSound(String soundName, long soundId)
  {
      GameMain.getSingleton().stopSound(soundName, soundId);
  }

  public void setGlobal(String key, String value)
  {
      GameMain.getSingleton().setGlobal(key,value);
  }

  public String getGlobal(String key)
  {
      return GameMain.getSingleton().getGlobal(key);
  }

  public GameContainer getParent()
  {
    return m_parent;
  }

  public void setParent(GameContainer layer)
  {
    m_parent = layer;
  }

  public void setRotation(float a)
  {
    super.setRotation(a);
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

