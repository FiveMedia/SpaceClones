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


public class TreeSprite extends GameSprite implements SwitchChangedListener {

  GameAnimateable m_animation;

  int m_dir = 1;
  int m_startState = 0;
  float m_scale = 1.0f;
  Light pLight = null;
  //ConeLight cLight = null;
  //float lightDist = 250;
  float lightDist = 600;
  float m_lightDX, m_lightDY;
  boolean m_lightStrobe = false;
  boolean m_lightFlicker = false;
  boolean m_lightMove = false;
  float m_lightTime = 0;
  int m_lightState = 0;
  Color m_onColor = null;
  Color m_offColor = new Color(0,0,0,1);
  float m_lightOnTime, m_lightOffTime;
  MainGameLayer m_layer;
  float m_offX = 0;
  float m_offY = 0;
  float m_degreeChange = 0.1f;
  float m_maxDegrees, m_minDegrees, m_currDirection;
  boolean m_lightRotate = false;
  boolean m_switchAttached = false;
  AnimateLightFlicker m_lightFlickerIn, m_lightFlickerOut;
  PlayerSprite m_internalPlayer = null;
  float m_speedFactor = 1;
  boolean m_lightNoReverse = false;

  boolean m_soundPlaying = false;
  long m_soundId = -1;
  boolean m_playSound = false;

  public  TreeSprite (TextureAtlas myTextures, String baseName, int numFrames, int dir, float scale, String light, MainGameLayer layer,float xx, float yy, float animationSpeed) 
  {
    this(myTextures, baseName, numFrames, dir, scale, light, layer, xx,yy, animationSpeed, 1.0f);
  }

