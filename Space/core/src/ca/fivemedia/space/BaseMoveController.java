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

public abstract class BaseMoveController implements MoveController {

  int[] col = new int[9];
  float tw, th;
  float m_currDir = 1;
  protected float m_dx, m_dy;
  protected float maxSpeedX = 2.5f;
  protected float maxSpeedY = 0.0f;
  protected float m_accelX = 1.0f;
  protected float m_accelY = 0.0f;
  protected float m_spx = 1.0f;
  protected float m_spy = 1.0f;
  float m_xx, m_yy;
  int m_tileX, m_tileY;
  boolean m_motionActive = true;
  int m_triggerType = 0; //0 = start right away, 1 = when player touches, 2 = first time in display, 3 = player on.
  float m_startDir = 0;
  boolean m_fallDeadState = false;
  boolean m_done = false;
  boolean m_supportReverse = false;
  GameSprite m_triggerSprite = null;
  float m_range = 0;
  boolean m_ignoreDie = false;
  int m_iter  = 0;

  public BaseMoveController(float speedX, float speedY) {
    super();
    tw = 48.0f;
    th = 32.0f;
    maxSpeedX = speedX;
    maxSpeedY = speedY;
  }

  public void triggerMotion()
  {
    //Gdx.app.debug("BaseMoveController", "triggerMotion() called on move controller.");
    m_motionActive = true;
    m_done = false;
  }

  public void setTriggerType(int t)
  {
    m_triggerType = t;
    if (t > 0)
      m_motionActive = false;
  }

  public void reset()
  {
    if (m_startDir != 0)
      m_currDir = m_startDir;

    m_motionActive = false;
    m_dx = 0;
    m_dy = 0;
    m_fallDeadState = false;
  }

  public void setIgnoreDie()
  {
    m_ignoreDie = true;
  }

  public void setIgnoreReverse()
  {
    m_supportReverse = false;
  }


  public void setSpeedXFactor(float sp)
  {
    m_spx = sp;
  }

  public void setSpeedYFactor(float sp)
  {
    m_spy = sp;
  }

  public float getDirection()
  {
    return m_currDir;
  }

