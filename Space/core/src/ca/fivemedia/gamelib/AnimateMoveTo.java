package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;

public class AnimateMoveTo extends GameAnimation {

  protected float m_targetLocX, m_targetLocY, m_stepLocX, m_stepLocY, m_startLocX, m_startLocY, m_currentLocX, m_currentLocY;

  public  AnimateMoveTo (float duration, float fromLocX, float fromLocY, float toLocX, float toLocY) {
    super(duration, 1);
    m_targetLocX = toLocX;
    m_targetLocY = toLocY;

    m_startLocX= fromLocX;
    m_startLocY = fromLocY;

    m_currentLocX = fromLocX;
    m_currentLocY = fromLocY;

    m_stepLocX = (m_targetLocX - m_startLocX)/m_duration;
    m_stepLocY = (m_targetLocY - m_startLocY)/m_duration;

  }

  /*
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
  } */

  public void animateStep(float deltaTime, float time)
  {
      m_currentLocX += (deltaTime * m_stepLocX);
      m_currentLocY += (deltaTime * m_stepLocY);

      boolean x_done = false;
      boolean y_done = false;

      if (m_targetLocX > m_startLocX)
      {
        if (m_currentLocX > m_targetLocX)
        {
          m_currentLocX = m_targetLocX;
        }
      } else if (m_targetLocX < m_startLocX)
      {
        if (m_currentLocX < m_targetLocX)
        {
          m_currentLocX = m_targetLocX;
        }
      } 

      if (m_targetLocY > m_startLocY)
      {
        if (m_currentLocY > m_targetLocY)
        {
          m_currentLocY = m_targetLocY;
        }
      } else if (m_targetLocY < m_startLocY)
      {
        if (m_currentLocY < m_targetLocY)
        {
          m_currentLocY = m_targetLocY;
        }
      } 

      m_target.setPosition(m_currentLocX, m_currentLocY);

  }

  public void initializeAnimation()
  {
    m_currentLocX = m_startLocX;
    m_currentLocY = m_startLocY;
  }
}