  public  TreeSprite (TextureAtlas myTextures, String baseName, int numFrames, int dir, float scale, String light, MainGameLayer layer,float xx, float yy, float animationSpeed, float lr) {
    super(myTextures.findRegion(baseName + "_F1"));
    boolean xRay = false;

    float t = MathUtils.random(0.6f, 4.0f);
    if (animationSpeed > 0f)
        t = animationSpeed;

    if (numFrames == 2)
    {
      m_animation = new AnimateSpriteFrame(myTextures, new String[] {baseName + "_F1", baseName + "_F2"}, t, -1);
    } else if (numFrames == 3)
    {
      m_animation = new AnimateSpriteFrame(myTextures, new String[] {baseName + "_F1", baseName + "_F2", baseName + "_F3"}, t, -1);
    }

    m_layer = layer;

    m_scale = scale;
    m_dir = dir;

    this.setScale(m_scale * m_dir, m_scale);

    if (m_animation != null)
      this.runAnimation(m_animation);

    Gdx.app.debug("TreeSprite", "Light = " + light);

    if (light.endsWith("X"))
    {
      xRay = true;
      light = light.substring(0, light.length()-1);
    }

    if (light.equals("Point"))
    {
      m_offX = this.getWidth()/2;
      m_offY = this.getHeight() - 15;
      yy = yy + (this.getHeight() - 15);
      xx = xx + (this.getWidth()/2);
      lightDist = lightDist*lr*0.8f;
      pLight = layer.createPointLight(new Color(0.7f,0.5f, 0.5f, 0.6f), lightDist, xx, yy);
      pLight.setXray(xRay);
    } else if (light.equals("Big"))
    {

      m_offX = this.getWidth()/2;
      m_offY = this.getHeight() - 15;

      yy = yy + (this.getHeight() - 15);
      xx = xx + (this.getWidth()/2);
      //m_lightStrobe = true;
      //m_lightOnTime = MathUtils.random(0.1f, 0.4f);
      //m_lightOffTime = MathUtils.random(0.2f, 1.1f);
      //m_lightFlicker = true;
      m_lightMove = false;
      m_lightDX = -1.5f;
      lightDist = lightDist*lr;
      m_onColor = new Color(0.7f,0.7f, 0.7f, 0.8f);
      pLight = layer.createPointLight(m_onColor, lightDist, xx, yy);
      pLight.setXray(xRay);
    } else if (light.equals("Strobe"))
    {
      m_offX = this.getWidth()/2;
      m_offY = this.getHeight() - 15;

      yy = yy + (this.getHeight() - 15);
      xx = xx + (this.getWidth()/2);
      //m_lightStrobe = true;
      //m_lightOnTime = MathUtils.random(0.1f, 0.4f);
      //m_lightOffTime = MathUtils.random(0.2f, 1.1f);
      //m_lightFlicker = true;
      m_lightMove = false;
      m_lightStrobe = true;
      //m_lightFlicker = true;
      m_lightOnTime = 0.1f;
      m_lightOffTime = 0.25f;
      m_lightDX = -1.5f;
      m_onColor = new Color(0.7f,0.7f, 0.7f, 0.8f);
      lightDist = lightDist*lr;
      pLight = layer.createPointLight(m_onColor, lightDist, xx, yy);
      pLight.setXray(true);
    } else if (light.equals("GreenGlow"))
    {
      m_offX = this.getWidth()/2;
      m_offY = this.getHeight()/2;
      lightDist = 600 * lr;
      pLight = layer.createPointLight(new Color(0.16f,0.9f, 0.16f, 0.6f), lightDist, xx + m_offX, yy + m_offY);
    }  else if (light.equals("GreenGoo"))
    {
      m_offX = this.getWidth()/2;
      m_offY = this.getHeight()-8;
      lightDist = 170 * lr;
      pLight = layer.createPointLight(new Color(0.16f,0.9f, 0.16f, 0.27f), lightDist, xx + m_offX, yy + m_offY);
      pLight.setXray(true);
    } else if (light.equals("WhiteGoo"))
    {
      m_offX = this.getWidth()/2;
      m_offY = this.getHeight()-8;
      lightDist = 170 * lr;
      pLight = layer.createPointLight(new Color(0.9f,0.9f, 0.9f, 0.27f), lightDist, xx + m_offX, yy + m_offY);
      pLight.setXray(true);
    }else if (light.startsWith("Spotlight"))
    { 
      if (light.equals("Spotlight"))
      {
        m_offX = 1;
        m_offY = this.getHeight()/2;
        lightDist = 600*lr;

        float dirDegrees = 0;
        if (dir == -1)
        {
          dirDegrees = 180;
          m_offX = this.getWidth() - 1;
        }
        pLight = layer.createConeLight(new Color(0.7f,0.6f, 0.5f, 0.9f), lightDist, xx + m_offX, yy + m_offY, dirDegrees, 20);
        pLight.setXray(xRay);
      } else if (light.equals("SpotlightPan"))
      {
        m_offX = 1;
        m_offY = this.getHeight()/2;
        lightDist = 600*lr;
        
        m_currDirection = 0;
        if (dir == -1)
        {
          m_currDirection = 180;
          m_offX = this.getWidth() - 1;
        }

        pLight = layer.createConeLight(new Color(0.7f,0.6f, 0.5f, 0.9f), lightDist, xx + m_offX, yy + m_offY, m_currDirection, 20);
        pLight.setXray(xRay);
        m_degreeChange = -0.75f;
        m_minDegrees = m_currDirection - 30;
        m_maxDegrees = m_currDirection + 30;
        m_lightRotate = true;
      } else if (light.equals("SpotlightPanWide"))
      {
        m_offX = 1;
        m_offY = this.getHeight()/2;
        lightDist = 600*lr;
        
        m_currDirection = 0;
        if (dir == -1)
        {
          m_currDirection = 180;
          m_offX = this.getWidth() - 1;
        }

        pLight = layer.createConeLight(new Color(0.7f,0.6f, 0.5f, 0.9f), lightDist, xx + m_offX, yy + m_offY, m_currDirection, 20);
        pLight.setXray(xRay);
        m_degreeChange = -0.75f;
        m_minDegrees = m_currDirection - 50;
        m_maxDegrees = m_currDirection + 50;
        m_lightRotate = true;
      }
    } else if (light.equals("StarBlue"))
    {
      Gdx.app.debug("TreeSprite", "StarBlue Light: LR=" + lr);
      m_offX = this.getWidth()/2;
      m_offY = this.getHeight()/2;
      yy = yy + (this.getHeight() - m_offY);
      xx = xx + m_offX;
      lightDist = 500f*lr;
      pLight = layer.createPointLight(new Color(0.2f,0.2f, 0.6f, 0.8f), lightDist, xx, yy);
      pLight.setXray(xRay);
    } else if (light.equals("Star"))
    {
      Gdx.app.debug("TreeSprite", "Star Light: LR=" + lr);
      m_offX = this.getWidth()/2;
      m_offY = this.getHeight()/2;
      yy = yy + (this.getHeight() - m_offY);
      xx = xx + m_offX;
      lightDist = 500f*lr;
      pLight = layer.createPointLight(new Color(0.7f,0.7f, 0.6f, 0.7f), lightDist, xx, yy);
      pLight.setXray(xRay);
    } else if (light.equals("StarRed"))
    {
      Gdx.app.debug("TreeSprite", "StarRed Light: LR=" + lr);
      m_offX = this.getWidth()/2;
      m_offY = this.getHeight()/2;
      yy = yy + (this.getHeight() - m_offY);
      xx = xx + m_offX;
      lightDist = 500f*lr;
      pLight = layer.createPointLight(new Color(0.8f,0.2f, 0.2f, 0.8f), lightDist, xx, yy);
      pLight.setXray(xRay);
    } else if (light.equals("Alarm"))
    {
      if (baseName.startsWith("Light"))
      {
        m_offX = this.getWidth()/2;
        m_offY = this.getHeight()/4;
      } else
      {
        m_offX = this.getWidth()/2;
        m_offY = this.getHeight()/2;
      }

      m_playSound = true;
      lightDist = 600*lr;
      m_currDirection = 0;

      pLight = layer.createConeLight(new Color(0.9f,0.3f, 0.3f, 0.9f), lightDist, xx + m_offX, yy + m_offY, m_currDirection, 35);
      pLight.setXray(xRay);
      m_degreeChange = -18f * dir;
      m_minDegrees = m_currDirection - 180;
      m_maxDegrees = m_currDirection + 180;
      m_lightRotate = true;
      m_lightNoReverse = true;
      pLight.setXray(xRay);
    }

    if (pLight != null)
    {
      m_lightFlickerOut = new AnimateLightFlicker(0.7f, false);
      m_lightFlickerIn = new AnimateLightFlicker(0.7f, true);
    }

    if (baseName.startsWith("glass"))
    {
      this.setOpacity(0.65f);
    }
  }

