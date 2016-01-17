package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;

public class AnimateScaleTo extends GameAnimation {

  protected float m_targetScaleX, m_targetScaleY, m_stepScaleX, m_stepScaleY, m_startScaleX, m_startScaleY, m_currentScaleX, m_currentScaleY;

  public  AnimateScaleTo (float duration, float fromScaleX, float fromScaleY, float toScaleX, float toScaleY) {
    super(duration, 1);
    m_targetScaleX = toScaleX;
    m_targetScaleY = toScaleY;

    m_startScaleX= fromScaleX;
    m_startScaleY = fromScaleY;

    m_currentScaleX = fromScaleX;
    m_currentScaleY = fromScaleY;

    m_stepScaleX = (m_targetScaleX - m_startScaleX)/m_duration;
    m_stepScaleY = (m_targetScaleY - m_startScaleY)/m_duration;

  }

  public  AnimateScaleTo (float duration, float fromScaleX, float fromScaleY, float toScaleX, float toScaleY, int repeat) {
    super(duration, repeat);
    m_targetScaleX = toScaleX;
    m_targetScaleY = toScaleY;

    m_startScaleX= fromScaleX;
    m_startScaleY = fromScaleY;

    m_currentScaleX = fromScaleX;
    m_currentScaleY = fromScaleY;

    m_stepScaleX = (m_targetScaleX - m_startScaleX)/m_duration;
    m_stepScaleY = (m_targetScaleY - m_startScaleY)/m_duration;
  }

  public void animateStep(float deltaTime, float time)
  {
      m_currentScaleX += (deltaTime * m_stepScaleX);
      m_currentScaleY += (deltaTime * m_stepScaleY);

      boolean x_done = false;
      boolean y_done = false;

      if (m_targetScaleX > m_startScaleX)
      {
        if (m_currentScaleX > m_targetScaleX)
        {
          m_currentScaleX = m_targetScaleX;
        }
      } else if (m_targetScaleX < m_startScaleX)
      {
        if (m_currentScaleX < m_targetScaleX)
        {
          m_currentScaleX = m_targetScaleX;
        }
      } 

      if (m_targetScaleY > m_startScaleY)
      {
        if (m_currentScaleY > m_targetScaleY)
        {
          m_currentScaleY = m_targetScaleY;
        }
      } else if (m_targetScaleY < m_startScaleY)
      {
        if (m_currentScaleY < m_targetScaleY)
        {
          m_currentScaleY = m_targetScaleY;
        }
      } 

      m_target.setScale(m_currentScaleX, m_currentScaleY);

  }

  public void initializeAnimation()
  {
    m_currentScaleX = m_startScaleX;
    m_currentScaleY = m_startScaleY;
  }
}