package ca.fivemedia.gamelib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.Iterator;
import java.util.ArrayList;

public interface InputManager {

  public boolean isLeftPressed();
  public boolean isRightPressed();
  public boolean isDownPressed();
  public boolean isUpPressed();
  public boolean isFirePressed();
  public boolean isSpeedPressed();
  public boolean isJumpPressed();
  public boolean nextPressed();
  public boolean buttonTapped(GameButton b);
}