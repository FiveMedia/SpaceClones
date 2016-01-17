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

public class VerticalFloatingPlatformMoveController extends BaseMoveController {

  int m_ignoreCollisionTicks = 0;
  boolean m_active = false;

  public VerticalFloatingPlatformMoveController(float speed) {
    super(0, 2.0f*speed);
    m_accelY = 1.0f;
    m_supportReverse = true;
  }

  public void move(BaseSprite sprite, PlayerSprite player, TiledMapTileLayer platformTiles, TiledMapTileLayer climbableTiles)
  {
    if (this.shouldMove(player,sprite))
    {
      this.accelerate(0, m_accelY*m_currDir);
      //this.calculateNewLocation(1,1,sprite);
      int spriteCol = 0;

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
      }

      sprite.setVelocity(m_dx, m_dy);
    }
  }

}