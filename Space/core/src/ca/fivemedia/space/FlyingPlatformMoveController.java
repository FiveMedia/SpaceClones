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

public class FlyingPlatformMoveController extends BaseMoveController {

  int yTicks = 0;
  int m_swoopTicks = 40;
  float m_yDir = 1.0f;
  float startMaxSpeedY;
  int ignoreTicks = 0;

  public FlyingPlatformMoveController(float speedX, float speedY, int swoopTicks, float accelY) {
    super(speedX, speedY);
    m_swoopTicks = swoopTicks;
    m_accelY = accelY;
    startMaxSpeedY = speedY;
  }

  public void reset()
  {
    yTicks = 0;
    m_yDir = 1.0f;
    maxSpeedY = startMaxSpeedY;
  }

  public void move(BaseSprite sprite, PlayerSprite player, TiledMapTileLayer platformTiles, TiledMapTileLayer climbableTiles)
  {
    yTicks++;
    if (yTicks > m_swoopTicks)
    {
        m_yDir = -m_yDir;
        yTicks = 0;
    }


    this.accelerate(m_accelX*m_currDir,m_accelY*m_yDir);
    this.calculateNewLocation(-m_currDir,0,sprite);

    if (ignoreTicks > 0)
    {
      ignoreTicks--;
    } else
    {
      int spriteCol = 0;
      spriteCol = this.getSpriteCollisions(sprite.getParent(), sprite);

      if (spriteCol == 1)
      {
        ignoreTicks = 60;
        m_currDir = -m_currDir;
      } else if (spriteCol == 2)
      {
        sprite.die();
        m_dx = 0;
        m_dy = 0;
        sprite.setVelocity(0, 0);
        ignoreTicks = 90;
        return;
      }
    }

    int cd = (int)m_currDir;
    int c1 = this.getTileBelow(m_tileX,m_tileY, platformTiles, 6);
    int c2 = this.getTileBelow(m_tileX+cd, m_tileY, platformTiles, 6);
    int c3 = this.getTileBelow(m_tileX+cd*2, m_tileY, platformTiles, 6);
    int c4 = this.getTileBelow(m_tileX+cd*3, m_tileY, platformTiles, 6);

    if ((c1 < 0) && (c2 < 0) && (c3 < 0) && (c4 < 0))
    {
      m_currDir = -m_currDir;
    }

    sprite.setVelocity(m_dx, m_dy);
  }
}