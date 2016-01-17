package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;

public class AnimateFadeOut extends AnimateFade {

  public  AnimateFadeOut (float duration) {
    super(duration, 1, 0);
  }

  public  AnimateFadeOut (float duration, int repeat) {
    super(duration, 1,0, repeat);
  }

  public void initializeAnimation()
  {
  	m_startOpacity = m_target.getOpacity();
  	m_currentOpacity = m_startOpacity;
  	m_stepOpacity = (m_targetOpacity - m_startOpacity)/m_duration;
  }
}