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

public class GameButton extends GameSprite{

  GameText m_label;
  boolean m_selected = false;
  TextureRegion m_selectedRegion, m_notSelectedRegion;
  GameAnimateable m_selectedAnimation;

  public GameButton(TextureAtlas textures, String text, BitmapFont font)
  {
    super(textures.findRegion("button_notSelected"));
    m_selectedRegion = textures.findRegion("button_selected");
    m_notSelectedRegion = textures.findRegion("button_notSelected");

    m_label = new GameText(font, this.getWidth());
    m_label.setText(text);

    AnimateScaleTo g = new AnimateScaleTo(0.45f, 1.0f, 1.0f, 1.08f, 1.08f);
    AnimateDelay d = new AnimateDelay(0.1f);
    AnimateScaleTo s = new AnimateScaleTo(0.40f, 1.08f, 1.08f, 1.0f, 1.0f);
    GameAnimateable[] a = {g,d,s};
    m_selectedAnimation = new GameAnimationSequence(a,-1);
  }

  public boolean isSelected()
  {
    return m_selected;
  }

  public void setSelected(boolean v)
  {
    m_selected = v;
    if (m_selected)
    {
      this.stopAllAnimations();
      this.setRegion(m_selectedRegion);
      this.runAnimation(m_selectedAnimation);
    } else
    {
      this.setRegion(m_notSelectedRegion);
      this.stopAllAnimations();
    }
  }

  public void setPosition(float xx, float yy)
  {
    super.setPosition(xx,yy);
    m_label.setPosition(xx, yy+58);
  }


  @Override
  public void draw(SpriteBatch s)
  {
    //Gdx.app.log("INFO", "Draw GameSprite called. m_opacity = " + m_opacity);
    super.draw(s, m_opacity);
    m_label.draw(s);
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

