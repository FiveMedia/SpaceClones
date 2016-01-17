package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import ca.fivemedia.gamelib.*;
import java.util.Random;
import com.badlogic.gdx.math.*;

public abstract class BaseSprite extends GameSprite {

  protected float m_dx, m_dy;
  float defaultMaxSpeedX = 7f;
  protected float m_startX = -1;
  protected float m_startY = -1;
  float lastMoveX, lastMoveY;
  protected TiledMapTileLayer m_platformTiles;
  protected TiledMapTileLayer m_climbableTiles;
  int ticks = 0;
  int[] col = new int[9];
  float maxSpeedX = 7.0f;
  float maxSpeedY = 5.0f;
  float maxFallVelocity = 14.0f;
  float m_horizontalDragFactor = 0.5f;
  float m_climbingDragFactor = 0.25f;
  float m_gravity = 1.0f;
  boolean m_jumping;
  boolean m_falling = false;
  boolean m_climbing;
  float tw, th;
  boolean m_onGround = true;
  GameAnimateable m_standardDeathAnimation, m_deathAnimation;
  GameAnimation m_spawnAnimation, m_flattenAnimation, m_levelEndingAnimation, m_spinAnimation;
  protected AnimateSpriteFrame m_walkAnimation;
  boolean m_dying = false;
  boolean m_alive = true;
  boolean m_spawning = false;
  float m_currDir = 1;
  protected MoveController m_moveController = null;
  float m_originalOriginY;
  protected String m_soundPrefix = null;
  protected int m_numSounds = 0;
  private Random m_random = new Random();
  int m_soundIgnoreTicks = 0;
  int m_lastSoundNum = 1;
  protected int m_pauseMoveTicks = 0;
  boolean m_levelEnding = false;
  boolean m_levelEndingFading = false;
  float m_machineX, m_machineY;
  protected int m_hp = 1;
  protected boolean m_wasSpawned = false;
  protected boolean m_fallOnDead = false;
  protected boolean m_deadFalling = false;
  protected float m_deltaTime;
  protected Circle m_circle = null;
  float m_circleOffsetX = 0;
  float m_circleOffsetY = 0;
  public boolean m_playSpawnSound = false;
  protected float m_deathSoundVolume = 0.5f;
  protected boolean m_hideOnDead = true;
  private Vector2 m_distCalc = new Vector2();
  protected PlayerSprite m_internalPlayer = null;
  boolean m_ignoreDie = false;

	public  BaseSprite (TextureRegion region, TiledMapTileLayer platformTiles, TiledMapTileLayer climbableTiles) {
    super(region);
    m_platformTiles = platformTiles;
    m_climbableTiles = climbableTiles;
    m_dx = 0;
    m_dy = 0;
    tw = 48.0f;
    th = 32.0f;
    m_standardDeathAnimation = new AnimateFadeOut(0.4f);
    m_deathAnimation = m_standardDeathAnimation;

//float duration, float fromScaleX, float fromScaleY, float toScaleX, float toScaleY
    m_flattenAnimation = new AnimateScaleTo(0.3f, 1.0f, 1.0f, 1.0f, 0.2f);
    m_originalOriginY = this.getOriginY();

    m_spawnAnimation = new AnimateFadeIn(1.0f);
    m_levelEndingAnimation = new AnimateScaleTo(2.0f, 1.0f, 1.0f, 0.05f, 0.05f);
    m_spinAnimation = new AnimateRotateTo(0.25f,0,359,-1);
  }

  public void setPlayer(PlayerSprite internalPlayer)
  {
    m_internalPlayer = internalPlayer;
  }

  public void setTrigger(int trigger)
  {
    if (m_moveController != null)
      m_moveController.setTriggerType(trigger);
  }

  public void setIgnoreDie()
  {
    m_ignoreDie = true;
    if (m_moveController != null)
    {
      m_moveController.setIgnoreDie();
    }
  }

  public void setIgnoreReverse()
  {
    if (m_moveController != null)
    {
      m_moveController.setIgnoreReverse();
    }
  }

  //returns 1 to max inclusive
  public int getRandom(int max)
  {
    return m_random.nextInt(max) + 1;
  }

  public int getHP()
  {
    return 1;
  }

  public boolean isCircle()
  {
    if (m_circle != null)
      return true;

    return false;
  }

