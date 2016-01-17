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

public class VerticalMoveController extends BaseMoveController {

  int m_ignoreCollisionTicks = 0;
  boolean m_active = false;
  boolean m_dieIfHitPlatform = false;

  public VerticalMoveController(float speedY, boolean supportReverse, boolean dieHitPlatform)
  {
    this(speedY, supportReverse);
    m_dieIfHitPlatform = dieHitPlatform;
  }

  public VerticalMoveController(float speedY, boolean supportReverse) {
    super(0, speedY);
    m_accelY = 2.0f;
    m_accelX = 0;
    m_supportReverse = supportReverse;
  }

  public void move(BaseSprite sprite, PlayerSprite player, TiledMapTileLayer platformTiles, TiledMapTileLayer climbableTiles)
  {
    if (this.shouldMove(player,sprite))
    {
      this.accelerate(0, m_accelY*m_currDir);
      int spriteCol = 0;

      if (m_ignoreCollisionTicks > 0)
      {
        m_ignoreCollisionTicks--;
      } else
      {
       spriteCol = this.getSpriteCollisions(sprite.getParent(), sprite);
      }

      if (spriteCol == 1)
      {
        m_ignoreCollisionTicks = 32;

        /*
        if (maxSpeedY > 4)
        {
          m_ignoreCollisionTicks = ;
        }
        else if (maxSpeedY > 3)
        {
          m_ignoreCollisionTicks = 28;
        }
        else if (maxSpeedY > 2)
        {
          m_ignoreCollisionTicks = 50;
        }
        else if (maxSpeedY > 1)
        {
          m_ignoreCollisionTicks = 65;
        }

        if (maxSpeedY < 1)
          m_ignoreCollisionTicks = 180;
          */


        m_currDir = -m_currDir;
      } else if ((spriteCol == 5) && (m_supportReverse))
      {
        m_ignoreCollisionTicks = 28;
        //Gdx.app.debug("SingleDirMove", "reverse dir");
        m_currDir = -m_currDir;
        m_dy = 0;
        m_dx = 0;
        m_motionActive = false;
        m_done = true;
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