

package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import ca.fivemedia.gamelib.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;
import box2dLight.*;

public class Seeker extends BaseSprite {

  int m_hp = 3;
  PlayerSprite m_player = null;
  float m_state = 0;
  float m_speed = 1.0f;
  float m_time = 0;
  MainGameLayer m_gameLayer = null;
  GameAnimateable  m_hitAnimation = null;
  long m_flySound = -1;
  AnimateSpriteFrame m_screamAnimation;
  Light pLight;
  boolean m_onScreenTrigger = false;
  boolean m_motionActive = true;

  public Seeker(TextureAtlas myTextures, TiledMapTileLayer pTiles, TiledMapTileLayer lTiles, PlayerSprite player, MainGameLayer layer) {
    super(myTextures.findRegion("seeker_F1"),pTiles,lTiles);
    m_moveController = null;
    m_gameLayer = layer;

    //m_walkAnimation = new AnimateSpriteFrame(myTextures, new String[] {"seeker_F1",}, 1, -1);
    m_screamAnimation = new AnimateSpriteFrame(myTextures, new String[] {"seeker_F1","seeker_F2", "seeker_F2", "seeker_F2", "seeker_F2", "seeker_F2", "seeker_F2", "seeker_F2", "seeker_F2", "seeker_F1"}, 2.15f, 1);
    m_player = player;
    m_spawnAnimation = new AnimateFadeIn(0.02f);

    //m_standardDeathAnimation = new AnimateSpriteFrame(myTextures, new String[] {"BatN_Dead"}, 0.5f, 1);
    //m_deathAnimation = m_standardDeathAnimation;

    m_numSounds = 1;
    m_soundPrefix = "seeker";
    m_fallOnDead = false;
    m_deathSoundVolume = 0.4f;

    pLight = layer.createPointLight(new Color(0.2f,0.75f, 0.2f, 0.6f), 350, 0,0);
    pLight.setActive(false);
    pLight.setXray(true);

  }

  public void move()
  {


    m_time -= m_deltaTime;

    
    if (m_player.isVisible() && m_motionActive)
    {
      //attacking player
      pLight.setActive(true);
      if (m_time <= 0)
      {
        m_time = 0;
        if (this.getX() > m_player.getX())
        {
          this.setScale(-1,1);
          m_dx -= 0.1f;
          if (m_dx < -6f*m_speed)
          {
            m_dx = -6f*m_speed;
            m_time = 0.3f;
          }
        } else
        {
          this.setScale(1,1);
          m_dx += 0.1f;
          if (m_dx > 6f*m_speed)
          {
            m_dx = 6f * m_speed;
            m_time = 0.3f;
          }
        }
      }

      if (this.getY() > m_player.getY())
      {
        m_dy -= 0.1f;
      } else
      {
        m_dy += 0.1f;
      }

      if (m_dy > (5.5f * m_speed))
        m_dy = 5.5f * m_speed;
      else if (m_dy < (-5.5f * m_speed))
        m_dy = -5.5f * m_speed;

    } else
    {
      pLight.setActive(false);

      if (m_dx > 0)
      {
        m_dx = m_dx - 0.2f;
      } else if (m_dx < 0)
      {
        m_dx = m_dx + 0.2f;
      }

      if (m_dy > 0)
        m_dy = m_dy - 0.2f;
      else if (m_dy < 0)
        m_dy = m_dy + 0.2f;

      if (m_motionActive == false)
      {
        if (m_player.isVisible())
        {
          if (m_parent.isOnScreen(this.getX(), this.getY()))
          {
            m_motionActive = true;
          }
        }
      }
    }

    pLight.setPosition(this.getX() + this.getWidth()/2, this.getY() + this.getHeight());
  } 


  public void drop(float dur, float dx, float speed)
  {
    m_state = 1;
    m_time = dur;
    m_dx = dx;
    m_dy = -1.5f;
    m_speed = speed;
  }

  @Override
  public void hitPlayer(PlayerSprite player)
  {

  }

  public void scream()
  {
    if (m_screamAnimation.isRunning() == false)
    {
      this.playSoundPanVolume("seeker1", 0.8f, 0.6f);
      m_soundIgnoreTicks = 350;
      this.runAnimation(m_screamAnimation);
    }
  }

  public void hitByAttack()
  { 
    return;
  }

  public void playSpriteSound()
  {

    if ((m_pause) || (m_player.isVisible() == false))
      return;

    if (m_soundIgnoreTicks > 0)
        m_soundIgnoreTicks--;

    if (m_parent.isOnScreen(this.getX() + this.getWidth()/2, this.getY()+30))
    {

      if (m_soundIgnoreTicks < 1)
      {
        int r = getRandom(5);
        if (r == 2)
        {
            this.playSoundPanVolume("seeker1", 0.8f, 0.6f);
            m_soundIgnoreTicks = 350;
            this.runAnimation(m_screamAnimation);
        } else
        {
          m_soundIgnoreTicks = 70;
        }
      }
    }
  }

  public void die()
  {
    return;

  }

  public void resetLevel()
  {

  }

  public void setTrigger(int trigger)
  {
    if (trigger == 10)
    {
      m_onScreenTrigger = true;
      m_motionActive = false;
    }
  }


  public void spawn()
  {


  }
}