  public void setSpriteTrigger(GameSprite sprite, float range)
  {
    if (m_moveController != null)
    {
      m_moveController.setSpriteTrigger(sprite, range);
    }
  }

  public void setStartPosition(float xx, float yy)
  {
    m_startX = xx;
    m_startY = yy;
  }

  @Override
  public void update(float deltaTime)
  {

    m_deltaTime = deltaTime;

    if (m_pause)
      return;

    if (m_levelEnding)
    {
      if (m_levelEndingFading == false)
      {
        //this.translate(m_dx, m_dy);
        float deltaX = Math.abs(m_machineX - this.getX());
        float deltaY = Math.abs(m_machineY - this.getY());
        if ((deltaX < (Math.abs(m_dx)+16)) && (deltaY < (Math.abs(m_dy)+16)))
        {
          m_levelEndingFading = true;
          this.runAnimation(m_levelEndingAnimation);
        }
      } else
      {
        if (m_levelEndingAnimation.isRunning() == false)
        {
          this.setVisible(false);
          m_alive = false;
        }
      }
    }
    else if (m_spawning)
    {
      if (!m_spawnAnimation.isRunning())
      {
        m_spawning = false;
      }
    }
    else if (m_dying == false)
    {
      //ticks++;
      if (m_pauseMoveTicks > 0)
        m_pauseMoveTicks--;

      if (m_pauseMoveTicks < 1)
      {
        this.move();
        this.translate(m_dx,m_dy);
        if ((m_dx != 0) || (m_dy != 0))
        {
          lastMoveX = m_dx;
          lastMoveY = m_dy;
        } 
      }
    } else
    {
      if (m_deadFalling)
      {
        if (m_moveController != null)
            m_moveController.fallDead(this, m_platformTiles);

        this.translate(m_dx,m_dy);
        if (m_dy == 0)
        {
          this.setVisible(false);
          m_alive = false;
          if (m_moveController != null)
            m_moveController.reset();
        }

      } else if (m_deathAnimation.isRunning() == false)
      {
        if (!m_fallOnDead)
        {
          if (m_hideOnDead)
            this.setVisible(false);

          m_alive = false;
          if (m_moveController != null)
            m_moveController.reset();
        } else
        {
          m_deadFalling = true;
          this.playDeathSound();
          //this.playSound(m_soundPrefix + "Death", m_deathSoundVolume); 
          if (m_moveController != null)
            m_moveController.fallDead(this, m_platformTiles);

          this.translate(m_dx,m_dy);
        }
      } 
    }
  }

  public void setSpeed(float sp)
  {
    maxSpeedX = maxSpeedX * sp;
    defaultMaxSpeedX = maxSpeedX;
  }

  public void setSpeedY(float sp)
  {
    maxSpeedY = maxSpeedY * sp;
  }

  public abstract void move();

  public void setVelocity(float dx, float dy)
  {
    m_dx = dx;
    m_dy = dy;
  }

  public void getCollisions(int tileX, int tileY)
  {
    this.getCollisions(tileX, tileY, m_platformTiles);
  }

  public int getCellAt(int tileX, int tileY)
  {
    TiledMapTileLayer.Cell c = m_platformTiles.getCell(tileX,tileY);
    if (c != null)
    {
      return c.getTile().getId();
    }

    return -1;
  }

  public void getCollisions(int tileX, int tileY, TiledMapTileLayer tiles)
  {
    int i = 0;
    for (int cy = 0; cy < 3; cy++)
    {
      int ty = tileY + cy;
      for (int cx=0; cx < 3; cx++)
      {
          int tx = tileX + cx;
          TiledMapTileLayer.Cell c = tiles.getCell(tx,ty);
          if (c != null)
          {
            col[i] = c.getTile().getId();
          } else 
          {
            col[i] = -1;
          }
          i++;
      }
    }
  }


  public void accelerate (float ax, float ay)
  {

    m_dx += ax;
    m_dy += ay;

    if (Math.abs(m_dx) > maxSpeedX)
    {
      m_dx = maxSpeedX * (m_dx/Math.abs(m_dx));
    }

    if (m_climbing)
    {
      if (Math.abs(m_dy) > maxSpeedY)
      {
        m_dy = maxSpeedY * (m_dy/Math.abs(m_dy));
      }
    } else
    {
      if (m_dy < -maxFallVelocity)
      {
        m_dy = -maxFallVelocity;
      }
    }
  }

