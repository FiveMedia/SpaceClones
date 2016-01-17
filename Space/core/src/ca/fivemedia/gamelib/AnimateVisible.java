package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;

public class AnimateVisible extends GameAnimation {

  boolean m_visibleTarget = false;

  public  AnimateVisible (boolean visible) {
    super(0.01f, 1);
    m_visibleTarget = visible;
  }

  public void animateStep(float deltaTime, float time)
  {
    m_target.setVisible(m_visibleTarget);
  }

  public void initializeAnimation()
  {

  }
  
}