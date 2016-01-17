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

public class GameScrollTextPanel extends GamePanel {

  protected boolean m_done = true;
  protected int m_num;
  protected float m_lineHeight;

  public  GameScrollTextPanel (BitmapFont font, String[] lines, float duration) 
  {
    super();
    int totalLen = 0;
    for (int i =0; i < lines.length; i++)
    {
      totalLen += lines[i].length();
    }

    float perLetter = duration/totalLen;
    for (int i =0; i < lines.length; i++)
    {
      float d = lines[i].length() * perLetter;
      GameScrollText lineScroll = new GameScrollText(font, lines[i], d);
      this.add(lineScroll);
    }

    m_lineHeight = font.getLineHeight() + 2;
  }

  public void run()
  {
    int i = 0;
    for (GameDrawable line : m_children)
    {
      GameScrollText lt = (GameScrollText) line;
      if (i == 0)
      {
        lt.run();
      } else
      {
        lt.clear();
      }
      i++;
    }

    m_num = 0;
    m_done = false;    
  }

  @Override
  public void update(float deltaTime)
  {
    if (!m_visible)
      return;

    if (!m_done)
    {
      super.update(deltaTime);
      GameDrawable line = m_children.get(m_num);
      GameScrollText lt = (GameScrollText) line;
      if (lt.isDone())
      {
        m_num++;
        if (m_num >= m_children.size())
        {
          m_done = true;
        } else
        {
          GameDrawable line2 = m_children.get(m_num);
          GameScrollText lt2 = (GameScrollText) line2;
          lt2.run();
        }
      }
    }
  }

  public boolean isDone()
  {
    return m_done;
  }

  public void setPosition(float x, float y)
  {
    super.setPosition(x,y);
    int l = m_children.size();
    //float topY = y - (l*m_lineHeight);
    int i = 0;
    for (GameDrawable line : m_children)
    {
      GameScrollText lt = (GameScrollText) line;
      lt.setPosition(x, y - m_lineHeight*i);
      i++;
    }
  }

}
