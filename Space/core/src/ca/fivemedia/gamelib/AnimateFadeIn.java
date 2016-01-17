package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;

public class AnimateFadeIn extends AnimateFade {

	public AnimateFadeIn (float duration) 
  {
    super(duration, 0f, 1.0f);
  }

  public  AnimateFadeIn (float duration, float o) {
    super(duration, 0, o);
  }

  public  AnimateFadeIn (float duration, float o, int repeat) {
    super(duration, 0, o, repeat);
  }
}