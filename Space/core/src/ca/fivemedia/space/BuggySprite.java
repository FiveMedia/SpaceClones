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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import box2dLight.*;

public class BuggySprite extends BaseSprite {

  protected TextureRegion standRegion;
  protected AnimateSpriteFrame m_standAnimation, m_climbAnimation, m_jumpAnimation, m_hoverAnimation, m_attackAnimation, m_hitAnimation, m_idleAnimation, m_tutorialAnimation;
  protected GameAnimateable m_throughDoorAnimation,m_invincibleAnimation;
  protected GameAnimateable m_startHoverAnimation = null;
  boolean m_jumping;
  int m_jumpTicks = 0;
  boolean m_onLadder = false;
  boolean m_ladderBelow = false;
  boolean m_ladderAbove = false;
  protected GameInputManager m_inputManager;
  boolean m_attacking = false;
  int m_attackTicks = 0;
  int m_attackPauseTicks = 0;
  WeaponInterface m_activeWeapon;
  float m_currDir;
  boolean m_slip = false;
  int m_lives = 5;
  int m_energy = 3;
  int m_ignoreHits = 0;
  boolean m_inHit = false;
  boolean m_doubleJumping = false;
  boolean m_doubleJumpPossible = false;
  int m_deadState = 1;
  int m_deadTicks = 0;
  float m_checkX, m_checkY;
  ArrayList<PlatformSprite> m_platforms;
  PlatformSprite m_collisionPlatform = null;
  PlatformSprite m_activePlatform = null;
  boolean m_onPlatform = false;
  Rectangle m_boundingBox = new Rectangle();
  float m_boundOffX = 0f;
  boolean m_inSwamp = false;
  int m_slipTicks = 60;
  int m_iter  = 0;
  int m_idleTicks = 0;
  Light pLight;

  Body m_body = null;
  BodyDef m_bodyDef = null;
  FixtureDef fixtureDef = null;
  GameSprite m_hoverSprite = null;

  public boolean m_focus = true;

  boolean m_hover = false;
  MainGameLayer m_gameLayer;
  boolean m_inFloat = false;

  float m_hoverMin = -20000;
  float m_hoverMax = 200000;

  int m_bounceTicks = 0;
  boolean m_bounce = false;

  boolean m_explodeDeath = false;
  boolean m_wrappedUp = false;
  AnimateSpriteFrame m_wrappedUpAnimation, m_firstWrappedAnimation,m_wrappedFreeAnimation, m_floatAnimation;
  GameAnimateable m_floatAnimation2;

  int m_wrapHP = 0;
  int m_floatTicks = 0;

  float m_hoverSpeed = 4;

  float m_floatMaxYUp = 3.5f;
  float m_floatMaxYDown = -5f;
  float m_floatImpulse = 0.45f;

  boolean m_inTeleport = false;
  int m_teleportState = 0;
  AnimateMoveTo m_teleportAnimation = null;
  GameAnimateable m_teleportOut, m_teleportIn;
  long m_heartbeatSoundId = -1;

  float m_doubleJumpDir = 0;
  boolean m_canExit = true;

  float m_lightX = 0;
  float m_lightDir = 1;

