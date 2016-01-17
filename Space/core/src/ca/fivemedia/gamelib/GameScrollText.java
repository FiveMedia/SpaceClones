package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GameScrollText extends GameText {

  protected String m_finalText;

  protected float m_duration, m_stateTime;
  protected int m_len, m_lastLen;
  protected boolean m_done = true;

  public  GameScrollText (BitmapFont font, String text, float duration) 
  {
    super(font);
    m_finalText = text;
    m_len = m_finalText.length();
    m_duration = duration;
  }

  public void setText(String text)
  {
    m_finalText = text;
    m_len = m_finalText.length();
  }

  public void run()
  {
    super.setText("");
    m_stateTime = 0;
    m_done = false;
    m_lastLen = 0;
  }

  public void clear()
  {
    super.setText("");
  }

  public void update(float deltaTime)
  {
    if (!m_visible)
      return;

    if (!m_done)
    {
      m_stateTime += deltaTime;
      int l = (int) ( (m_stateTime/m_duration) * m_len);
      if (l > m_len)
      {
        l = m_len;
        m_done = true;
      }

      if (l > m_lastLen)
      {
        m_lastLen = l;
        super.setText(m_finalText.substring(0,l));
      }
    }
  }

  public boolean isDone()
  {
    return m_done;
  }

}

