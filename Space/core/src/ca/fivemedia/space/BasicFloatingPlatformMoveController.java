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

public class BasicFloatingPlatformMoveController extends BaseMoveController {

  int m_ignoreCollisionTicks = 0;

  public BasicFloatingPlatformMoveController(float speedX) {
    super(2.5f*speedX, 0);
    m_supportReverse = true;
  }

  public void move(BaseSprite sprite, PlayerSprite player, TiledMapTileLayer platformTiles, TiledMapTileLayer climbableTiles)
  {
    if (this.shouldMove(player, sprite))
    {
      this.accelerate(m_accelX*m_currDir,0);
      this.calculateNewLocation(1-m_currDir,1,sprite);
      this.getCollisions(m_tileX,m_tileY, platformTiles);

      int spriteCol  = 0;

      if (m_ignoreCollisionTicks > 0)
      {
        m_ignoreCollisionTicks--;
      } else
      {
       spriteCol = this.getSpriteCollisions(player.getParent(), sprite);
      }

      if (spriteCol == 1)
      {
        m_ignoreCollisionTicks = 60;
        m_currDir = -m_currDir;
      } else
      {
        if (m_currDir > 0)
        {
          //moving right
          if(col[5] > 0)
          {
            //platform to right, switch
            m_currDir = -1;
          }
        } else if (m_currDir < 0)
        {
          //moving left
          if(col[3] > 0)
          {
            //platform to left, switch
            m_currDir = 1;
          }
        }
      }

      sprite.setVelocity(m_dx, m_dy);
    }
  }

  public void reset()
  {
    super.reset();
    m_ignoreCollisionTicks = 0;
  }

}