  public  BuggySprite (TextureAtlas myTextures, TiledMapTileLayer pTiles, TiledMapTileLayer lTiles, ArrayList<PlatformSprite> platforms, GameInputManager inputManager, int lives, World world, MainGameLayer gameLayer) {
    
    super(myTextures.findRegion("buggy_F1"),pTiles,lTiles);
    standRegion = myTextures.findRegion("buggy_F1");
    m_walkAnimation = new AnimateSpriteFrame(myTextures, new String[] {"buggy_F1", "buggy_F2"}, 0.3f, -1);
    m_standAnimation = new AnimateSpriteFrame(myTextures, new String[] {"buggy_F1"}, 1.0f, -1);
    
    m_attackAnimation = new AnimateSpriteFrame(myTextures, new String[] {"man_stand_F1"}, 0.3f, 1);
    
    m_hitAnimation = null;
    m_tutorialAnimation = new AnimateSpriteFrame(myTextures, new String[] {"buggy_F1"}, 0.5f, -1);
    m_idleAnimation = new AnimateSpriteFrame(myTextures, new String[] {"buggy_F1"}, 0.5f, -1);
    
    m_throughDoorAnimation = new AnimateFadeOut(0.5f);
    m_floatAnimation = new AnimateSpriteFrame(myTextures, new String[] {"man_jump_F3", "man_jump_F3"}, 0.65f, -1);

    m_teleportIn = new AnimateFadeIn(0.35f);
    m_teleportOut = new AnimateFadeOut(0.35f);

    AnimateTranslateVertical f1 = new AnimateTranslateVertical(1.0f, 0f, -3f,1);
    AnimateTranslateVertical f2 = new AnimateTranslateVertical(3.0f, 1.0f, -1f, 1);
    GameAnimateable[] af = {f1,f2};

    m_floatAnimation2 = new GameAnimationSequence(af,-1);

    AnimateFade fo = new AnimateFade(0.2f, 0.9f, 0.2f);
    AnimateDelay delay = new AnimateDelay(0.25f);
    AnimateFade fi = new AnimateFade(0.2f, 0.2f,0.9f);
    AnimateFade ff = new AnimateFade(0.25f, 0.2f, 1.0f);
    GameAnimateable[] a = {fo,fi,delay,fo,fi, delay, fo, ff};

    m_invincibleAnimation = new GameAnimationSequence(a, 1);
    m_invincibleAnimation.setIgnoreStop(true);

    m_inputManager = inputManager;
    m_platforms = platforms;

    m_activeWeapon = null;

    maxSpeedX = 6.0f;
    defaultMaxSpeedX = 6.0f;
    maxSpeedY = 4.0f;
    maxFallVelocity = 20.0f;
    m_horizontalDragFactor = 1.0f;
    m_climbingDragFactor = 0.25f;
    m_gravity = 0.8f;

    m_jumping = false;
    m_climbing = false;
    m_currDir = 1;

    m_lives = lives;
    m_boundingBox.width = this.getBoundingRectangle().width/4;
    m_boundOffX = this.getBoundingRectangle().width/2;
    m_boundingBox.height = this.getBoundingRectangle().height/5;

    m_gameLayer = gameLayer;

    this.runAnimation(m_standAnimation);

    if (gameLayer.m_stage > 1)
      pLight = gameLayer.createConeLight(new Color(0.8f,0.8f, 0.5f, 0.75f), 1100, 0, 0, 0, 25);
    else
      pLight = gameLayer.createConeLight(new Color(0.8f,0.8f, 0.5f, 0.35f), 700, 0, 0, 0, 25);

    pLight.setXray(true);
    pLight.setActive(false);
    m_lightX = this.getWidth()-20;

  }

