package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

public abstract class GameAnimation implements GameAnimateable {

	protected float m_time = 0.0f;
	protected float m_duration;
	protected int m_repeat;
	protected GameDrawable m_target = null;
	protected int m_numRuns = 0;
	protected boolean m_running;
  protected GameSprite m_targetSprite;
  protected boolean m_ignoreStop = false;

	public GameAnimation (float duration, int repeat) {
    	super();
    	m_duration = duration;
    	m_repeat = repeat;  // - 1 is infinite
    	m_numRuns = 0;
    	m_running = false;
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
	  	m_time += deltaTime;
	  	if (m_time >= m_duration)
	  	{
	  		m_numRuns++;
	  		if ((m_repeat < 0) || (m_numRuns < m_repeat))
	  		{
          this.animateStep(deltaTime, m_time);
          int nr = m_numRuns;
	  			this.run(m_target);
          m_numRuns = nr;
	  		} else
	  		{
          m_time = m_duration-0.00001f;
          m_running = false;
          this.animateStep(deltaTime, m_time);
	  		}
	  	} else
      {
        this.animateStep(deltaTime, m_time);
      }
  	}
  	return m_running;
  }

  public abstract void animateStep(float deltaTime, float time);
  public abstract void initializeAnimation();

  public void run(GameDrawable target)
  {
  	m_target = target;
    if (m_target instanceof GameSprite)
    {
      m_targetSprite = (GameSprite)target;
    }

  	m_time = 0.0f;
  	m_numRuns = 0;
  	m_running = true;
  	this.initializeAnimation();
  }

  public void stop()
  {
    m_running = false;
  }

  public boolean isRunning()
  {
    return m_running;
  }
}