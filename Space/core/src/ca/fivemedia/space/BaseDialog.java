package ca.fivemedia.space;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import ca.fivemedia.gamelib.*;

public class BaseDialog extends GamePanel {

  protected GameText m_title;
  protected GameText m_prompt1 = null;
  protected GameText m_prompt2 = null;
  protected static GameSprite m_back;
  protected AnimateScaleTo m_scaleAnimationIn, m_scaleAnimationOut;
  GameMenu menu = null;
  int m_numButtons = 2;

  public BaseDialog (BitmapFont font, String button1t, String button2t, int defaultButton, TextureAtlas textures, GameMenuListener listener, InputManager inputManager) 
  {
    super();
    if (m_back == null)
      m_back = new GameSprite(new Texture("dialog_back.png"));
    
    this.add(m_back);
    m_title = new GameText(font,960);
    this.add(m_title);

    m_prompt1 = new GameText(font,960);
    this.add(m_prompt1);

    m_prompt2 = new GameText(font,960);
    this.add(m_prompt2);

    GameButton button1 = new GameButton(textures, button1t, font);
    GameButton button2 = new GameButton(textures, button2t, font);
    menu = new GameMenu(button1, button2, defaultButton, 40, false, inputManager, listener);
    this.add(menu);
    //m_scaleAnimationIn = new AnimateScaleTo(0.5f, 1.0f, 0.05f, 1.0f, 1.0f);
    //m_scaleAnimationOut = new AnimateScaleTo(0.5f, 1.0f, 1.0f, 1.0f, 0.05f);
  }

  public BaseDialog (BitmapFont font, TextureAtlas textures) 
  {
    super();
    if (m_back == null)
      m_back = new GameSprite(new Texture("dialog_back.png"));
    
    this.add(m_back);
    m_title = new GameText(font,960);
    this.add(m_title);
  }

  public void setTitle(String title)
  {
    m_title.setText(title);
  }

  public void setPrompt1(String p1)
  {
    m_prompt1.setText(p1);
  }

  public void setPrompt2(String p2)
  {
    m_prompt2.setText(p2);
  }

  public void setPosition(float x, float y)
  {
    super.setPosition(x,y);
    m_back.setPosition(x,y);
    m_title.setPosition(x+20,y+m_back.getHeight() - 40);

    if (menu != null)
    {
      float mx = (m_back.getWidth() - (275 *  m_numButtons) - (40 * (m_numButtons-1)))/2;
      menu.setPosition(mx+x, y + 60);
    }

    if (m_prompt1 != null)
    {
      m_prompt1.setPosition(x+20,y+m_back.getHeight() - 140);
      m_prompt2.setPosition(x+20,y+m_back.getHeight() - 210);
    }
  }

  public void show()
  {
    this.setPosition(150, 100);
    this.setVisible(true);
    this.resume();
  }

}