  @Override
  public void update(float deltaTime)
  {

    if (m_pause)
      return;

    if ((m_energy != 1) && (m_heartbeatSoundId > 0))
    {
      this.stopSound("heartbeat");
      m_heartbeatSoundId = -1;
    }

    if (m_levelEnding)
    {
      if (m_heartbeatSoundId > 0)
      {
        this.stopSound("heartbeat");
         m_heartbeatSoundId = -1;
      }

      if (m_levelEndingFading == false)
      {
        this.translate(m_dx, m_dy);
        float deltaX = Math.abs(m_machineX - this.getX());
        float deltaY = Math.abs(m_machineY - this.getY());
        if ((deltaX < 15) && (deltaY < 15))
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
      if (m_energy == 1)
      {
        if (m_heartbeatSoundId < 0)
        {
          m_heartbeatSoundId = this.loopSound("heartbeat", 0.25f);
        }
      }

      float ex = 0;
      if (m_activePlatform != null)
      {
        ex = m_activePlatform.getDX();
      }

      if (m_pauseMoveTicks > 0)
        m_pauseMoveTicks--;

      if (m_inTeleport)
      {
        if (m_teleportState == 0)
        {
          if (m_teleportOut.isRunning() == false)
          {
            m_teleportState = 1;
            this.runAnimation(m_teleportAnimation);
            this.playSound("teleport", 0.75f);
          }
        } else if (m_teleportState == 1)
        {
          if (m_teleportAnimation.isRunning() == false)
          {
            m_teleportState = 2;
            this.runAnimation(m_teleportIn);
            this.playSound("teleportOut", 0.75f);
          }
        } else if (m_teleportState == 2)
        {
          if (m_teleportIn.isRunning() == false)
          {
            m_teleportState = 0;
            m_inTeleport = false;
            m_pauseMoveTicks = 0;
          }
        }
      } else if (m_pauseMoveTicks < 1)
      {
          this.move();
          this.translate(m_dx+ex,m_dy);
          if ((m_dx != 0) || (m_dy != 0))
          {
            lastMoveX = m_dx;
            lastMoveY = m_dy;
          } 
      }
    } else if (m_dying == true)
    {
      if (m_deadState == 1)
      {

        if (m_deathAnimation.isRunning() == false)
        {
          pLight.setActive(false);
          m_deadState = 2;
          if (m_explodeDeath)
          {
            m_climbing = false;
            m_explodeDeath = false;
            m_deadState = 3;
            m_deadTicks = 0;
            m_dx = 0;
            m_dy = 0;
          } else
          {
            m_climbing = false;
            m_dy = 0;
            this.accelerate(0,26f);
            maxFallVelocity = 128.0f;
            m_deadTicks = 0;
            m_dx = 0;
            this.playSound("falling",0.9f);
          }
        }
      } else if (m_deadState == 2)
      {
        m_deadTicks++;
        this.accelerate(0, -(m_gravity*1.5f));
        this.translate(m_dx,m_dy);
        if (m_deadTicks > 90)
        {
          m_alive = false;
          m_deadState = 1;
          m_dying = false;
          m_explodeDeath = false;
        }
      } else if (m_deadState == 3)
      {
        m_deadTicks++;
        if (m_deadTicks > 130)
        {
          m_alive = false;
          m_deadState = 1;
          m_dying = false;
          m_explodeDeath = false;
        }
      }
    }
  }

  public int getLives()
  {
    return m_lives;
  }

  public void addLives(int l)
  {
    m_lives += l;
    if (m_lives > 99)
      m_lives = 99;
  }

  public void setLives(int l)
  {
    m_lives = l;
  }

  public int getEnergy()
  {
    return m_energy;
  }

  public void addEnergy(int e)
  {
    m_energy += e;
    if (m_energy > 3)
      m_energy = 3;


  }

  @Override
  public void accelerate(float ax, float ay)
  {

    float maxX = maxSpeedX;
    float maxY = maxSpeedY;
    float maxFall = maxFallVelocity;


    if (m_slip)
    {
      ax = ax / 2f;
      maxX = (maxX * 2f);
    }

    m_dx += ax;
    m_dy += ay;

    if (m_bounce)
    {
      maxX = 40;
      maxY = 40;
      maxFall = 40;
    }

    if ((m_inSwamp) && (m_dying == false))
    {
      maxX = maxX / 6;
      maxFall = 4;
    }

    if (Math.abs(m_dx) > maxX)
    {
      if (!m_inSwamp)
      { 
        if (m_slip)
        {
          m_dx = maxX * (m_dx/Math.abs(m_dx));
        } else if (!m_onGround)
        {
          if (m_slipTicks > 30)
          {
            if (m_dx > 0)
              m_dx = m_dx - 0.5f;

            if (m_dx < 0)
              m_dx = m_dx + 0.5f;
          } else
          {
            if (m_dx > 0)
            {
              m_dx = (maxX * 2f);
            } else
            {
              m_dx = - (maxX * 2f);
            }
          }
        } else
        {
          if (m_dx > 0)
            m_dx = m_dx - 0.5f;

          if (m_dx < 0)
            m_dx = m_dx + 0.5f;
        }
      } else
      {
        m_dx = maxX * (m_dx/Math.abs(m_dx));
      }
    }

    if (m_climbing)
    {
      if (Math.abs(m_dy) > maxY)
      {
        m_dy = maxY * (m_dy/Math.abs(m_dy));
      }
    } else
    {
      if (m_dy < -maxFall)
      {
        m_dy = -maxFall;
      }

      if ((m_inSwamp) && (m_dying == false))
      {
        if (m_dy > 3f)
          m_dy = 3f;
      }

      if ((m_inFloat) && (m_dying == false))
      {
        if (m_dy < m_floatMaxYDown)
        {
          //m_dy += 0.25f;
          m_dy = m_floatMaxYDown;
        }
      }
    }
  }

  @Override
  public float applyDrag(float delta, float drag)
  {
    if (m_inSwamp)
    {
      drag = drag * 3;
    }

    if (m_inFloat)
    {
      drag = drag / 4f;
    }

    if (m_slip)
    {
      drag = drag / 5f;
    }

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

  public void setCanExit(boolean c)
  {
    m_canExit = c;
  }

  public float getDirection()
  {
    return m_currDir;
  }

  public void setCheckpoint(float xx, float yy)
  {
    if (m_dying == false)
    {
      m_checkX = xx;
      m_checkY = yy;
    }
  }

  public void setLocationToCheckpoint()
  {
    this.setPosition(m_checkX, m_checkY);
    m_alive = true;
    m_dying = false;
    m_jumping = false;
    m_climbing = false;
    m_currDir = 1;
    m_dx = 0;
    m_dy = 0;
    m_onGround = true;
    maxFallVelocity = 20.0f;
    m_energy = 3;
    this.setOpacity(1);

    this.runAnimation(m_standAnimation);
    this.setRegion(standRegion);

  }

  public void move()
  {

    m_slipTicks++;

    boolean alignX = false;

    if (m_ignoreHits > 0)
    {
      m_ignoreHits--;
    }

    if (m_bounceTicks > 0)
    {
      m_bounceTicks--;
    } else
    {
      m_bounce = false;
    }
    
    if (m_attacking)
    {
      if (m_attackTicks > 0)
      {
        m_attackTicks--;
      } else
      {
        m_attacking = false;
        m_attackPauseTicks = m_activeWeapon.getPauseTicks();
      }
    } else if (m_attackPauseTicks > 0)
    {
      m_attackPauseTicks--;
    }

    if (m_focus)
    {
      m_gameLayer.playerSprite.setPosition(this.getX()+this.getWidth()/2 - 20, this.getY());
      if (((m_inputManager.isJumpPressed()) || (m_inputManager.isDownPressed())) && m_canExit)
      {
        m_gameLayer.playerSprite.exitBuggy(this.getX()+this.getWidth()/2 - 20, this.getY(), m_currDir);
        m_focus = false;
      }


    }

    // Move Y Now

    if (m_inFloat)
    {
      this.accelerate(0, -(m_gravity/4f));
    } else
    {
      this.accelerate(0, -m_gravity);
    }

    //check for collisions above, below, right, and left.
    float xx = this.getX() + m_dx + this.getWidth()/2;
    float bottomY = this.getY() + m_dy;
    float topY = this.getY() + this.getHeight();

    int tileX = (int) Math.floor(xx/tw);
    int tileY = (int) Math.floor(bottomY/th);

    //this.getCollisions(tileX,tileY, m_platformTiles);
    //this.getPlatformCollisions(xx,this.getY() + m_dy);

    int lc = getCellAt(tileX, tileY);

    if (m_collisionPlatform == null)
    {
      m_onPlatform = false;
      m_activePlatform = null;
    }

      //If moving down
      if (m_dy < 0)
      {
        if (lc >= 0)
        {
          this.setPosition(this.getX(), (tileY + 1) * th);
          m_onGround = true;
          m_doubleJumping = false;
          this.playLandSound();
          m_dy = 0;
        } else if (m_collisionPlatform != null)
        {
          //hit a platform
            this.setPosition(this.getX(), (m_collisionPlatform.getY()+ m_collisionPlatform.getHeight() - 1f));
            m_onGround = true;
            m_onPlatform = true;
            m_activePlatform = m_collisionPlatform;
            m_doubleJumping = false;
            m_onLadder = false;
            m_climbing = false;
            this.playLandSound();
            m_dy = 0;
        }  else 
        {
          m_onGround = false; // THIS IS NEW
        }
      } else 
      {
        //moving up
        if (m_dy > 0)
        {
          tileY = (int) Math.floor(topY/th);
          lc = getCellAt(tileX, tileY);
          if (lc >= 0)
          {
            m_dy = 0;
            this.setPosition(this.getX(), (tileY * th) - this.getHeight());
            m_onGround = false;
            m_climbing = false;
            m_doubleJumping = false;
          } else
          {
            m_onGround = false;
          }
        } 
      }

    if (m_onGround)
    {
      bottomY = this.getY() + m_dy;
      int nny = (int) Math.floor(bottomY/th) - 1;
      int cellUnder = getCellAt(tileX, nny);
      if ((cellUnder > 8) && (cellUnder < 11))
      {
        m_slip = true;
        m_slipTicks = 0;
      } else
      {
        m_slip = false;
      }

      if (cellUnder == 4)
      {
        this.dieNow();
        return;
      }
    }
    // end vertical move

    if ((!m_bounce) && m_focus)
    {
      float cx = 0.7f;
      if (m_inFloat)
        cx = cx / 2f;

      if (m_inputManager.isLeftPressed())
      {
        this.accelerate(-cx,0);
      } else if (m_inputManager.isRightPressed())
      {
          this.accelerate(cx,0);
      } else 
      {
        //no left, right...so add horizontal drag
        if (m_onGround == false)
        {
          if (m_slipTicks > 60)
          {
            m_dx = this.applyDrag(m_dx, m_horizontalDragFactor);
          } else
          {
            m_dx = this.applyDrag(m_dx, m_horizontalDragFactor/4f);
          }
        } else
        {
          m_dx = this.applyDrag(m_dx, m_horizontalDragFactor);
        }
      }
    }

    if (m_focus == false)
    {
      m_dx = this.applyDrag(m_dx, m_horizontalDragFactor);
    }
    
    if ((m_attacking) && (m_onGround) && (m_activeWeapon.stopMovingWhenAttacking()) && (!m_slip))
    {
      m_dx = 0;
    }


    //check collisions X
    //check for collisions above, below, right, and left.
    float leftX = this.getX() + m_dx;
    float middleX = this.getX() + m_dx + this.getWidth()/2;
    float rightX = this.getX() + m_dx + this.getWidth();

    float yy = this.getY();

    tileX = (int) Math.floor(leftX/tw);
    tileY = (int) Math.floor(yy/th);

    //collisions on left side?
    if (m_dx < 0) // moving left
    {
      lc = getCellAt(tileX, tileY);
      int tlc = getCellAt(tileX, tileY+1);
      int ttlc = getCellAt(tileX, tileY+2);
      if ((lc >= 0) || (tlc > 0) || (ttlc > 0))
      {
        float correctX = (tileX + 1) * tw;
        float nx = correctX - this.getX();
        if (nx > 0)
        {
          m_dx = 0;
          this.setPosition(this.getX() + 1, this.getY());
        } else
        {
          m_dx = nx;
        }
      }

      if ((lc == 5) || (tlc == 5) || (ttlc == 5))
      {
        this.dieNow();
        return;
      }

    } else if (m_dx > 0) //moving right
    {
      tileX = (int) Math.floor(rightX/tw);
      lc = getCellAt(tileX, tileY);
      int tlc = getCellAt(tileX, tileY+1);
      int ttlc = getCellAt(tileX, tileY+2);
      if ((lc >= 0) || (tlc > 0) || (ttlc > 0))
      {
        float correctX = (tileX * tw) - this.getWidth();
        float nx = correctX - this.getX();
        if (nx < 0)
        {
          m_dx = 0;
          this.setPosition(this.getX() - 1, this.getY());
        } else
        {
          m_dx = nx;
        }
      }

      if ((lc == 6) || (tlc == 6) || (ttlc == 6))
      {
        this.dieNow();
        return;
      }

    } else if (m_dx == 0)
    {
      lc = getCellAt(tileX, tileY);
      tileX = (int) Math.floor(rightX/tw);
      int rc = getCellAt(tileX, tileY);
      if (lc > 0)
      {
        this.setPosition(this.getX() + 1, this.getY());
      } else if (rc > 0)
      {
        this.setPosition(this.getX() - 1, this.getY());
      }
    }

    if (m_onGround)
    {
      if (!m_attacking)
      {
        if (m_dx != 0)
        {
          if (!m_walkAnimation.isRunning())
          {
            this.stopAllAnimations();
            this.stopSound("buggyDrive");
            this.runAnimation(m_walkAnimation);
            this.loopSound("buggyDrive", 0.3f);
          }
        } else
        {
          if (m_idleAnimation.isRunning())
          {

          } else if (!m_standAnimation.isRunning())
          {
            this.stopAllAnimations();
            this.runAnimation(m_standAnimation);
            this.stopSound("buggyDrive");
          } else if (m_standAnimation.isRunning())
          {
            m_idleTicks++;
            if (m_idleTicks > 180)
            {
              this.stopAllAnimations();
              this.runAnimation(m_idleAnimation);
              m_idleTicks = 0;
            }
          }
        }
      }
    } else if ((m_inFloat) && (!m_attacking))
    {
      if (!m_floatAnimation.isRunning())
      {
        this.stopAllAnimations();
        this.runAnimation(m_floatAnimation);
        this.runAnimation(m_floatAnimation2);
        this.stopSound("buggyDrive");
      }
    }

    if (m_dx > 0) 
    {
      this.setScale(1,1);
      m_currDir = 1;
    } else if (m_dx < 0)
    {
      this.setScale(-1,1);
      m_currDir = -1;
    }

    if (m_lightDir != m_currDir)
    {
      m_lightDir = m_currDir;
      if (m_currDir > 0)
      {
        pLight.setDirection(0);
        m_lightX = this.getWidth() - 20;
      }
      else
      {
        m_lightX = 20;
        pLight.setDirection(180);
      }
    }

    pLight.setPosition(this.getX() + m_lightX, this.getY() + 45);

    m_inSwamp = false;
    m_inFloat = false;

  }

  public void showTutorial()
  {
    this.stopAllAnimations();
    this.runAnimation(m_tutorialAnimation);
  }

  public void startHover(float hx, float hy)
  {
    m_hoverSprite.setVisible(true);
    m_hoverSprite.setPosition(hx,hy);
    
    m_dy = 0;
    m_dx = 0;

    m_hover = true;
    this.stopAllAnimations();
    m_startHoverAnimation = new AnimateMoveTo(0.2f, this.getX(), this.getY(), hx, hy+18);
    this.chainAnimations(m_startHoverAnimation, m_hoverAnimation);

    if ((m_hoverSpeed + 6) > maxSpeedX)
      maxSpeedX = m_hoverSpeed + 6;

  }

  public void headlightsOn()
  {
    pLight.setActive(true);
    pLight.setPosition(this.getX() + m_lightX, this.getY() + 45);
  }

  public void stopHover(boolean forReals)
  {
    if (m_hover)
    {
      m_hoverSprite.setVisible(false);
      this.stopAllAnimations();
      m_hover = false;
      m_dx = 0;
      m_dy = 0;
      m_climbing = false;
      m_inSwamp = false;
      maxSpeedX = defaultMaxSpeedX;
      if (forReals)
      {
        m_gameLayer.setPlayerXOffsetTarget(0);
      }
    }

    if (m_wrappedUp)
    {
      m_wrappedUp = false;
      this.stopAllAnimations();
    }
  }

  public void setFocus(boolean f)
  {
    m_focus = f;
  }

  public void wrapUp()
  {
    //Gdx.app.debug("PlayerSprite", "wrapUp");
    m_wrapHP = 5;
    m_wrappedUp = true;
    m_attacking = false;
    m_jumping = false;
    this.stopSound("buggyDrive");

    if ((m_wrappedUpAnimation.isRunning()) || (m_wrappedFreeAnimation.isRunning()))
    {
      this.stopAllAnimations();
    }

    this.runAnimation(m_firstWrappedAnimation);
  }

  public void fightWrap()
  {
    if (m_wrappedUp)
    {
      if ((m_wrappedUpAnimation.isRunning()) || (m_wrappedFreeAnimation.isRunning()))
        return;

      if (m_wrapHP > 0)
      {
        //Gdx.app.debug("PlayerSprite", "fightWrap fighting hp = " + m_wrapHP);
        this.runAnimation(m_wrappedUpAnimation);
        m_wrapHP--;
        this.playSound("fightWeb",0.4f);
      } else if (m_wrapHP == 0)
      {
        //Gdx.app.debug("PlayerSprite", "fightWrap defeated");
        this.runAnimation(m_wrappedFreeAnimation);
        this.playSound("breakWeb",0.65f);
        m_wrapHP--;
      }
    }
  }

  public void getPlatformCollisions(float xx, float yy)
  {
    m_collisionPlatform = null;
    m_boundingBox.x = this.getBoundingRectangle().getX() + m_boundOffX;
    m_boundingBox.y = this.getBoundingRectangle().getY();

    for (m_iter = 0; m_iter < m_platforms.size(); m_iter++)
    {
      PlatformSprite p = m_platforms.get(m_iter);
      if (p.isCollidable())
      {
        if (Intersector.overlaps(p.getBoundingRectangle(), m_boundingBox))
        {
          //hit a platform
          m_collisionPlatform = p;
          break;
        }
      }
    }
  }

  public boolean isCollidable()
  {
    return (m_alive && !m_dying && !m_spawning && this.isVisible() && !m_inTeleport);
  }

  public void setHoverRange(float min, float max)
  {
    m_hoverMin = min;
    m_hoverMax = max;
  }

  public void hitByAttack()
  {
    this.dieNow();
  }

  public boolean hitByAttack(GameSprite enemy)
  {

    if (enemy instanceof Seeker)
    {
      Seeker ss = (Seeker)enemy;
      ss.scream();
    }

    this.dieNow();

    /*
    if (m_dying == false)
    {
      Gdx.app.debug("PlayerSprite", "dying!!!!");
      this.playSound("vaporize", 0.8f);
      m_inHit = false;
      m_ignoreHits = 0;
      m_explodeDeath = true;
      m_energy = 0;
      this.stopAllAnimations();
      m_invincibleAnimation.stop();
      this.stopSound("buggyDrive");
      PooledEffect effect = m_gameLayer.bombEffectPool.obtain();
      effect.setPosition(this.getX()+this.getWidth()/2, this.getY());
      m_gameLayer.addParticleEffect(effect);
      this.runAnimation(m_throughDoorAnimation);
      m_dying = true;
      m_deadState = 1;
      return true;
    } */

    return true;
  }

  public void pause()
  {
    this.stopAllAnimations();
    this.stopSound("buggyDrive");
    m_pause = true;
  }

  public boolean isOnGround()
  {
    return m_onGround;
  }

 public void dieNow()
  {
    Gdx.app.debug("BuggySprite", "dieNow called");
    if ((m_dying == false) && (m_alive))
    {
      Gdx.app.debug("BuggyrSprite", "dying!!!!");
      this.playSound("vaporize", 0.8f);
      m_inHit = false;
      m_ignoreHits = 0;
      m_explodeDeath = true;
      m_energy = 0;
      this.stopAllAnimations();
      m_invincibleAnimation.stop();
      this.stopSound("buggyDrive");
      PooledEffect effect = m_gameLayer.bombEffectPool.obtain();
      effect.setPosition(this.getX()+this.getWidth()/2, this.getY());
      m_gameLayer.addParticleEffect(effect);
      this.runAnimation(m_throughDoorAnimation);
      m_dying = true;
      m_deadState = 1;
      if (m_focus == false)
      {
        m_gameLayer.buggyDieWithoutFocus();
      }
    }
  }

  public void setActiveWeapon(WeaponInterface weapon)
  {
    m_activeWeapon = weapon;
  }

  public WeaponInterface getActiveWeapon()
  {
    return m_activeWeapon;
  }

  public void goThroughDoor()
  {
    //this.pause();
    this.stopAllAnimations();
    this.stopSound("buggyDrive");
    m_pauseMoveTicks = 65;
    this.runAnimation(m_throughDoorAnimation);
    this.playSound("openDoor", 0.9f);
    m_dx = 0;
    m_dy = 0;
  }

  public void playLandSound()
  {
    if (m_dy < -15)
    {
      this.playSound("land", 0.4f);
    } else if (m_dy < -14)
    {
      this.playSound("land", 0.35f);
    } else if (m_dy < -13)
    {
      this.playSound("land", 0.3f);
    } else if (m_dy < -12)
    {
      this.playSound("land", 0.25f);
    }
  }

  public void deathByFalling()
  {
      if (!m_dying)
      {
        this.stopAllAnimations();
        this.stopSound("buggyDrive");
        this.playSound("falling",0.9f);
        m_deadTicks = 30;
        m_deadState = 2;
        m_dying = true;
        m_dx = 0;
      }
  }

  public void setHoverSpeed(float hoverSpeed)
  {
    m_hoverSpeed = hoverSpeed;

  }

  public void bounce(float xf, float yf)
  {
    m_dx = (xf * 30);
    m_dy = (yf * 30);
    m_bounce = true;
    m_bounceTicks = 20;
    //m_bounceDragTicks = 25;
    m_jumping = true;
    m_doubleJumpPossible = false;
    m_jumpTicks = 0;
    m_doubleJumping = false;
    //this.playSound("jump", 0.4f);
    if (!m_attacking)
    {
      this.stopAllAnimations();
      this.stopSound("buggyDrive");
      this.runAnimation(m_jumpAnimation);
    }
  }

  public boolean isInHit()
  {
    if (m_ignoreHits > 0)
      return true;

    return false;
  }

  public boolean isHover()
  {
    return m_hover;
  }

  public void startTeleport(float xx, float yy)
  {
    if ((m_inTeleport == false) && (m_dying == false))
    {
      m_teleportAnimation = new AnimateMoveTo(1.5f, this.getX(), this.getY(), xx,yy);
      m_inTeleport = true;
      m_teleportState = 0;
      this.stopSound("buggyDrive");
      this.stopAllAnimations();
      m_invincibleAnimation.stop();
      m_dx = 0;
      m_dy = 0;
      this.runAnimation(m_teleportOut);
      this.playSound("teleportIn", 0.75f);
    }
  }
}