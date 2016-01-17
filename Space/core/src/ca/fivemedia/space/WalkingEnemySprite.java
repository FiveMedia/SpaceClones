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

public abstract class WalkingEnemySprite extends BaseSprite {

  float m_accelX = 1.0f;
  float m_accelY = 0;
  protected float m_scale = 1.0f;
  int m_vertical = 0;

  public WalkingEnemySprite(TextureRegion region, TiledMapTileLayer pTiles, TiledMapTileLayer lTiles, float speedX, float speedY, int vertical, float scale, boolean destroy, int delayTicks) {
    super(region,pTiles,lTiles);
    m_vertical = vertical;
    m_scale = scale;
    if (m_vertical != 0)
    {
        m_moveController = new VerticalPlatformMoveController(speedX, speedX, m_vertical);
        this.setOrigin(this.getWidth()/2,this.getHeight()/2);
        this.setRotation(-90 * m_vertical);  
    }
    else
    {
      if (!destroy)
      {
        m_moveController = new SimplePlatformMoveController(speedX, speedY);
        if (m_scale > 1)
          this.setOrigin(this.getWidth()/2,0);
        
      } else
      {
        m_moveController = new SingleDirectionMoveController(4.0f * speedX, true, delayTicks);
        this.setOrigin(this.getWidth()/2,0);
      }
    }
  }

  @Override
  public void setSpeed(float sp)
  {
    m_moveController.setSpeedXFactor(sp);
  }

  public void move()
  {


    m_moveController.move(this, m_internalPlayer, m_platformTiles, m_climbableTiles);
    //if (m_vertical == 1)
    //{
    //  if (m_dy > 0)
    //  {
    //    this.setScale(-m_scale * m_vertical, m_scale);
    //  } else if (m_dy < 0)
    //  {
    //    this.setScale(m_scale * m_vertical, m_scale);
    //  }

    //} else 
    if (m_vertical != 0)
    { 
      if (m_dy > 0)
      {
        this.setScale(-m_scale * m_vertical, -m_scale);
      } else if (m_dy < 0)
      {
        this.setScale(m_scale * m_vertical, -m_scale );
      }

    }else
    {
      if (m_dx > 0)
      {
        this.setScale(m_scale,m_scale);
      } else if (m_dx < 0)
      {
        this.setScale(-m_scale,m_scale);
      }
    }

  }

  public void setStartDirection(float f)
  {
    super.setStartDirection(f);
    if (m_vertical != 0)
    { 
      this.setScale(-f * m_scale * m_vertical, -m_scale);
    } else
    {
      this.setScale(m_scale * f,m_scale);
    }

  }

}