  public float applyDrag(float delta, float drag)
  {

    if (delta != 0)
    {
        if (delta > 0)
        {
          delta = delta - drag;
          if (delta < 0)
            delta = 0;

        } else if (delta < 0)
        {
          delta = delta + drag;
          if (delta > 0)
            delta = 0;
        }
    }

    return delta;
  }

  public int dirFacing()
  {

    float tx = m_dx;
    float ty = m_dy;

    if ((tx == 0) && (ty == 0))
    {
      tx = lastMoveX;
      ty = lastMoveY;
    }

    if (tx > 0)
      return 1;

    if (tx < 0)
        return 3;

    if (ty > 0)
      return 0;

    if (ty < 0)
        return 2;

      return 1;

  }

  public boolean isMoving()
  {
    if ((m_dx != 0) || (m_dy != 0))
      return true;

    return false;
  }

  public float getDX()
  {
    return m_dx;
  }

  public void hitByAttack()
  {
    if ((m_dying == false) && (m_alive))
    {
      this.stopAllAnimations();
      this.runAnimation(m_deathAnimation);
      m_dying = true;
      this.playDeathSound();
    }
  }

  public void die()
  {
    if ((m_dying == false) && (m_alive))
    {
      this.stopAllAnimations();
      this.runAnimation(m_deathAnimation);
      m_dying = true;
    }
  }

  public void hitByBlock()
  {
    this.stopAllAnimations();
    m_deathAnimation = m_flattenAnimation;
    this.setOrigin(this.getOriginX(), 0);
    this.runAnimation(m_deathAnimation);
    m_dying = true;
    float v  = 0.4f;
    if (m_internalPlayer != null)
      v = this.calculateVolume(this, m_internalPlayer, 0.5f, 0.1f);

    this.playSound("plasmaCubeCrush", v);
  }

  public boolean isAlive()
  {
    return m_alive;
  }

  public void setAlive(boolean a)
  {
    m_alive = a;
  }

  public boolean isCollidable()
  {
    return (m_alive && !m_dying && !m_spawning && this.isVisible());
  }

  public void spawn()
  {
    if (m_moveController != null)
      m_moveController.reset();

    m_wasSpawned = true;
    m_deadFalling = false;
    this.stopAllAnimations();
    this.setScale(1.0f * m_currDir, 1.0f);

    this.setOrigin(this.getOriginX(), m_originalOriginY);

    if (m_walkAnimation != null)
      m_walkAnimation.setStartFrame();

    m_deathAnimation = m_standardDeathAnimation;

    this.setVisible(true);
    m_alive = true;
    m_dying = false;
    m_spawning = true;

    this.runAnimation(m_spawnAnimation);
    m_dx = 0;
    m_dy = 0;

    if (m_playSpawnSound && (m_parent.isOnScreen(this.getX(), this.getY())))
    {
      float v = 0.25f;
      if (m_internalPlayer != null)
      {
        //v = this.calculateVolume(this, m_internalPlayer,0.35f, 0.1f);
        //Gdx.app.debug("BaseSprite", "calcVolume=" + v);
        this.playSound(m_soundPrefix + "_spawn", m_internalPlayer, this, 0.4f, 0.1f);
      } else
      {
        this.playSound(m_soundPrefix + "_spawn", v);
      }
    }

    //Gdx.app.debug("BaseSprite", "Spawning m_currDir=" + m_currDir);
  }

  public void setDirection(float f)
  {
    m_currDir = f;
    if (m_moveController != null)
      m_moveController.setDirection(f);
  }

  public void setStartDirection(float f)
  {
    this.setDirection(f);
    if (m_moveController != null)
      m_moveController.setStartDirection(f);
  }

  public void playSoundIfOnScreen(String sound, float v)
  {
    if (m_parent.isOnScreen(this.getX(), this.getY()))
    {
      this.playSound(sound,v);
    }
  }

  public void playSoundIfOnScreen(String sound, float max, float min)
  {
    if (m_parent.isOnScreen(this.getX(), this.getY()))
    {
      this.playSound(sound,m_internalPlayer, this, max, min);
    }
  }

