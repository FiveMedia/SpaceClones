package ca.fivemedia.space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ca.fivemedia.gamelib.*;

public interface MoveController {
  public void move(BaseSprite sprite, PlayerSprite player, TiledMapTileLayer platformTiles, TiledMapTileLayer climbableTiles);
  public void setDirection(float f);
  public void reset();
  public void setSpeedXFactor(float sp);
  public void setTriggerType(int t);
  public void setSpriteTrigger(GameSprite sprite, float range);
  public boolean shouldMove(PlayerSprite player, BaseSprite sprite);
  public void triggerMotion();
  public void setStartDirection(float f);
  public void fallDead(BaseSprite sprite, TiledMapTileLayer platformTiles);
  public void setVelocity(float dx, float dy);
  public boolean isDone();
  public void setIgnoreDie();
  public void setIgnoreReverse();
  public boolean isMotionActive();
  public float getDirection();
}