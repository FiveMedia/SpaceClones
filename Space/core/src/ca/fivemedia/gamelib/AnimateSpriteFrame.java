package ca.fivemedia.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.Gdx;

public class AnimateSpriteFrame extends GameAnimation {

  protected Animation m_animation;
  protected int m_numFrames = 0;

  public AnimateSpriteFrame(TextureAtlas myTextures, String[] frames, float duration, int repeat) {
    super(duration, repeat);
    m_numFrames = frames.length;
    TextureAtlas.AtlasRegion[] trAni = new TextureAtlas.AtlasRegion[m_numFrames];
    for (int i=0; i < m_numFrames; i++)
    {
      trAni[i] = myTextures.findRegion(frames[i]);
      //Gdx.app.debug("space", "Frame " + i + " name:" + frames[i]);
    }

    float frameRate = duration / m_numFrames;
    m_animation = new Animation(frameRate, trAni);
  }

  public void animateStep(float deltaTime, float time)
  {
    m_targetSprite.setRegion(m_animation.getKeyFrame(time, true));
  }

  public void setStartFrame()
  {
    m_targetSprite.setRegion(m_animation.getKeyFrame(0f, true));
  }

  public TextureRegion getStartFrame()
  {
    return m_animation.getKeyFrame(0f, true);
  }

  public int getKeyFrameIndex()
  {
    return m_animation.getKeyFrameIndex(m_time);
  }


  public void initializeAnimation()
  {
    
  }
}