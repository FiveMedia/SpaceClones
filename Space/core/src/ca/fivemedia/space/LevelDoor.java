package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import ca.fivemedia.gamelib.*;


public class LevelDoor extends GameSprite {

  protected TextureRegion unlockedSprite, lockedSprite;
  boolean m_stage;
  int m_level;
  boolean m_locked;
  protected GameText m_label;
  int m_cloneCount = 0;
  GameSprite[] m_clones = null;
  GameAnimateable[] m_cloneCollectAnimation;
  TextureRegion m_cloneFullRegion, m_cloneEmptyRegion;

  public  LevelDoor(TextureAtlas myTextures, TextureAtlas textures, boolean isStage, int level, GameContainer parent, BitmapFont font, String title) {
    super(myTextures.findRegion("door_locked"));
    lockedSprite = myTextures.findRegion("door_locked");
    unlockedSprite = myTextures.findRegion("door_unlocked");
    m_level = level;
    m_stage = isStage;
    m_locked = true;
    m_label = new GameText(font,250);
    m_label.setText(title);
    parent.add(m_label);

    if (isStage == false)
    {
      m_clones = new GameSprite[3];
      m_cloneFullRegion = textures.findRegion("bird_full");
      m_cloneEmptyRegion = textures.findRegion("bird_empty");

      m_cloneCollectAnimation = new GameAnimateable[3];

      for (int i =0; i < 3; i++)
      {
          m_clones[i] = new GameSprite(textures.findRegion("bird_empty"));
          m_clones[i].setScale(0.8f);
          parent.add(m_clones[i]);

          AnimateDelay d1 = new AnimateDelay(1.5f);
          AnimateRotateTo r1 = new AnimateRotateTo(0.3f,0f,359.9f, 1);
          AnimateRotateTo r2 = new AnimateRotateTo(0.2f,0f,359.9f, 2);
          AnimateRotateTo r3 = new AnimateRotateTo(0.3f,0f,359.9f, 2);
          AnimateRotateTo r4 = new AnimateRotateTo(0.6f,0f,360f, 1);
          AnimateRotateTo r5 = new AnimateRotateTo(0.9f,0f,360f, 1);
          AnimateSpriteFrame s1 = new AnimateSpriteFrame(textures, new String[] {"bird_full"}, 0.01f, 1);

          GameAnimateable[] a = {d1,r1,r2,s1,r3,r4,r5};
          m_cloneCollectAnimation[i] = new GameAnimationSequence(a,1);
      }
    }
  }

  public void unlock()
  {
    this.setRegion(unlockedSprite);
    m_locked = false;
  }

  public void setClones(int clones)
  {
    if (clones < 0)
    {
      m_clones[0].setVisible(false);
      m_clones[1].setVisible(false);
      m_clones[2].setVisible(false);
      return;
    }

    m_cloneCount = clones;
    for (int i=0; i < 3; i++)
    {
      if (i < (clones))
      {
        m_clones[i].setRegion(m_cloneFullRegion);
      }
      else
      {
        m_clones[i].setRegion(m_cloneEmptyRegion);
      }
    }

  }

  public void animateClones(int clones)
  {
    for (int i=0; i < 3; i++)
    {
      if (i < clones)
      {
        m_clones[i].runAnimation(m_cloneCollectAnimation[i]);
      }
      else
      {
        m_clones[i].setRegion(m_cloneEmptyRegion);
      }
    }
    m_cloneCount = clones;

    this.playSound("showclones", 0.5f);
  }

  public int getLevel()
  {
    return m_level;
  }

  public boolean isLocked()
  {
    return m_locked;
  }

  public boolean isStage()
  {
    return m_stage;
  }

  public void setPosition(float x, float y) { 
    super.setPosition(x,y);
    //m_label.setPosition(x+this.getWidth()/2-125,y+168);
    if (m_stage)
    {
      m_label.setPosition(x+this.getWidth()/2-125,y+170);
    }else
    {
      m_label.setPosition(x+this.getWidth()/2-125,y+210);
    }

    if (m_clones != null)
    {
      float lx = x+this.getWidth()/2 - 64-(m_clones[0].getWidth()/2);
      m_clones[0].setPosition(lx,y+123);
      m_clones[1].setPosition(lx+64,y+138);
      m_clones[2].setPosition(lx+128,y+123);
    }
  }

}

