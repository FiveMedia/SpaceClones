package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;

public class AnimateTranslateVertical extends GameAnimation {

  protected float m_maxUp, m_maxDown, m_step, m_currentY;
  protected float m_dir = 1f;

  public  AnimateTranslateVertical (float duration, float maxUp, float maxDown, int repeat) {
    super(duration, repeat);
    m_maxUp = maxUp;
    m_maxDown = maxDown;
    m_step = ((maxUp - maxDown) * 2f) / duration;
    m_currentY = 0;
  }

  public void animateStep(float deltaTime, float time)
  {
    m_currentY += (deltaTime * m_step * m_dir);

    if (m_currentY >= m_maxUp)
      m_dir =  -1;
    else if (m_currentY <= m_maxDown)
      m_dir = 1;

    m_targetSprite.translate(0, m_currentY);
  }

  public void initializeAnimation()
  {
    m_dir = 1;
    m_currentY = 0;
  }
}