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

public class FallMoveController extends BaseMoveController {

  int m_ignoreCollisionTicks = 0;
  boolean m_active = false;
  boolean m_dieIfHitPlatform = false;
  float maxYUp = 4.0f;
  int m_pt = 60;
  int m_pb = 60;
  int m_pauseTicks = 60;
  boolean m_shakeOnGround = false;

  public FallMoveController(float speedY, boolean supportReverse, boolean dieHitPlatform, float pt, float pb, boolean shakeOnGround)
  {
    this(speedY, supportReverse);
    m_dieIfHitPlatform = dieHitPlatform;
    m_pt = (int) (pt * 60);
    m_pb = (int)(pb * 60);
    m_pauseTicks = m_pt;
    m_shakeOnGround = shakeOnGround;
  }

  public FallMoveController(float speedY, boolean supportReverse) {
    super(0, 18);
    m_accelY = 0.8f;
    m_accelX = 0;
    m_supportReverse = supportReverse;
    maxYUp = speedY;
    maxSpeedY = 18;
  }

  public void move(BaseSprite sprite, PlayerSprite player, TiledMapTileLayer platformTiles, TiledMapTileLayer climbableTiles)
  {
    if (this.shouldMove(player,sprite))
    {
        if (m_pauseTicks > 0)
        {
          m_pauseTicks--;
          m_dy = 0;
        }

      int cid = -1;
      int tid = -1;

      if (m_pauseTicks < 1)
      { 
        this.accelerate(0, m_accelY*m_currDir);
        int spriteCol = 0;

        int tileY = 0;
        int topTileY = 0;

        if (m_ignoreCollisionTicks > 0)
        {
          m_ignoreCollisionTicks--;
        } else
        {
          spriteCol = this.getSpriteCollisions(sprite.getParent(), sprite);
          float xx = sprite.getX() + m_dx + sprite.getWidth()/2;
          float bottomY = sprite.getY() + m_dy;

          int tileX = (int) Math.floor(xx/tw);
          tileY = (int) Math.floor(bottomY/th);
          topTileY = (int) Math.floor((sprite.getY()+m_dy+sprite.getHeight())/th);
          
          cid = getCellAt(tileX, tileY, platformTiles);
          tid = getCellAt(tileX, topTileY, platformTiles);
        }

        if ((cid > 0) && (m_dy < 0)) //hit on bottom
        {
          sprite.setPosition(sprite.getX(), (tileY + 1) * th);

          m_dy = 0;
          if (m_supportReverse)
          {
            m_ignoreCollisionTicks = 10;
            m_currDir = -m_currDir;
            m_dy = 0;
            m_dx = 0;
            //m_motionActive = false;
            //m_done = true;
            if (m_currDir < 0)
            {
              maxSpeedY = 18;
              m_pauseTicks = m_pt;
            } else
            {
              maxSpeedY = maxYUp;
              m_pauseTicks = m_pb;
              if (m_pb < 0)
              {
                //become permanent blocks!!
                m_done = true;
                m_pauseTicks = 100000;
              }

              if (m_shakeOnGround)
              {
                player.shakeCameraIfClose(sprite);
              }
            }
          } else
          {
            m_done = true;
          }
        } else if (tid > 0 && (m_dy > 0)) // hit on top
        {
          sprite.setPosition(sprite.getX(), (topTileY * th) - sprite.getHeight());

          m_dy = 0;
          if (m_supportReverse)
          {
            m_ignoreCollisionTicks = 10;
            m_currDir = -m_currDir;
            m_dy = 0;
            m_dx = 0;
            //m_motionActive = false;
            //m_done = true;
            if (m_currDir < 0)
            {
              maxSpeedY = 18;
              m_pauseTicks = m_pt;
            } else
            {
              maxSpeedY = maxYUp;
              m_pauseTicks = m_pb;
            }
          } else
          {
            m_done = true;
          }
        }else
        {
          if (spriteCol == 1)
          {
            m_ignoreCollisionTicks = 10;
            m_currDir = -m_currDir;
            float oldDy = m_dy;
            m_dy = 0;
            m_dx = 0;
            //m_motionActive = false;
            //m_done = true;
            if (m_currDir < 0)
            {
              maxSpeedY = 18;
              m_pauseTicks = m_pt;
              m_dy = -oldDy;
            } else
            {
              maxSpeedY = maxYUp;
              m_pauseTicks = m_pb;
              m_dy = -oldDy;
            }
          } else if ((spriteCol == 5) && (m_supportReverse))
          {
            m_ignoreCollisionTicks = 10;
            m_currDir = -m_currDir;
            float oldDy = m_dy;
            m_dy = 0;
            m_dx = 0;
            //m_motionActive = false;
            //m_done = true;
            if (m_currDir < 0)
            {
              maxSpeedY = 18;
              m_pauseTicks = m_pt;
              m_dy = -oldDy;
            } else
            {
              maxSpeedY = maxYUp;
              m_pauseTicks = m_pb;
              m_dy = -oldDy;
            }

          } else if ((spriteCol == 2) && (!m_ignoreDie))
          {
            sprite.die();
            m_dx = 0;
            m_dy = 0;
          } else if ((spriteCol == 4) && (m_dieIfHitPlatform))
          {
            sprite.die();
            m_dx = 0;
            m_dy = 0;
          }
        }
      }
      sprite.setVelocity(m_dx, m_dy);
    }
  }

  public void reset()
  {
    super.reset();
    m_ignoreCollisionTicks = 4;
    m_done = false;
  }

}