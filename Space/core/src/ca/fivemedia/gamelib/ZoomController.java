package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

public class ZoomController {

	float m_time = 0.0f;
	float m_duration;
	boolean m_running;
  float m_targetZoom;
  float m_zoomStep;
  float m_currentZoom;

	public ZoomController(float z) {
    	super();
      m_currentZoom = z;
      m_targetZoom = z;
      m_running = false;
      m_zoomStep = 0;
  }

  public void step(float deltaTime)
  {
  	if (m_running)
  	{
	  	m_time += deltaTime;
	  	m_currentZoom += (m_zoomStep * deltaTime);
	  	if (m_time >= m_duration)
	  	{
        m_currentZoom = m_targetZoom;
	  		m_running = false;
	  	}
  	}
  }

  public void setZoom(float targetZoom)
  {
    m_running = false;
    m_currentZoom = targetZoom;
  }

  public void setZoom(float targetZoom, float duration)
  {
    m_duration = duration;
    m_targetZoom = targetZoom;
    m_zoomStep = (m_targetZoom - m_currentZoom)/m_duration;
    m_running = true;
    m_time = 0.0f;
  }

  public float getZoom()
  {
    return m_currentZoom;
  }

  public boolean isZooming()
  {
    return m_running;
  }
}