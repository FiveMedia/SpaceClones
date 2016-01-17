package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.audio.*;

public class SoundPair  {

  public long soundId = 0;
  public Sound sound = null;
  public GameDrawable sprite = null;
  public float maxVolume = 0;
  public float minVolume = 0;

}