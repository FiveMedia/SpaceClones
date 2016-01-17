package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import ca.fivemedia.gamelib.*;
import com.badlogic.gdx.math.MathUtils;
import box2dLight.*;


public class MoonSprite extends GameSprite {

  PointLight pLight = null;

  public  MoonSprite (TextureAtlas myTextures, MainGameLayer layer) {
    super(myTextures.findRegion("moon_F1"));
    pLight = layer.createPointLight(new Color(0.8f,0.8f, 0.5f, 0.5f), 1500, 0, 0);
    pLight.setXray(true);

  }

  public void update(float deltaTime)
  {
    pLight.setPosition(this.getX() + this.getWidth()/2, this.getY() + this.getHeight()/2);
  }

  public boolean isCollidable()
  {
    return false;
  }

  public void setPosition(float xx, float yy)
  {
    super.setPosition(xx,yy);
    pLight.setPosition(xx + this.getWidth()/2, yy + this.getHeight()/2);
  }

  public void disableLight()
  {
    pLight.setActive(false);
  }

  public void resetLevel()
  {
    pLight.setActive(true);
  }

}

