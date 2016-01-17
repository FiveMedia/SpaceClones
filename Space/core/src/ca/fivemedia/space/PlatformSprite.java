package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import ca.fivemedia.gamelib.*;
import box2dLight.*;


public class PlatformSprite extends BaseSprite {

    //public GameAnimateable m_cloudAnimation;
    PlayerSprite m_playerSprite;
    boolean m_movingSoundPlaying = false;
    long m_soundId = 0;

    Light pLight = null;
    float lightDist = 1000f;
    float m_offX, m_offY;

    public PlatformSprite(TextureAtlas myTextures, TiledMapTileLayer pTiles, TiledMapTileLayer lTiles, String move, float speed, float onDT, float offDT, float delayT, int trigger, int groupId, PlayerSprite playerSprite, String spriteName, float lightRange, MainGameLayer layer) {
      this(myTextures, pTiles, lTiles, move, speed, onDT, offDT, delayT, trigger, groupId, playerSprite, spriteName);

      m_offX = this.getWidth()/2;
      m_offY = this.getHeight();

      if (lightRange > 0)
      {
        lightDist = 800 * lightRange;
        pLight = layer.createConeLight(new Color(0.7f,0.6f, 0.5f, 0.9f), lightDist, m_offX, m_offY, 270, 35);
      } else
      {
        m_offY = this.getHeight()/2;
        lightDist = 225;
        pLight = layer.createPointLight(new Color(0.16f,0.9f, 0.16f, 0.45f), lightDist, m_offX, m_offY);
        pLight.setXray(true);
      }
    }
   
    
    public PlatformSprite(TextureAtlas myTextures, TiledMapTileLayer pTiles, TiledMapTileLayer lTiles, String move, float speed, float onDT, float offDT, float delayT, int trigger, int groupId, PlayerSprite playerSprite, String spriteName) {
    super(myTextures.findRegion(spriteName),pTiles,lTiles);
    m_groupId = groupId;
    m_playerSprite = playerSprite;
    super.setPlayer(playerSprite);

    if (move.equals("basic"))
    {
      m_moveController = new BasicFloatingPlatformMoveController(speed);
      //moves side to side between platforms squares, start dir is which ever side is 'open'
    } else if (move.equals("vertical"))
    {
      m_moveController = new VerticalFloatingPlatformMoveController(speed);
    } else
    {
      m_moveController = new StaticMoveController(0f);
    }

    m_moveController.setTriggerType(trigger);

    //fading platforms
    if (onDT > 0)
    {
      AnimateFadeOut out = new AnimateFadeOut(0.5f);
      AnimateFadeIn in = new AnimateFadeIn(0.5f);
      AnimateDelay d1 = new AnimateDelay(onDT);
      AnimateDelay d2 = new AnimateDelay(offDT);
      AnimatePlaySound s1 = new AnimatePlaySound("platformOff", playerSprite, 0.2f, 0.05f);
      AnimatePlaySound s2 = new AnimatePlaySound("platformOn", playerSprite, 0.2f, 0.05f);
      GameAnimateable[] a = {d1,s1,out,d2,s2,in};
      GameAnimateable startDelay = new AnimateDelay(delayT);
      GameAnimationSequence fade = new GameAnimationSequence(a,-1);
      GameAnimateable[] aa = {startDelay,fade};
      this.runAnimation(new GameAnimationSequence(aa,-1));

    }

    //m_walkAnimation = new AnimateSpriteFrame(myTextures, new String[] {"GreenCloud_F1"}, 1.0f, -1);
    //this.runAnimation(m_walkAnimation);
    m_numSounds = 0;
    /*AnimateScaleTo g = new AnimateScaleTo(0.3f, 1.0f, 1.0f, 1.1f, 1.1f);
    AnimateScaleTo s = new AnimateScaleTo(0.3f, 1.1f, 1.1f, 1.0f, 1.0f);
    AnimateRotateTo r = new AnimateRotateTo(8.6f,0f,359f, -1);
    GameAnimateable[] a = {g,s};
    GameAnimateable seq = new GameAnimationSequence(a,-1);
    this.runAnimation(r);
    this.runAnimation(seq);*/
  }

  public boolean isCollidable()
  {
    return (this.isVisible() && (this.getOpacity() > 0.4f));
  }

  public void move()
  {
    m_moveController.move(this, m_playerSprite, m_platformTiles, m_climbableTiles);
    if ((m_dx != 0) || (m_dy != 0))
    {
      if (m_movingSoundPlaying == false)
      {
        if (m_parent.isOnScreen(this.getX(), this.getY()))
        {
          m_soundId = this.loopSoundManageVolume("platformMove", this, m_playerSprite, 0.12f,0.05f);
          //m_soundId = this.loopSound("platformMove", 0.1f);
          m_movingSoundPlaying = true;
        }
      } else if (m_parent.isOnScreen(this.getX(), this.getY()) == false)
      {
        this.stopSound("platformMove", m_soundId);
        m_soundId = 0;
        m_movingSoundPlaying = false;
      }
    }

    if (pLight != null)
    {
      pLight.setPosition(this.getX() + m_offX, this.getY() + m_offY);
    }
  }

  @Override
  public void playDeathSound()
  {
    //this.stopSound(m_soundPrefix + m_lastSoundNum);
    //if (m_soundPrefix != null)
     //this.playSound(m_soundPrefix + "Death", 0.15f);
  }

  @Override
  public void hitPlayer(PlayerSprite player)
  {

  }

  @Override
  public void hitByAttack()
  {
    /*
    this.stopAllAnimations();
    this.runAnimation(m_deathAnimation);
    m_dying = true;
    this.playDeathSound();
    */
  }

  public float getDX()
  {
    return m_dx;
  }

  public void setVisible(boolean v)
  {
    super.setVisible(v);
    if (pLight != null)
    {
      pLight.setActive(v);
    }
  }

  public void setPosition(float xx, float yy)
  {
    super.setPosition(xx,yy);
    if (pLight != null)
    {
      pLight.setPosition(xx + m_offX, yy + m_offY);
    }
  }

  @Override
  public void resetLevel()
  {
    m_dx = 0;
    m_dy = 0;
    m_dying = false;
    m_alive = true;
    m_spawning = false;
    m_movingSoundPlaying = false;
    this.stopSound("platformMove");
    this.stopSound("platformOn");
    this.stopSound("platformOff");

    //Gdx.app.debug("PlatformSprite", "resetLevel called, m_startX = " + m_startX);

    if (m_startX >= 0)
    {
      this.setPosition(m_startX, m_startY);
    }

    if (m_soundPrefix != null)
      this.stopSound(m_soundPrefix + m_lastSoundNum);

    m_moveController.reset();
  }

  public Light getLight()
  {
    return pLight;
  }

  @Override
  public void hitByBlock()
  {
    /*
    this.stopAllAnimations();
    m_deathAnimation = m_flattenAnimation;
    this.setOrigin(this.getOriginX(), 0);
    this.runAnimation(m_deathAnimation);
    m_dying = true;
    */
  }

}