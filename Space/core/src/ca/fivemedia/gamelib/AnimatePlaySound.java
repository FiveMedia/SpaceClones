package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;

public class AnimatePlaySound extends GameAnimation {

  String soundName = "";
  float volume = 0.5f;
  boolean m_onScreen = false;
  float minVolume = -1f;
  GameSprite player = null;

  public AnimatePlaySound (String sound, float vol) {
    super(0.00001f, 1);
    soundName = sound;
    volume = vol;
  }

  public AnimatePlaySound (String sound, float vol, boolean onScreen) {
    super(0.00001f, 1);
    soundName = sound;
    volume = vol;
    m_onScreen = onScreen;
  }

  public AnimatePlaySound(String sound, GameSprite p, float max, float min) {
    this(sound,p,max,min, true);
  }

  public AnimatePlaySound(String sound, GameSprite p, float max, float min, boolean onScreen) {
    super(0.00001f, 1);
    soundName = sound;
    minVolume = min;
    volume = max;
    player = p;
    m_onScreen = onScreen;
  }

  public void animateStep(float deltaTime, float time)
  {
    if (!m_onScreen)
    {
      if (player == null)
      {
        GameMain.getSingleton().playSound(soundName, volume);
      }
      else
      {
        GameMain.getSingleton().playSound(soundName, player, m_target, volume, minVolume);
      }
    }
    else if (m_target.getParent().isOnScreen(m_target.getX(), m_target.getY()))
    {
      if (player == null)
      {
        GameMain.getSingleton().playSound(soundName, volume);
      }
      else
      {
        GameMain.getSingleton().playSound(soundName, player, m_target, volume, minVolume);
      }
    }
  }

  public void initializeAnimation()
  {

  }
}