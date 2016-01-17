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

public class StaticMoveController extends BaseMoveController {


  public StaticMoveController(float bounceSize) {
    super(0,0);
  }

  public void reset()
  {
    
  }

  public void move(BaseSprite sprite, PlayerSprite player, TiledMapTileLayer platformTiles, TiledMapTileLayer climbableTiles)
  {
    sprite.setVelocity(0,0);
    //sprite.setVelocity(m_dx, m_dy);
  }

}