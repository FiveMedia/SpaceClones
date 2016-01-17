package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;

public class AnimateRotateTo extends GameAnimation {

  protected float m_targetDegrees, m_currentDegrees, m_startDegrees, m_stepDegrees;

  public  AnimateRotateTo (float duration, float fromDegrees, float toDegrees) {
    super(duration, 1);
    m_targetDegrees = toDegrees;
    m_startDegrees = fromDegrees;
    m_currentDegrees = m_startDegrees;

    m_stepDegrees = (m_targetDegrees - m_startDegrees)/m_duration;

  }

  public  AnimateRotateTo (float duration, float fromDegrees, float toDegrees, int repeat) {
    super(duration, repeat);
    m_targetDegrees = toDegrees;
    m_startDegrees = fromDegrees;
    m_currentDegrees = m_startDegrees;
    m_stepDegrees = (m_targetDegrees - m_startDegrees)/m_duration;
  }

  public void animateStep(float deltaTime, float time)
  {
      m_currentDegrees += (deltaTime * m_stepDegrees);


      if (m_targetDegrees > m_startDegrees)
      {
        if (m_currentDegrees > m_targetDegrees)
        {
          m_currentDegrees = m_targetDegrees;
        }
      } else if (m_targetDegrees < m_startDegrees)
      {
        if (m_currentDegrees < m_targetDegrees)
        {
          m_currentDegrees = m_targetDegrees;
        }
      } 

      m_target.setRotation(m_currentDegrees);

  }

  public void initializeAnimation()
  {
    m_currentDegrees = m_startDegrees;
  }
}