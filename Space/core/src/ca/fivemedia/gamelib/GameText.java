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

public class GameText extends GameShape {

  protected String m_text;
  protected BitmapFont m_font;
  protected float m_width = 0;
  protected boolean m_centered = false;

  public GameText(BitmapFont font)
  {
    super();
    m_animations = new ArrayList<GameAnimateable>();
    m_font = font;
  }

  public  GameText (BitmapFont font, float width) {
    super();
    m_animations = new ArrayList<GameAnimateable>();
    m_font = font;
    m_centered = true;
    m_width = width;
  }

  public void dispose() {}

  public void setText(String s)
  {
    m_text = s;
  }

  public void draw (SpriteBatch s)
  {
    if ((m_text != null) && (this.isVisible()))
    {
      if (!m_centered)
      {
        m_font.draw(s, m_text, m_x, m_y);
      } else
      {
        m_font.draw(s, m_text, m_x, m_y, m_width, 1, false);
      }
    }
  }

  public void setGlobal(String key, String value)
  {
      GameMain.getSingleton().setGlobal(key,value);
  }

  public String getGlobal(String key)
  {
      return GameMain.getSingleton().getGlobal(key);
  }

}

