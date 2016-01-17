package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import ca.fivemedia.gamelib.*;


public class TouchDetails {

  public int finger;
  public float currX, currY;
  public boolean isDown = false;
  public boolean lastStateDown = false;
  public boolean tapped = false;
  public float startX, startY, screenX, screenY;
  public int ticksDown = 0;
  public boolean inDrag = false;

  public TouchDetails() {

  }

}

