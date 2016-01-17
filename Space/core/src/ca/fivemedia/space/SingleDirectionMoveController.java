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

public class SingleDirectionMoveController extends BaseMoveController {

  int m_ignoreCollisionTicks = 0;
  boolean m_active = false;
  boolean m_dieLeftOfPlayer = false;
  int m_ticks = 0;
  int m_delayTicks = 0;

  public SingleDirectionMoveController(float speedX, boolean supportReverse, int delayTicks) {
    super(speedX, 0);
    m_accelY = 0f;
    m_accelX = 2.0f;
    m_supportReverse = supportReverse;
    m_delayTicks = delayTicks;
  }

  public SingleDirectionMoveController(float speedX, boolean supportReverse, boolean dieLeftOfPlayer) {
    super(speedX, 0);
    m_accelY = 0f;
    m_accelX = 2.0f;
    m_supportReverse = supportReverse;
    m_dieLeftOfPlayer = dieLeftOfPlayer;
  }


  public void move(BaseSprite sprite, PlayerSprite player, TiledMapTileLayer platformTiles, TiledMapTileLayer climbableTiles)
  {
    if (this.shouldMove(player,sprite))
    {
      if (m_ticks < m_delayTicks)
      {
        m_ticks++;
        return;
      }

      this.accelerate(m_accelX*m_currDir, 0);
      int spriteCol = 0;

      if (m_ignoreCollisionTicks > 0)
      {
        m_ignoreCollisionTicks--;
      } else
      {
       spriteCol = this.getSpriteCollisions(sprite.getParent(), sprite);
      }

      if ((spriteCol == 1) && (m_supportReverse))
      {
        m_ignoreCollisionTicks = 30;
        //Gdx.app.debug("SingleDirMove", "reverse dir");
        m_currDir = -m_currDir;
        if (maxSpeedX > 4)
          m_ignoreCollisionTicks = 32;
        
      } else if ((spriteCol == 5) && (m_supportReverse))
      {
        m_ignoreCollisionTicks = 30;
        //Gdx.app.debug("SingleDirMoveController", "REVERSE AND STOP");
        m_currDir = -m_currDir;
        m_dy = 0;
        m_dx = 0;
        m_motionActive = false;
        m_done = true;
      } else if ((spriteCol == 2) && (!m_ignoreDie))
      {
        //Gdx.app.debug("SingleDirMove", "die");
        sprite.die();
        m_dx = 0;
        m_dy = 0;
      } else if (m_dieLeftOfPlayer)
      {
        if (sprite.getX() < (player.getX() - 1800))
        {
          sprite.die();
          m_dx = 0;
          m_dy = 0;
        }
      } 

      sprite.setVelocity(m_dx, m_dy);
    }
  }

  public void reset()
  {
    super.reset();
    m_ignoreCollisionTicks = 0;
    m_done = false;
  }

}