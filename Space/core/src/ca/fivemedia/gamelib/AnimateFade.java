package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.Gdx;

public class AnimateFade extends GameAnimation {

  protected float m_targetOpacity, m_stepOpacity, m_startOpacity, m_currentOpacity;

  public  AnimateFade (float duration, float fromOpacity, float toOpacity) {
    super(duration, 1);
    m_targetOpacity = toOpacity;
    m_startOpacity = fromOpacity;
    m_currentOpacity = m_startOpacity;
    m_stepOpacity = (m_targetOpacity - m_startOpacity)/m_duration;
  }

	public  AnimateFade (float duration, float fromOpacity, float toOpacity, int repeat) {
    super(duration, repeat);
    m_targetOpacity = toOpacity;
    m_startOpacity = fromOpacity;
    m_currentOpacity = m_startOpacity;
    m_stepOpacity = (m_targetOpacity - m_startOpacity)/m_duration;
  }

  public void animateStep(float deltaTime, float time)
  {
      m_currentOpacity += (deltaTime * m_stepOpacity);
      if (m_targetOpacity > m_startOpacity)
      {
        if (m_currentOpacity > m_targetOpacity)
          m_currentOpacity = m_targetOpacity;
      } else
      {
        if (m_currentOpacity < m_targetOpacity)
          m_currentOpacity = m_targetOpacity;
      }
      
      m_target.setOpacity(m_currentOpacity);
  }

  public void initializeAnimation()
  {
    m_currentOpacity = m_startOpacity;
    m_target.setOpacity(m_currentOpacity);

    //Gdx.app.debug("AnimateFade", "initializeAnimation = " + m_currentOpacity);
  }
}