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

public class SimplePlatformMoveController extends BaseMoveController {

  int m_ignoreTicks = 0;

  public SimplePlatformMoveController(float speedX, float speedY) {
    super(speedX, speedY);
    m_supportReverse = true;
  }


  public void reset()
  {

  }

  public void move(BaseSprite sprite, PlayerSprite player, TiledMapTileLayer platformTiles, TiledMapTileLayer climbableTiles)
  {

    if (this.shouldMove(player,sprite))
    {

      this.accelerate(m_accelX*m_currDir,0);

      if (m_ignoreTicks > 0)
      {
        m_ignoreTicks--;
      } else
      {
       

        int spriteCol = 0;
        spriteCol = this.getSpriteCollisions(sprite.getParent(), sprite);

        if ((spriteCol == 1) && (m_supportReverse))
        {
          //Gdx.app.debug("SimplePlatformMoveController", "should reverse!!!!!!!");
          m_ignoreTicks = 30;
          if (maxSpeedX > 4)
            m_ignoreTicks = 20;

          m_currDir = -m_currDir;
        } else if (spriteCol == 2)
        {
          //Gdx.app.debug("SimplePlatformMoveController", "should die!!!!!!!");
          sprite.die();
          m_dx = 0;
          m_dy = 0;
          sprite.setVelocity(0, 0);
          m_ignoreTicks = 60;
          return;
        }

        
        float bottomY = sprite.getY() + m_dy;
        int tileY = (int) Math.floor(bottomY/th);

        if (m_currDir > 0)
        {
          //moving right
          float xx = sprite.getX() + m_dx + sprite.getWidth();
          int tileX = (int) Math.floor(xx/tw);
          int lc = getCellAt(tileX, tileY - 1, platformTiles);
          int lc2 = getCellAt(tileX, tileY, platformTiles);

          if((lc < 0) || (lc == 4) || (lc2 > 0))
          {
            //nothing to the right
            m_currDir = -1;
            m_dx = 0;
          }
        } else if (m_currDir < 0)
        {
          //moving left
          float xx = sprite.getX() + m_dx;
          int tileX = (int) Math.floor(xx/tw);
          int lc = getCellAt(tileX, tileY - 1, platformTiles);
          int lc2 = getCellAt(tileX, tileY, platformTiles);

          if((lc < 0) || (lc == 4) || (lc2 > 0))
          {
            //nothing to the left
            m_currDir = 1;
            m_dx = 0;
          }
        }

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
        }

      }
    }

    sprite.setVelocity(m_dx, m_dy);

  }



}