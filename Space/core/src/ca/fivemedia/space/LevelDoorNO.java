package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import ca.fivemedia.gamelib.*;


public class LevelDoorNO extends GameSprite {

  protected TextureRegion unlockedSprite, lockedSprite;
  boolean m_stage;
  int m_level;
  boolean m_locked;
  protected GameText m_label;
  int m_birdCount = 0;
  GameSprite[] m_birds = null;
  GameAnimateable[] m_birdCollectAnimation;
  TextureRegion m_birdFullRegion, m_birdEmptyRegion;

  public  LevelDoorNO(TextureAtlas myTextures, TextureAtlas textures, boolean isStage, int level, GameContainer parent, BitmapFont font, String title) {
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
      m_birds = new GameSprite[3];
      m_birdFullRegion = textures.findRegion("bird_full");
      m_birdEmptyRegion = textures.findRegion("bird_empty");

      m_birdCollectAnimation = new GameAnimateable[3];

      for (int i =0; i < 3; i++)
      {
          m_birds[i] = new GameSprite(textures.findRegion("bird_empty"));
          m_birds[i].setScale(0.8f);
          parent.add(m_birds[i]);

          AnimateDelay d1 = new AnimateDelay(1.5f);
          AnimateRotateTo r1 = new AnimateRotateTo(0.3f,0f,359.9f, 1);
          AnimateRotateTo r2 = new AnimateRotateTo(0.2f,0f,359.9f, 2);
          AnimateRotateTo r3 = new AnimateRotateTo(0.3f,0f,359.9f, 2);
          AnimateRotateTo r4 = new AnimateRotateTo(0.6f,0f,360f, 1);
          AnimateRotateTo r5 = new AnimateRotateTo(0.9f,0f,360f, 1);
          AnimateSpriteFrame s1 = new AnimateSpriteFrame(textures, new String[] {"bird_full"}, 0.01f, 1);

          GameAnimateable[] a = {d1,r1,r2,s1,r3,r4,r5};
          m_birdCollectAnimation[i] = new GameAnimationSequence(a,1);
      }
    }
  }

  public void unlock()
  {
    m_locked = true;
  }

  public void setBirds(int birds)
  {
    birds = 0;
    if (birds < 0)
    {
      m_birds[0].setVisible(false);
      m_birds[1].setVisible(false);
      m_birds[2].setVisible(false);
      return;
    }

    m_birdCount = birds;
    for (int i=0; i < 3; i++)
    {
      if (i < (birds))
      {
        m_birds[i].setRegion(m_birdFullRegion);
      }
      else
      {
        m_birds[i].setRegion(m_birdEmptyRegion);
      }
    }

  }

  public void animateBirds(int birds)
  {
    for (int i=0; i < 3; i++)
    {
      if (i < birds)
      {
        m_birds[i].runAnimation(m_birdCollectAnimation[i]);
      }
      else
      {
        m_birds[i].setRegion(m_birdEmptyRegion);
      }
    }
    m_birdCount = birds;

    this.playSound("showBirds", 0.5f);
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

    if (m_birds != null)
    {
      float lx = x+this.getWidth()/2 - 64-(m_birds[0].getWidth()/2);
      m_birds[0].setPosition(lx,y+123);
      m_birds[1].setPosition(lx+64,y+138);
      m_birds[2].setPosition(lx+128,y+123);
    }
  }

}

