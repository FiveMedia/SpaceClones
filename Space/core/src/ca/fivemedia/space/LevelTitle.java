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

public class LevelTitle extends GamePanel {

  protected GameText m_title;
  protected GameSprite m_back;
  protected AnimateScaleTo m_scaleAnimationIn, m_scaleAnimationOut;

  public  LevelTitle (BitmapFont font) 
  {
    super();
    m_back = new GameSprite(new Texture("level_title_back.png"));
    this.add(m_back);
    m_title = new GameText(font,710);
    this.add(m_title);
    //m_scaleAnimationIn = new AnimateScaleTo(0.5f, 1.0f, 0.05f, 1.0f, 1.0f);
    //m_scaleAnimationOut = new AnimateScaleTo(0.5f, 1.0f, 1.0f, 1.0f, 0.05f);
  }

  public void setTitle(String title)
  {
    m_title.setText(title);
  }

  public void setPosition(float x, float y)
  {
    super.setPosition(x,y);
    m_back.setPosition(x,y);
    m_title.setPosition(x+20,y+68);
  }

}