  public int getCellAt(int tileX, int tileY, TiledMapTileLayer tiles)
  {
    TiledMapTileLayer.Cell c = tiles.getCell(tileX,tileY);
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

  public int getSpriteCollisions(GameContainer parent, GameSprite sprite)
  {
    for (m_iter = 0; m_iter < parent.getChildren().size(); m_iter++)
    {
      GameDrawable s = parent.getChildren().get(m_iter);
      if (s instanceof SpecialBehaviourSprite)
      {
        SpecialBehaviourSprite sb = (SpecialBehaviourSprite) s;
        if ((sb.getType() == 0) && m_supportReverse)
        {
          if (Intersector.overlaps(sb.getBoundingRectangle(), sprite.getBoundingRectangle()))
          {
            //Gdx.app.debug("SingleDirectionMoveController", "should reverse!!!!!!!");
            return 1;
          }
        } else if ((sb.getType() == 3) && m_supportReverse)
        {
          //platform reverse and stop
          if (Intersector.overlaps(sb.getBoundingRectangle(), sprite.getBoundingRectangle()))
          {
            //Gdx.app.debug("SingleDirectionMoveController", "should reverse!!!!!!!");
            return 5;
          }
        } else if ((sb.getType() == 1) && (!m_ignoreDie))
        {
          if (Intersector.overlaps(sb.getBoundingRectangle(), sprite.getBoundingRectangle()))
          {
            //Gdx.app.debug("BaseMoveController", "should die!!!!!!!");
            return 2;
          }
        } else if (sb.getType() == 2)
        {
          if (Intersector.overlaps(sb.getBoundingRectangle(), sprite.getBoundingRectangle()))
          {
            //Gdx.app.debug("BaseMoveController", "spider stop");
            return 4;
          }
        }
      } else if (s instanceof PlatformSprite)
      {
        PlatformSprite ps = (PlatformSprite)s;
        if (ps != sprite)
        {
          Rectangle pr = ps.getBoundingRectangle();
          pr.width = pr.width - 30;
          pr.x += 15;
          pr.y += 5;
          pr.height -= 5;

          if (Intersector.overlaps(ps.getBoundingRectangle(), sprite.getBoundingRectangle()))
          {
            float deltaY = sprite.getY() - pr.getY();
            if (deltaY > -5)
              return 4;
          }
        }
      }
    }
    return 0;
  }

  public int getTileBelow(int tileX, int tileY, TiledMapTileLayer tiles, int max)
  {
    for (int cy = 0; cy < max; cy++)
    {
      int ty = tileY - cy;
      TiledMapTileLayer.Cell c = tiles.getCell(tileX,ty);
      if (c != null)
      {
        return cy;
      } 
    }

    return -1;
  }

  public boolean shouldMove(PlayerSprite player, BaseSprite sprite)
  {
    if (m_motionActive)
      return true;

    if (m_triggerType == 0)
    {
      m_motionActive = true;
      m_done = false;
    } else if (m_triggerType == 1)
    {
      Rectangle sr = sprite.getBoundingRectangle();
      sr.height += 4;
      sr.width -= 16;
      sr.x += 8;
      sr.y -= 2;
      if (Intersector.overlaps(sr, player.getBoundingRectangle()))
      {
        m_motionActive = true;
        m_done = false;
        if (sprite.getGroupId() > 0)
        {
          //Gdx.app.debug("BasMoveController", "groupId = " + sprite.getGroupId());
          GameContainer pp = sprite.getParent();
          for (GameDrawable dd : pp.getChildren())
          {
            if (dd instanceof BaseSprite)
            {
              BaseSprite sd = (BaseSprite) dd;
              sd.triggerMotion(sprite.getGroupId());
            }
          }
        }
      }
    } else if (m_triggerType == 3)
    {
      Rectangle sr = sprite.getBoundingRectangle();
      sr.height += 4;
      sr.width -= 8;
      sr.x += 4;
      sr.y -= 2;
      Rectangle pr = player.getBoundingRectangle();
      pr.height = 8;
      pr.width -= 20;
      pr.x += 10;
      if (Intersector.overlaps(sr, pr))
      {
        m_motionActive = true;
        m_done = false;
        if (sprite.getGroupId() > 0)
        {
          //Gdx.app.debug("BasMoveController", "groupId = " + sprite.getGroupId());
          GameContainer pp = sprite.getParent();
          for (GameDrawable dd : pp.getChildren())
          {
            if (dd instanceof BaseSprite)
            {
              BaseSprite sd = (BaseSprite) dd;
              sd.triggerMotion(sprite.getGroupId());
            }
          }
        }
      }
    } else if ((m_triggerType == 4) || (m_triggerType == 40))
    {
      //underneath it (40 is just buggy)
      if ((player.isVisible() == true) && (m_triggerType == 40))
      {
        return m_motionActive;
      }

      float ddx = 20;
      if (m_triggerType == 40)
      {
        ddx = 90;
      }

      float px = player.getX() + player.getWidth()/2;
      float sx = sprite.getX() - ddx;
      float sxm = sprite.getX() + sprite.getWidth() + ddx;
      float deltaY  = sprite.getY() - player.getY();

      if ((deltaY > -40) && (deltaY < 700))
      {
        if ((px > sx) && (px < sxm))
        {
          m_motionActive = true;
          m_done = false;
          if (sprite.getGroupId() > 0)
          {
            //Gdx.app.debug("BasMoveController", "groupId = " + sprite.getGroupId());
            GameContainer pp = sprite.getParent();
            for (GameDrawable dd : pp.getChildren())
            {
              if (dd instanceof BaseSprite)
              {
                BaseSprite sd = (BaseSprite) dd;
                sd.triggerMotion(sprite.getGroupId());
              }
            }
          }
        }
      }
    } else if (m_triggerType == 5)
    {
      //sprite within 1300 pixels of player
      float deltaX = sprite.getX() - player.getX();
      if (deltaX < 1300)
      {
        m_motionActive = true;
        m_done = false;
        if (sprite.getGroupId() > 0)
        {
          //Gdx.app.debug("BasMoveController", "groupId = " + sprite.getGroupId());
          GameContainer pp = sprite.getParent();
          for (GameDrawable dd : pp.getChildren())
          {
            if (dd instanceof BaseSprite)
            {
              BaseSprite sd = (BaseSprite) dd;
              sd.triggerMotion(sprite.getGroupId());
            }
          }
        }
      }
    }  else if (m_triggerType == 6)
    {
      //sprite within 450 pixels of player
      float deltaX = Math.abs(sprite.getX() - player.getX());
      float deltaY = Math.abs(sprite.getY() - player.getY());
      if ((deltaX < 450) && (deltaY < 150))
      {
        m_motionActive = true;
        m_done = false;
        if (sprite.getGroupId() > 0)
        {
          //Gdx.app.debug("BasMoveController", "groupId = " + sprite.getGroupId());
          GameContainer pp = sprite.getParent();
          for (GameDrawable dd : pp.getChildren())
          {
            if (dd instanceof BaseSprite)
            {
              BaseSprite sd = (BaseSprite) dd;
              sd.triggerMotion(sprite.getGroupId());
            }
          }
        }
      }
    }else if (m_triggerType == 9)
    {
      //sprite in range (left of this by range)
      float deltaX = sprite.getX() - m_triggerSprite.getX();
      if (deltaX < m_range)
      {
        m_motionActive = true;
        m_done = false;
        if (sprite.getGroupId() > 0)
        {
          //Gdx.app.debug("BasMoveController", "groupId = " + sprite.getGroupId());
          GameContainer pp = sprite.getParent();
          for (GameDrawable dd : pp.getChildren())
          {
            if (dd instanceof BaseSprite)
            {
              BaseSprite sd = (BaseSprite) dd;
              sd.triggerMotion(sprite.getGroupId());
            }
          }
        }
      }
    }else if ((m_triggerType == 10) || (m_triggerType == 100))
    {
      if ((player.isVisible() == true) && (m_triggerType == 100))
      {
        return m_motionActive;
      }
      //on same level
      float yy = sprite.getY();
      if (sprite.getScaleY() > 1)
      {
        yy -= sprite.getHeight()/2;
      }

      float deltaY = yy - player.getY();
      if ((deltaY > -50) && (deltaY < 50))
      {
        m_motionActive = true;
        m_done = false;
        if (sprite.getGroupId() > 0)
        {
          GameContainer pp = sprite.getParent();
          for (GameDrawable dd : pp.getChildren())
          {
            if (dd instanceof BaseSprite)
            {
              BaseSprite sd = (BaseSprite) dd;
              sd.triggerMotion(sprite.getGroupId());
            }
          }
        }
      }
    }else if (m_triggerType == 11)
    {
      //sprite in range (left of this by range) and under it
      float deltaX = Math.abs(sprite.getX() - player.getX());
      float deltaY = sprite.getY() - player.getY();
      if ((deltaY > -80) && (deltaX < 720))
      {
        m_motionActive = true;
        m_done = false;
        if (sprite.getGroupId() > 0)
        {
          //Gdx.app.debug("BasMoveController", "groupId = " + sprite.getGroupId());
          GameContainer pp = sprite.getParent();
          for (GameDrawable dd : pp.getChildren())
          {
            if (dd instanceof BaseSprite)
            {
              BaseSprite sd = (BaseSprite) dd;
              sd.triggerMotion(sprite.getGroupId());
            }
          }
        }
      }
    }else if ((m_triggerType == 12) || (m_triggerType == 120)) //same level and to the right of enemy
    {
      if ((player.isVisible() == true) && (m_triggerType == 120))
      {
        return m_motionActive;
      }

      //on same level
      float yy = sprite.getY();
      if (sprite.getScaleY() > 1)
      {
        yy -= sprite.getHeight()/2;
      }

      float deltaY = yy - player.getY();
      if ((deltaY > -50) && (deltaY < 50))
      {
        float deltaX = sprite.getX() - player.getX();
        if (deltaX < 0)//player is to the right of it.
        {
          m_motionActive = true;
          m_done = false;
          if (sprite.getGroupId() > 0)
          {
            GameContainer pp = sprite.getParent();
            for (GameDrawable dd : pp.getChildren())
            {
              if (dd instanceof BaseSprite)
              {
                BaseSprite sd = (BaseSprite) dd;
                sd.triggerMotion(sprite.getGroupId());
              }
            }
          }
        }

      }
    }else if (m_triggerType == 50)
    {
      //special trigger/do nothing
    }

    return m_motionActive;
  }

  public void setSpriteTrigger(GameSprite sprite, float range)
  {
    m_triggerSprite = sprite;
    m_range = range;
  }

  public void calculateNewLocation(float dx, float dy, BaseSprite sprite)
  {
        //check for collisions above, below, right, and left.
    m_xx = sprite.getX() + sprite.getWidth()/2 - (tw*dx) + m_dx;
    m_yy = sprite.getY() - (th*dy) + m_dy;

    m_tileX = (int) Math.floor(m_xx/tw);
    m_tileY = (int) Math.floor(m_yy/th);
  }

  public abstract void move(BaseSprite sprite, PlayerSprite player, TiledMapTileLayer platformTiles, TiledMapTileLayer climbableTiles);
  
  public void accelerate (float ax, float ay)
  {

    m_dx += ax;
    m_dy += ay;

    if (Math.abs(m_dx) > (maxSpeedX*m_spx))
    {
      m_dx = maxSpeedX * m_spx * (m_dx/Math.abs(m_dx));
    }

    if (Math.abs(m_dy) > maxSpeedY)
    {
      m_dy = maxSpeedY * (m_dy/Math.abs(m_dy));
    }

  }

  public void setStartDirection(float f)
  {
    m_currDir = f;
    m_startDir = f;
  }

  public void setDirection(float f)
  {
    m_currDir = f;
    //Gdx.app.debug("BaseDirMove", "setDir = " + m_currDir);
  }

  public void fallDead(BaseSprite sprite, TiledMapTileLayer platformTiles)
  {
    m_dx = 0;
    maxSpeedY = 14;
    this.accelerate(0, -1.0f);
    this.calculateNewLocation(1,1,sprite);
    this.getCollisions(m_tileX,m_tileY, platformTiles);
    if(col[7] > 0)
    {
      m_dy = 0;
    }
    sprite.setVelocity(m_dx, m_dy);
  }

  public void setVelocity(float dx, float dy)
  {
    m_dx = dx;
    m_dy = dy;
  }

  public boolean isDone()
  {
    return m_done;
  }

  public boolean isMotionActive()
  {
    return m_motionActive;
  }

}