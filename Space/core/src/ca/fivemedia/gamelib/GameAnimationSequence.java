package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

public class GameAnimationSequence implements GameAnimateable {

  protected boolean m_running;
  protected int m_numRuns = 0;
  protected GameDrawable m_target = null;
  protected GameSprite m_targetSprite;
  protected int m_repeat;
  GameAnimateable[] m_animations = null;
  int m_currentAnimation;
  int m_numAnimations;
  protected boolean m_ignoreStop = false;

  public GameAnimationSequence (GameAnimateable[] animations, int repeat) {
      super();
      m_repeat = repeat;  // - 1 is infinite
      m_numRuns = 0;
      m_running = false;
      m_animations = animations;
      m_currentAnimation = 0;
      m_numAnimations = m_animations.length;
  }

  public void setIgnoreStop(boolean ignore)
  {
    m_ignoreStop = ignore;
  }

  public boolean ignoreStop()
  {
    return m_ignoreStop;
  }

  public boolean step(float deltaTime)
  {
    if (m_running)
    {
 
      m_animations[m_currentAnimation].step(deltaTime);

      if (m_animations[m_currentAnimation].isRunning() == false)
      {
        m_currentAnimation++;
        if (m_currentAnimation >= m_numAnimations)
        {
          //done chain of animations
          m_numRuns++;
          if ((m_repeat < 0) || (m_numRuns < m_repeat))
          {
            //this.animateStep(deltaTime, m_time);
            int nr = m_numRuns;
            this.run(m_target);
            m_numRuns = nr;
          } else
          {
            m_running = false;
          }
        } else
        {
           m_animations[m_currentAnimation].run(m_target);
        }
      }
    }

    return m_running;
  }

  public void run(GameDrawable target)
  {
    m_target = target;
    if (m_target instanceof GameSprite)
    {
      m_targetSprite = (GameSprite)target;
    }

    m_numRuns = 0;
    m_running = true;
    m_currentAnimation = 0;
    m_animations[m_currentAnimation].run(m_target);

  }

  public void stop()
  {
    m_running = false;
    if (m_currentAnimation < m_animations.length)
      m_animations[m_currentAnimation].stop();
  }

  public boolean isRunning()
  {
    return m_running;
  }
}