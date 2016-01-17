package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;

public class SoundManager  {

  SoundPair[] m_sounds = new SoundPair[90];
  int m_top = 0;
  int m_state = 0;

  public SoundManager()
  {
    for (int i = 0; i < 90; i++)
    {
      m_sounds[i] = new SoundPair();
    }
  }

  public void addSound(long soundId, Sound sound, GameDrawable sprite, float max, float min)
  {
    SoundPair s = m_sounds[m_top];
    m_top++;

    s.sound = sound;
    s.soundId = soundId;
    s.sprite = sprite;
    s.maxVolume = max;
    s.minVolume = min;

    if (m_top >= 90)
      m_top = 0;

  }

  public void updateVolumes(Vector2 p)
  {
    int start = m_state * 15;
    for (int i = start; i < start + 15; i++)
    {
      if (m_sounds[i].soundId != 0)
      {
        float v = this.calculateVolume(p, m_sounds[i].sprite, m_sounds[i].maxVolume, m_sounds[i].minVolume);
        float pan = this.calculatePan(p, m_sounds[i].sprite);
        //Gdx.app.debug("SoundManager", "updateVolume: " + v);
        m_sounds[i].sound.setPan(m_sounds[i].soundId, pan,v);
      }
    }

    m_state++;
    if (m_state > 5)
      m_state = 0;
  }

  public void removeSound(long soundId)
  {
    for (int i = 0; i < 90; i++)
    {
      if (m_sounds[i].soundId == soundId)
      {
        m_sounds[i].soundId = 0;
        m_sounds[i].sprite = null;
        m_sounds[i].sound = null;
      }
    }
  }

  public void clear()
  {

    for (int i = 0; i < 90; i++)
    {
      m_sounds[i].soundId = 0;
      m_sounds[i].sound = null;
      m_sounds[i].sprite = null;
    }

    m_top = 0;
    m_state = 0;

  }

  public float calculatePan(Vector2 distCalc, GameDrawable target)
  {
    float pan = distCalc.x - target.getX();
    if (pan >= 0)
    {
      //sound on right
      if (pan > 700)
      {
        pan = 700;
      }
    } else if (pan < -700)
    {
      pan = -700;
    }

    return -(pan/700f);

  }

  public float calculateVolume(Vector2 distCalc, GameDrawable target, float max, float min)
  {
      float d = distCalc.dst(target.getX(), target.getY());

      if (d > 1200)
        return min;

      if (d > 500)
        d = 500;

      float v = max - ((max-min) * d / 500f);

      if (v > min)
        return v;

      float dy = Math.abs(distCalc.y) - Math.abs(target.getY());
      if (dy > 500)
        return min;

      return v;
  }
}