  public void playSoundPanVolume(String sound, float max, float min)
  {
    if (m_internalPlayer != null)
      this.playSound(sound,m_internalPlayer, this, max, min);
    else
      this.playSound(sound, max);
  }

  public void playSpriteSound()
  {
    if (m_soundPrefix == null)
      return;

    if (m_pause)
      return;

    if (m_soundIgnoreTicks > 0)
        m_soundIgnoreTicks--;

    if (m_parent.isOnScreen(this.getX(), this.getY()))
    {

      if (m_soundIgnoreTicks < 1)
      {
        int r = getRandom(180);
        float v = (float)getRandom(4);
        if (r == 5)
        {
          if (m_numSounds > 0)
          {
            r = getRandom(m_numSounds);
            m_lastSoundNum = r;
            //Gdx.app.debug("playSpriteSound", "SOUND: " + m_soundPrefix + r);
            v = 0.3f + (v*0.05f);
            if (m_internalPlayer != null)
            {
              this.playSoundIfOnScreen(m_soundPrefix + r, v, 0.05f);
            } else
              this.playSound(m_soundPrefix + r, v);

            m_soundIgnoreTicks = 600 + getRandom(200);
          }
        }
      }
    }
  }

  public void playDeathSound()
  {
    if (m_soundPrefix != null)
    {
      if (m_numSounds > 0)
      {
        this.stopSound(m_soundPrefix + m_lastSoundNum);
      }

      if (m_deathSoundVolume > 0.85f)
        this.playSound(m_soundPrefix + "Death", m_deathSoundVolume);
      else
        this.playSoundIfOnScreen(m_soundPrefix + "Death", m_deathSoundVolume, 0.35f);
    }
  }

  public void hitPlayer(PlayerSprite player)
  {
    if (m_pauseMoveTicks < 1)
    {
      m_pauseMoveTicks = 60;
      if (player.getX() > this.getX())
      {
        this.translate(-24,0);
      } else
      {
        this.translate(24,0);
      }
    }
  }

  public void endLevel(float mx, float my)
  {
    m_levelEnding = true;

    m_machineX = mx;
    m_machineY = my;


    if ((m_soundPrefix != null) && (m_numSounds > 0))
      this.stopSound(m_soundPrefix + m_lastSoundNum);

    this.runAnimation(m_spinAnimation);
    this.runAnimation(new AnimateMoveTo (2.5f, this.getX(), this.getY(), m_machineX, m_machineY));
  }

  public void resetLevel()
  {
    if (m_wasSpawned)
    {
      m_dx = 0;
      m_dy = 0;
      this.stopAllAnimations();
      this.setVisible(false);
      m_dying = false;
      m_alive = false;
      m_spawning = false;
      m_pauseMoveTicks = 0;

      if (m_soundPrefix != null)
        this.stopSound(m_soundPrefix + m_lastSoundNum);
    }
  }

  public void triggerMotion(int group)
  {
    //Gdx.app.debug("BaseSprite", "triggerMotion called with  groupId = " + group + "myid= " + m_groupId);
    if (m_groupId > 0)
    {

      if (group == m_groupId)
      {
        //Gdx.app.debug("BaseSprite", "groupId = " + group + " match!");
        if (m_moveController != null)
          m_moveController.triggerMotion();
      }
    }
  }

  public Rectangle getBoundingRectangleAttack()
  {
    return this.getBoundingRectangle();
  }

  public Circle getBoundingCircle()
  {
    if (m_circle == null)
      return null;

   // Rectangle box = super.getBoundingRectangle();
    //m_circle.x = box.x + m_circleOffsetX;
    //m_circle.y = box.y + m_circleOffsetY;
    m_circle.x = this.getX() + this.getOriginX();
    m_circle.y = this.getY() + this.getOriginY();

    return m_circle;
  }

  public float calculateVolume(BaseSprite source, BaseSprite target, float max, float min)
  {

    m_distCalc.set(source.getX(), source.getY());
    float d = m_distCalc.dst(target.getX(), target.getY());

    if (d > 1200)
      return 0;

    if (d > 500)
      d = 500;

    float v = max - ((max-min) * d / 500f);

    if (v > min)
      return v;

    float dy = Math.abs(source.getY()) - Math.abs(target.getY());
    if (dy > 500)
      return 0;
    return v;
  }

}

