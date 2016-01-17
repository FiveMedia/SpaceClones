package ca.fivemedia.gamelib;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GameMenu extends GamePanel {

  int m_selectedButton = -1;
  int m_lastSelectedButton = -1;
  int m_numButtons = 2;

  boolean m_align = false;
  float m_spacer = 10;
  int m_ignoreTicks = 5;

  InputManager m_inputManager;
  GameMenuListener m_listener;

  public  GameMenu (GameButton button1, float spacer, boolean verticalAlign, InputManager inputManager, GameMenuListener listener) {
      super();
      this.add(button1);
      button1.setSelected(true);

      m_selectedButton = 1;

      m_align = verticalAlign;
      m_spacer = spacer;

      m_inputManager = inputManager;
      m_numButtons = 1;

      m_listener = listener;
      m_ignoreTicks = 5;
  }

  public  GameMenu (GameButton button1, GameButton button2, int defaultButton, float spacer, boolean verticalAlign, InputManager inputManager, GameMenuListener listener) {
      super();
      this.add(button1);
      this.add(button2);
      if (defaultButton == 1)
        button1.setSelected(true);
      else
        button2.setSelected(true);

      m_selectedButton = defaultButton;

      m_align = verticalAlign;
      m_spacer = spacer;

      m_inputManager = inputManager;
      m_numButtons = 2;

      m_listener = listener;
      m_ignoreTicks = 5;
  }

  public  GameMenu (GameButton button1, GameButton button2, GameButton button3, int defaultButton, float spacer, boolean verticalAlign, InputManager inputManager, GameMenuListener listener) {
      super();
      this.add(button1);
      this.add(button2);
      this.add(button3);

      if (defaultButton == 1)
        button1.setSelected(true);
      else if (defaultButton == 2)
        button2.setSelected(true);
      else
        button3.setSelected(true);

      m_selectedButton = defaultButton;

      m_align = verticalAlign;
      m_spacer = spacer;

      m_inputManager = inputManager;
      m_numButtons = 3;

      m_listener = listener;
       m_ignoreTicks = 5;
  }

  public  GameMenu (GameButton button1, GameButton button2, GameButton button3, GameButton button4, int defaultButton, float spacer, boolean verticalAlign, InputManager inputManager, GameMenuListener listener) {
      super();
      this.add(button1);
      this.add(button2);
      this.add(button3);
      this.add(button4);

      if (defaultButton == 1)
        button1.setSelected(true);
      else if (defaultButton == 2)
        button2.setSelected(true);
      else if (defaultButton == 3)
        button3.setSelected(true);
      else
        button4.setSelected(true);

      m_selectedButton = defaultButton;

      m_align = verticalAlign;
      m_spacer = spacer;

      m_inputManager = inputManager;
      m_numButtons = 4;

      m_listener = listener;
       m_ignoreTicks = 5;
  }

  @Override
  public void update(float deltaTime)
  {
    if (m_visible) 
    {
      for (GameDrawable d : m_children)
      {
        if (d.isVisible())
         d.update(deltaTime);
      }
    }

    if (m_ignoreTicks > 0)
      m_ignoreTicks--;
    else
    {
      if (m_align == false)
      {
        if (m_inputManager.isLeftPressed())
        {
          m_selectedButton--;
          if (m_selectedButton < 1)
            m_selectedButton = m_numButtons;
          m_ignoreTicks = 15;
          this.playSound("toggle", 0.5f);
        } else if (m_inputManager.isRightPressed())
        {
          m_selectedButton++;
          if (m_selectedButton > m_numButtons)
            m_selectedButton = 1;
          m_ignoreTicks = 15;
          this.playSound("toggle", 0.5f);
        }
      } else
      {
        if (m_inputManager.isUpPressed())
        {
          m_selectedButton--;
          if (m_selectedButton < 1)
            m_selectedButton = m_numButtons;
          m_ignoreTicks = 15;
          this.playSound("toggle", 0.5f);
        } else if (m_inputManager.isDownPressed())
        {
          m_selectedButton++;
          if (m_selectedButton > m_numButtons)
            m_selectedButton = 1;
          m_ignoreTicks = 15;
          this.playSound("toggle", 0.5f);
        }
      }

      if (m_selectedButton != m_lastSelectedButton)
      {
        m_lastSelectedButton = m_selectedButton;
        int bNum = 1;
        for (GameDrawable d : m_children)
        {
          GameButton b = (GameButton) d;
          if (bNum == m_selectedButton)
            b.setSelected(true);
          else
            b.setSelected(false);
          bNum++;
        }
      }

      if (m_inputManager.isJumpPressed() || m_inputManager.isFirePressed())
      {
        m_listener.buttonSelected(m_selectedButton);
        m_ignoreTicks = 8;
        this.playSound("click", 0.65f);
      } else
      {
        int bNum = 1;
        for (GameDrawable d : m_children)
        {
          GameButton b = (GameButton) d;
          if (m_inputManager.buttonTapped(b))
            m_listener.buttonSelected(bNum);

          bNum++;
        }
      }
    }
  }


  public void setPosition(float x, float y) { 
    m_x = x;
    m_y = y;
    float xx = m_x;
    float yy = m_y;

    for (GameDrawable d : m_children)
    { 
      d.setPosition(xx,yy);

      if (m_align == false)
      {
        xx += 275 + m_spacer;

      } else
      {
        yy -= (80 + m_spacer);
      }
    }
  }

  public void pause()
  {
    m_pause = false;
  }

  public void resume()
  {
    m_pause = false;
  }


}