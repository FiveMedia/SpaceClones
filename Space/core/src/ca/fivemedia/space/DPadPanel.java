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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class DPadPanel extends GamePanel {

  protected GameText m_title;
  protected GameSprite[] m_arrows;
  protected float m_scaleUp, m_scaleDown;
  protected int m_size;
  protected float m_x1, m_x2, m_y1, m_y2;
  protected GameSprite[] m_buttons;

  public DPadPanel (TextureAtlas textures) 
  {
    super();
    m_arrows = new GameSprite[8];
    m_buttons = new GameSprite[3];

    m_arrows[0] = new GameSprite(textures.findRegion("dpad_top"));
    m_arrows[1] = new GameSprite(textures.findRegion("dpad_right"));
    m_arrows[2] = new GameSprite(textures.findRegion("dpad_bottom"));
    m_arrows[3] = new GameSprite(textures.findRegion("dpad_left"));
    m_arrows[4] = new GameSprite(textures.findRegion("dpad_top2"));
    m_arrows[5] = new GameSprite(textures.findRegion("dpad_right2"));
    m_arrows[6] = new GameSprite(textures.findRegion("dpad_bottom2"));
    m_arrows[7] = new GameSprite(textures.findRegion("dpad_left2"));

    m_buttons[0] = new GameSprite(textures.findRegion("jumpButton"));
    m_buttons[1] = new GameSprite(textures.findRegion("fireButton"));
    m_buttons[2] = new GameSprite(textures.findRegion("menuButton"));

    m_arrows[0].setOrigin(0,0);
    m_arrows[1].setOrigin(0,0);
    m_arrows[2].setOrigin(0,0);
    m_arrows[3].setOrigin(0,0);
    m_arrows[4].setOrigin(0,0);
    m_arrows[5].setOrigin(0,0);
    m_arrows[6].setOrigin(0,0);
    m_arrows[7].setOrigin(0,0);

    m_buttons[0].setOrigin(0,0);
    m_buttons[1].setOrigin(0,0);
    m_buttons[2].setOrigin(0,0);

    /*
    m_arrows[0].setRotation(270); // up
    m_arrows[1].setRotation(180); //right
    m_arrows[2].setRotation(90); //down
    m_arrows[3].setRotation(0); //left
    */

    this.setOpacity(0.2f);

    this.add(m_arrows[0]);
    this.add(m_arrows[1]);
    this.add(m_arrows[2]);
    this.add(m_arrows[3]);
    this.add(m_arrows[4]);
    this.add(m_arrows[5]);
    this.add(m_arrows[6]);
    this.add(m_arrows[7]);

    this.add(m_buttons[0]);
    this.add(m_buttons[1]);
    this.add(m_buttons[2]);

    this.setDPadSize(1);


  }

  public void setDPadSize(int sz)
  {
    if (sz > 4)
      sz = 0;

    m_size = sz;
    if (sz == 0)
    {
      m_scaleUp = 0.8f;
    } else if (sz == 1)
    {
      m_scaleUp = 1.0f;
    } else if (sz == 2)
    {
      m_scaleUp = 1.1f;
    } else if (sz == 3)
    {
      m_scaleUp = 1.25f;
    } else if (sz == 4)
    {
      m_scaleUp = 1.5f;
    } 

    m_scaleDown = m_scaleUp * 1.1f;

    m_arrows[0].setScale(m_scaleUp);
    m_arrows[1].setScale(m_scaleUp);
    m_arrows[2].setScale(m_scaleUp);
    m_arrows[3].setScale(m_scaleUp);
    m_arrows[4].setScale(m_scaleUp);
    m_arrows[5].setScale(m_scaleUp);
    m_arrows[6].setScale(m_scaleUp);
    m_arrows[7].setScale(m_scaleUp);

    m_buttons[0].setScale(m_scaleUp);
    m_buttons[1].setScale(m_scaleUp);
    m_buttons[2].setScale(m_scaleUp);

    this.setPosition(this.getX(), this.getY());

  }

  public void setOpacity(float o)
  {
    m_arrows[0].setOpacity(o);
    m_arrows[1].setOpacity(o);
    m_arrows[2].setOpacity(o);
    m_arrows[3].setOpacity(o);
    m_arrows[4].setOpacity(o);
    m_arrows[5].setOpacity(o);
    m_arrows[6].setOpacity(o);
    m_arrows[7].setOpacity(o);

    m_buttons[0].setOpacity(o);
    m_buttons[1].setOpacity(o);
    m_buttons[2].setOpacity(o);
  }

  public void increaseSize()
  {
    this.setDPadSize(m_size+1);
  }

  public void setPosition(float x, float y)
  {
    super.setPosition(x,y);

    m_arrows[3].setPosition(x, y + (82 * m_scaleUp));
    m_arrows[7].setPosition(x, y);

    m_arrows[2].setPosition(x + (92 * m_scaleUp),y);
    m_arrows[6].setPosition(x + (92 * m_scaleUp)-20,y);

    m_arrows[1].setPosition(x + (129 * m_scaleUp), m_arrows[3].getY());
    m_arrows[5].setPosition(m_arrows[1].getX() + 32 , y);

    m_arrows[0].setPosition(m_arrows[2].getX(), y + (129 * m_scaleUp));
    m_arrows[4].setPosition(m_arrows[6].getX(), y + (154 * m_scaleUp));

    m_buttons[0].setPosition(1280 - 100 * m_scaleUp - 8, 5);
    m_buttons[1].setPosition(m_buttons[0].getX() - 95 * m_scaleUp, m_buttons[0].getY());

    m_buttons[2].setPosition(1280 - 100*m_scaleUp - 8, 720 - 120*m_scaleUp);

  }

  public boolean isDirectionPressed(int direction, float xx, float yy)
  {
    boolean r = m_arrows[direction].getBoundingRectangle().contains(xx,yy);
    if (r == true)
      return true;

    return m_arrows[direction+4].getBoundingRectangle().contains(xx,yy);

  }

  public boolean isButtonPressed(int button, float xx, float yy) // 0 = jump, 1 = fire
  {
    boolean r = m_buttons[button].getBoundingRectangle().contains(xx,yy);
    if (r == true)
      return true;

    return false;
    //return m_arrows[direction+4].getBoundingRectangle().contains(xx,yy);
  }
}
