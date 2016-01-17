package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;

public class CameraPositionController {

	float m_time = 0.0f;
	float m_duration;
	boolean m_running;
  float m_targetX, m_targetY, m_currentX, m_currentY;
  float m_stepX, m_stepY;
  boolean m_shake = false;

	public CameraPositionController(float x, float y) {
    	super();
      m_currentX = x;
      m_currentY = y;
      m_running = false;
  }

  public void step(float deltaTime)
  {
  	if (m_running)
  	{
	  	m_time += deltaTime;

      if (m_shake)
      {
        float dx = MathUtils.random(-7f, 7f);
        float dy = MathUtils.random(-6f, 6f);

        m_currentX = m_targetX + dx;
        m_currentY = m_targetY + dy;

        if (m_time >= m_duration)
        {
          m_currentX = m_targetX;
          m_currentY = m_targetY;
          m_running = false;
          m_shake = false;
        }
      } else
      {
  	  	m_currentX += (m_stepX * deltaTime);
        m_currentY += (m_stepY * deltaTime);
  	  	if (m_time >= m_duration)
  	  	{
          m_currentX = m_targetX;
          m_currentY = m_targetY;
  	  		m_running = false;
  	  	}
      }
  	}
  }

  public void setPosition(float targetX, float targetY)
  {
    m_running = false;
    m_shake = false;
    m_currentX = targetX;
    m_currentY = targetY;
  }

  public void setPosition(float targetX, float targetY, float duration)
  {
    m_duration = duration;
    m_targetX = targetX;
    m_targetY = targetY;
    m_stepX = (m_targetX - m_currentX)/m_duration;
    m_stepY = (m_targetY - m_currentY)/m_duration;
    m_running = true;
    m_time = 0.0f;
  }

  public void shake(float targetX, float targetY, float duration)
  {
    m_duration = duration;
    m_targetX = targetX;
    m_targetY = targetY;

    m_currentX = m_targetX;
    m_currentY = m_targetY;

    m_running = true;
    m_shake = true;
    m_time = 0.0f;
  }

  public boolean isShaking()
  {
    return m_shake;
  }

  public void updateCameraPosition(Camera cam)
  {
    cam.position.set(m_currentX, m_currentY,0);
  }

  public float getX()
  {
    return m_currentX;
  }

  public float getY()
  {
    return m_currentY;
  }

  public boolean isPanning()
  {
    return m_running;
  }
}