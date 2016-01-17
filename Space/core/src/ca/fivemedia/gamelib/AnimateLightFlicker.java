package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.*;
import box2dLight.*;

public class AnimateLightFlicker extends GameAnimation {

  protected boolean m_on = true;
  protected float m_nextChange = 0;

  public  AnimateLightFlicker (float duration, boolean on) {
    super(duration, 1);
    m_on = on;
  }

  public void animateStep(float deltaTime, float time)
  {
    Light light = m_targetSprite.getLight();
    if (time > m_nextChange)
    {
      m_nextChange = time + MathUtils.random(0, 0.12f);
      //float currDist = light.getDistance();
      if (light.isActive())
      {
        light.setActive(false);
      } else
      {
        light.setActive(true);
      }
    }

    if (m_running == false)
    {
      light.setActive(m_on);
    }

  }

  public void initializeAnimation()
  {
    m_nextChange = MathUtils.random(0, 0.04f);
  }
}