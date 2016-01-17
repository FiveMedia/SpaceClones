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


public class LightningSprite extends GameSprite {

  GameAnimateable m_animation;

  int m_dir = 1;
  float m_scale = 1.0f;
  PointLight pLight = null;
  float m_nextFlash = 5.0f;
  float lightDist = 900;
  MainGameLayer m_layer;
  float m_lightTime = 0;
  int m_lightState = 0;

  public  LightningSprite (TextureAtlas myTextures, MainGameLayer layer) {
    super(myTextures.findRegion("lightning_F1"));

    //float t = MathUtils.random(0.3f, 0.6f);
    float t = 0.2f;
    m_animation = new AnimateSpriteFrame(myTextures, new String[] {"lightning_F1", "lightning_F2", "lightning_F3"}, t, 1);
    m_layer = layer;

    m_dir = 1;

    //this.setScale(m_scale * m_dir, m_scale);


      pLight = layer.createPointLight(new Color(0.8f,0.3f, 0.3f, 0.5f), lightDist, 0, 0);
      pLight.setXray(true);
      //layer.setAmbientLight(0.03f, 0.03f, 0.03f, 0.4f);
      pLight.setActive(false);
      m_lightTime = 0;
      this.setVisible(false);
  }

  public void update(float deltaTime)
  {
    m_lightTime += deltaTime;
    if (m_lightState == 0)
    {
      if (m_lightTime > m_nextFlash)
      {
        m_nextFlash = MathUtils.random(0.5f, 2.0f);
        this.setPosition(m_layer.playerSprite.getX() + MathUtils.random(-300,300), m_layer.playerSprite.getY()-MathUtils.random(50,300));
        pLight.setPosition(this.getX(), this.getY() + this.getHeight()/2);
        pLight.setActive(true);
        this.setOpacity(1.0f);
        this.setVisible(true);
        this.runAnimation(m_animation);
        m_lightState = 1;
        m_lightTime = 0;
        m_layer.setAmbientLight(0.7f, 0.2f,0.2f,0.7f);
        float xs = MathUtils.random(-1.5f, 1.5f);
        float ys = Math.abs(xs);
        if (ys < 0.25)
        {
          ys = 0.25f;
          xs = 0.25f;
        }

        this.setScale(xs,ys);
      }
    } else if (m_lightState == 1)
    {
      if (m_lightTime > 0.25f)
      {
        m_lightState = 2;
        m_lightTime = 0;
        m_layer.resetAmbientLight();
        pLight.setActive(false);
        this.setVisible(false);
      }
    } else if (m_lightState == 2)
    {
      if (m_lightTime > m_nextFlash)
      {
        float vv = MathUtils.random(0.08f, 0.25f);
        /*
        if (MathUtils.random(1,10) > 4)
          this.playSound("thunder1", vv);
        else
          this.playSound("thunder2", vv); */

        m_nextFlash = MathUtils.random(4,11);
        m_lightState = 0;
        m_lightTime = 0;
      }
    }
  }

  public boolean isCollidable()
  {
    return false;
  }

  public void resetLevel()
  {
      this.setVisible(false);
      this.stopAllAnimations();
      this.setOpacity(1.0f);
      this.setScale(m_scale * m_dir, m_scale);
      m_lightState = 0;
      m_layer.resetAmbientLight();
  }

}