  public void setSwitch(SwitchSprite s, int startState)
  {
    m_startState = startState;
    setLightState(s.getState(), false);
    s.addLight(this);
    m_switchAttached = true;
  }

  public void setLightState(int state, boolean animate)
  {
    if (animate == false)
    {
      if (m_startState == 0)
      {
        if (state == 0)
        {
          pLight.setActive(false);
        } else
        {
          pLight.setActive(true);
        }
      } else
      {
        if (state == 1)
        {
          pLight.setActive(false);
        } else
        {
          pLight.setActive(true);
        }
      }
    } else
    {
      this.playSoundPanVolume("lightFlicker", 0.9f,0.4f);
      if (m_startState == 0)
      {
        if (state == 0)
        {
          this.runAnimation(m_lightFlickerOut);
        } else
        {
          this.runAnimation(m_lightFlickerIn);
        }
      } else
      {
        if (state == 1)
        {
          this.runAnimation(m_lightFlickerOut);
        } else
        {
          this.runAnimation(m_lightFlickerIn);
        }
      }
    }
  }

  public void switchStateChanged(int state, boolean animate)
  {
    setLightState(state, animate);
  }

  public void setPlayer(PlayerSprite player)
  {
    m_internalPlayer = player;
  }

  public void playSoundPanVolume(String sound, float max, float min)
  {
    if (m_internalPlayer != null)
      this.playSound(sound,m_internalPlayer, this, max, min);
    else
      this.playSound(sound, max);
  }

