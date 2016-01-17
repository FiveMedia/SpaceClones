package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import ca.fivemedia.gamelib.*;
import com.badlogic.gdx.math.MathUtils;
import box2dLight.*;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;


public class SwitchSprite extends GameSprite {
  PlayerSprite m_player = null;
  //GameAnimateable m_fadeIn, m_fadeOut;
  int m_state = 0;
  int m_startState = 0;
  int m_code = 0;
  boolean m_levelReset = false;
  boolean m_ignore = false;
  ArrayList<SwitchChangedListener> m_lights = new ArrayList<SwitchChangedListener>(15);
  TextureRegion m_onTexture, m_offTexture;
  int m_toggleTicks = 0;

  public SwitchSprite(TextureAtlas myTextures, PlayerSprite player, int state, int code,int levelReset, int dir) {
    super(myTextures.findRegion("switch_off"));
    m_offTexture = myTextures.findRegion("switch_off");
    m_onTexture = myTextures.findRegion("switch_on");
    m_player = player;
    m_state = state;
    m_startState = m_state;
    m_code = code;

    if (levelReset == 1)
      m_levelReset = true;

    if (m_state == 0)
    {
      this.setRegion(m_offTexture);
    } else
    {
      this.setRegion(m_onTexture);
    }

    this.setScale(1,dir);

  }

  public int getCode()
  {
    return m_code;
  }

  public void addLight(SwitchChangedListener s)
  {
    m_lights.add(s);
  }

  public boolean isCollidable()
  {
    return false;
  }

  @Override
  public void update(float deltaTime)
  {
    if (Intersector.overlaps(this.getBoundingRectangle(), m_player.getBoundingRectangle()))
    {
      if (m_ignore)
        return;

      m_state++;
      if (m_state > 1)
        m_state = 0;

      if (m_state == 0)
      {
        this.setRegion(m_offTexture);
      } else
      {
        this.setRegion(m_onTexture);
      }

      for (SwitchChangedListener s : m_lights)
      {
        s.switchStateChanged(m_state, true);
      }

      m_ignore = true;

      this.playSound("toggle", 0.7f);
      m_toggleTicks = 20;

    } else
    {
      m_toggleTicks--;
      if (m_toggleTicks < 1)
        m_ignore = false;
    }
  }

  public int getState()
  {
    return m_state;
  }

  public boolean getLevelReset()
  {
    return m_levelReset;
  }

  public void resetLevel()
  {
    m_state = m_startState;

    if (m_state == 0)
    {
      this.setRegion(m_offTexture);
    } else
    {
      this.setRegion(m_onTexture);
    }

    for (SwitchChangedListener s : m_lights)
    {
      s.switchStateChanged(m_state, false);
    }

  }

}

