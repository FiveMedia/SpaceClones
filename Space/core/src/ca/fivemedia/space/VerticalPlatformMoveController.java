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

public class VerticalPlatformMoveController extends BaseMoveController {

  int m_ignoreTicks = 0;
  int m_groundDir = 1; //Ground is 'to the right'
  boolean m_falling = false;
  float oldMax = 2;

  public VerticalPlatformMoveController(float speedX, float speedY, int groundDir) {
    super(speedX, speedY);
    m_supportReverse = true;
    m_groundDir = groundDir;
    m_accelY = 1.0f;
  }


  public void reset()
  {

  }

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

  public void move(BaseSprite sprite, PlayerSprite player, TiledMapTileLayer platformTiles, TiledMapTileLayer climbableTiles)
  {

    this.accelerate(0,m_accelY*m_currDir);

    if (m_ignoreTicks > 0)
    {
      m_ignoreTicks--;
    } else
    {
     
      int spriteCol = 0;
      spriteCol = this.getSpriteCollisions(sprite.getParent(), sprite);

      if ((spriteCol == 1) && (m_supportReverse))
      {
        m_ignoreTicks = 30;
        m_currDir = -m_currDir;
      } else if (spriteCol == 2)
      {
        sprite.die();
        m_dx = 0;
        m_dy = 0;
        sprite.setVelocity(0, 0);
        m_ignoreTicks = 60;
        return;
      }

      float xx = sprite.getX() + m_dx + sprite.getWidth()/2;
      int tileX = (int)Math.floor(xx/tw);
      float bottomY = sprite.getY() + m_dy;
      int bottomTileY = (int) Math.floor(bottomY/th);
      float topY = sprite.getY() + m_dy + sprite.getHeight();
      int topTileY = (int) Math.floor(topY/th);


      int tileGroundX = tileX + m_groundDir;

      if (!m_falling)
      {

        if (m_currDir > 0)
        {
          //moving up
          int lc = getCellAt(tileGroundX, topTileY, platformTiles); //ground tile
          int lc2 = getCellAt(tileX, topTileY, platformTiles); //tile in front of you

          if((lc < 0) || (lc == 7) || (lc == 4) || (lc == 5) || (lc == 6) || (lc2 > 0))
          {
            //nothing above to climb on, change dir
            m_currDir = -1;
            m_dy = 0;
          }
        } else if (m_currDir < 0)
        {
          //moving down
          int lc = getCellAt(tileGroundX, bottomTileY , platformTiles);
          int lc2 = getCellAt(tileX, bottomTileY, platformTiles);

          if((lc < 0) || (lc == 7) || (lc == 4) || (lc == 5) || (lc == 6) || (lc2 > 0))
          {
            //nothing to the left
            m_currDir = 1;
            m_dy = 0;
          }
        }

        int tileY = (int)Math.floor((bottomY - m_dy + sprite.getHeight()/2)/th);
        int lc = getCellAt(tileGroundX, tileY, platformTiles);
        if (lc < 0)
        {
          //fall
          m_falling = true;
          oldMax = maxSpeedY;
          maxSpeedY = 12;
          m_currDir = -1;
        }
      } else
      {
        int tileY = (int)Math.floor((bottomY - m_dy + sprite.getHeight()/2)/th);
        int lc = getCellAt(tileGroundX, tileY, platformTiles);
        if ((lc > 3) && (lc < 7))
          sprite.die();
        else if (lc > -1)
        {
          m_falling = false;
          maxSpeedY = oldMax;
        }

        lc = getCellAt(tileGroundX - m_groundDir, bottomTileY, platformTiles);
        if (lc > -1)
          sprite.die();
      }

      //if you blow up square it's sliming down (vertically)
      //it falls, then wehn lands turns into a horizontal slime

      /*
      int tileX = (int)Math.floor((sprite.getX() + sprite.getWidth()/2)/tw);
      
      if (m_dy == 0)
      {
        int lc = getCellAt(tileX, tileY-1, platformTiles);
        if (lc < 0)
          this.accelerate(0, -1);
      } else
      {
        this.accelerate(0, -1);
        int lc = getCellAt(tileX, tileY, platformTiles);
        if (lc >= 0)
        {
          m_dy = 0;
          sprite.setPosition(sprite.getX(), (tileY + 1) * th);
          if (lc == 4)
            sprite.die();
        }
      }*/

    }

    sprite.setVelocity(m_dx, m_dy);

  }



}