  public void update(float deltaTime)
  {
    if (pLight != null)
    {
      float d = MathUtils.random(lightDist-25, lightDist + 25);
      pLight.setDistance(d);
      pLight.setPosition(this.getX() + m_offX, this.getY()+m_offY);
    }

    if (m_lightStrobe)
    {
      m_lightTime += deltaTime;
      if (m_lightState == 0)
      {
        if (m_lightTime > m_lightOnTime)
        {
          m_lightTime = 0;
          m_lightState = 1;
          //pLight.setColor(m_offColor);
          pLight.setActive(false);
        }
      } else
      {
        if (m_lightTime > m_lightOffTime)
        {
          m_lightTime = 0;
          m_lightState = 0;
          //pLight.setColor(m_onColor);
          pLight.setActive(true);
          if (m_lightFlicker)
          {
             m_lightOnTime = MathUtils.random(0.02f, 0.1f);
             m_lightOffTime = MathUtils.random(0.5f, 1.5f);
          }
        }
      }
    }


    if (m_lightMove)
    {
      m_lightTime += deltaTime;
      if (m_lightTime > 1.5f)
      {
        m_lightDX = -m_lightDX;
        m_lightTime = 0;
      }

      pLight.setPosition(pLight.getPosition().x + m_lightDX, pLight.getPosition().y);
    } 

    if (m_lightRotate)
    {
      m_currDirection += m_degreeChange * m_speedFactor;
      if (m_lightNoReverse)
      {
         if ((m_degreeChange > 0) && (m_currDirection > m_maxDegrees))
        {
          m_currDirection = m_minDegrees;
        } else if ((m_degreeChange < 0) && (m_currDirection < m_minDegrees))
        {
          m_currDirection = m_maxDegrees;
        }
      } else
      {
        if ((m_degreeChange > 0) && (m_currDirection > m_maxDegrees))
        {
          m_degreeChange = -m_degreeChange;
        } else if ((m_degreeChange < 0) && (m_currDirection < m_minDegrees))
        {
          m_degreeChange = -m_degreeChange;
        }
      }

      pLight.setDirection(m_currDirection);

    }

    /*
    if (m_playSound)
    {
      if (m_soundPlaying == false)
      {
        if (m_parent.isOnScreen(this.getX(), this.getY()))
        {
          if (m_soundId < 0)
          {
            m_soundId = loopSoundManageVolume("alarm", this, m_internalPlayer, 0.4f,0.2f);
            m_soundPlaying = true;
          }
        }
      } else if (m_parent.isOnScreen(this.getX(), this.getY()) == false)
      {
        this.stopSound("alarm", m_soundId);
        m_soundPlaying = false;
        m_soundId = -1;
      }
    } */

  }

  public boolean isCollidable()
  {
    return false;
  }

  @Override
  public void setVisible(boolean v)
  {
    super.setVisible(v);
    if (pLight != null)
    {
      if (m_switchAttached == false)
        pLight.setActive(v);
    }

  }

  public void setSpeed(float s)
  {
    m_speedFactor = s;
  }

  public Light getLight()
  {
    return pLight;
  }

  public void resetLevel()
  {
      this.setVisible(true);
      this.stopAllAnimations();
      this.setOpacity(1.0f);
      this.setScale(m_scale * m_dir, m_scale);
      if (m_animation != null)
        this.runAnimation(m_animation);
  }
}

