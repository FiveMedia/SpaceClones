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
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;
import box2dLight.*;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


//Zombie's move slowly, and don't climb or fall off platforms
//You can kill them with all weapons
public class SlimeSprite extends WalkingEnemySprite {
    MainGameLayer m_gameLayer = null;
    int m_explodeTicks = -1;
    GameAnimateable m_hitAnimation, m_glowAnimation;
    int m_vertical = 0;
    PointLight pLight;
    boolean m_glow = false;
    int m_lightDistance = 150;
    boolean m_destroyStuff = false;
    int m_iter  = 0;
    Rectangle m_bb = new Rectangle();
    float m_xOff, m_yOff;

    public SlimeSprite(TextureAtlas myTextures, TiledMapTileLayer pTiles, TiledMapTileLayer lTiles, float sp, MainGameLayer layer, int v, float scale) {
    super(myTextures.findRegion("slime_e_F1"),pTiles,lTiles, 2f * sp, 12f,v, scale,false,0);
    float tt = 0.6f / sp;
    m_glowAnimation = new AnimateSpriteFrame(myTextures, new String[] {"slime_e_F1", "slime_e_F2"}, tt, 3);
    m_walkAnimation = new AnimateSpriteFrame(myTextures, new String[] {"slime_F1", "slime_F2"}, tt/3, 1);

    m_standardDeathAnimation = new AnimateSpriteFrame(myTextures, new String[] {"slime_e_F1", "slime_e_F2",  "slime_e_F1", "slime_e_F2"}, 0.45f, 1);
    m_hitAnimation = new AnimateSpriteFrame(myTextures, new String[] {"slime_e_F1", "slime_e_F2",  "slime_e_F1", "slime_e_F2"}, 0.25f, 1);
    m_deathAnimation = m_standardDeathAnimation;
    m_vertical = v;
    maxSpeedX = 2.0f * sp;
    maxSpeedY = 2.0f * sp;
    

    if (m_vertical == 0)
      maxFallVelocity = 12.0f;

    m_horizontalDragFactor = 0.5f;
    m_climbingDragFactor = 0.25f;
    m_gravity = 0.0f;
    m_accelX = 1.0f;
    m_accelY = 1.0f;
    this.runAnimation(m_walkAnimation);
    m_gameLayer = layer;
    //m_numSounds = 2;
    //m_soundPrefix = "zombie";
    //m_playSpawnSound = true;

    m_spawnAnimation = new AnimateFadeIn(0.5f);
    m_hideOnDead = false;
    m_explodeTicks = -1;

    pLight = layer.createPointLight(new Color(0.7f,0.7f, 0.2f, 0.7f), m_lightDistance * m_scale, 0,0);
    pLight.setActive(false);
    pLight.setXray(true);

    if (m_scale > 2)
      m_destroyStuff = true;

    m_bb.width = this.getBoundingRectangle().width * 0.9f;
    m_bb.height = this.getBoundingRectangle().height * 0.9f;
    m_xOff = this.getBoundingRectangle().width * 0.05f;
    m_yOff = this.getBoundingRectangle().height * 0.05f;


  }

  @Override
  public void update(float deltaTime)
  {
    super.update(deltaTime);
    if (m_explodeTicks > 0)
    {
        m_explodeTicks--;
        if (m_explodeTicks < 1)
        {
            this.explode();
            pLight.setActive(false);
        }
    }

    pLight.setPosition(this.getX() + this.getWidth()/2, this.getY() + this.getHeight()/2);

    if (m_deathAnimation.isRunning())
    {
      pLight.setDistance(m_lightDistance);
      pLight.setActive(true);
      m_lightDistance += 20;
    } else
    {
      if (m_glow)
      {
        if (m_glowAnimation.isRunning() == false)
        {
          m_glow = false;
          pLight.setActive(false);
          this.runAnimation(m_walkAnimation);
        }
      } else
      {
        if (m_walkAnimation.isRunning() == false)
        {
          m_glow = true;
          pLight.setActive(true);
          this.runAnimation(m_glowAnimation);
        }
      }
    }


  }

  public void move()
  {
    super.move();

    if ((m_dx != 0) && m_destroyStuff)
    {
      this.destroySprites();
      this.destroyTiles();
    }
  }

  public void die()
  {
    if ((m_dying == false) && (m_alive))
    {
      this.stopAllAnimations();
      this.runAnimation(m_deathAnimation);

      AnimateScaleTo ss = null;
      if (m_vertical == 0)
      {
        this.setOrigin(this.getOriginX(), 0);
        ss = new AnimateScaleTo(0.36f,m_scale * m_currDir, m_scale, m_scale * 1.75f * m_currDir, m_scale * 1.75f);
      }
      else if (m_vertical == -1)
      {
        this.setOrigin(this.getWidth()/2, 0);
        ss = new AnimateScaleTo(0.36f,-m_scale * m_vertical * m_currDir, -m_scale, (-m_scale * m_vertical * m_currDir) * 1.75f, -m_scale * 1.75f);
      } else
      {
        this.setOrigin(this.getWidth()/2, this.getHeight());
        ss = new AnimateScaleTo(0.36f,m_scale * m_vertical * m_currDir, -m_scale, (m_scale * m_vertical * m_currDir) * 1.75f, -m_scale * 1.75f);
      }

       
      this.runAnimation(ss);
      m_dying = true;
      m_explodeTicks = 30;
      m_dx = 0;
      pLight.setActive(true);
      this.playSoundIfOnScreen("grow", 0.7f, 0.55f);
    }
  }

  public void hitByAttack()
  {
    if ((m_dying == false) && (m_alive))
    {
      if (m_pauseMoveTicks < 1)
      {
        this.stopAllAnimations();
        m_pauseMoveTicks = 16;
        this.runAnimation(m_hitAnimation);
        pLight.setActive(true);
      }
    }
  }

  public void explode()
  {
      this.setVisible(false);
      PooledEffect effect = m_gameLayer.slimeEffectPool.obtain();
      effect.setPosition(this.getX() + this.getWidth()/2, this.getY() + this.getHeight()/2);
      m_gameLayer.addParticleEffect(effect);
      this.playSoundIfOnScreen("splat", 0.99f, 0.8f);
      pLight.setActive(false);
  }

  public void turnOffSpawnSound()
  {
    m_playSpawnSound = false;
  }

  public void destroySprites()
  {
    m_bb.x = this.getBoundingRectangle().x + m_xOff;
    m_bb.y = this.getBoundingRectangle().y + m_yOff;

    for (m_iter = 0; m_iter < m_gameLayer.getChildren().size(); m_iter++)
    {
      GameDrawable s = m_gameLayer.getChildren().get(m_iter);
      if (((s instanceof TreeSprite) || (s instanceof MovingBlock) || (s instanceof SwitchSprite)) && (s.isVisible()))
      {
        if (s != this)
        {
          GameSprite ss = (GameSprite)s;
          if (Intersector.overlaps(ss.getBoundingRectangle(), m_bb))
          {
            if (s instanceof MovingBlock)
            {
              MovingBlock sss = (MovingBlock)s;
              sss.die();
            } else
            {
              ss.setVisible(false);
              this.explodeHere(ss.getX() + ss.getWidth()/2, ss.getY());
            }
            
          }
        }
      } 
    }

    for (m_iter = 0; m_iter < m_gameLayer.getChildrenBack().size(); m_iter++)
    {
      GameDrawable s = m_gameLayer.getChildrenBack().get(m_iter);
      if (((s instanceof TreeSprite) || (s instanceof MovingBlock) || (s instanceof SwitchSprite)) && (s.isVisible()))
      {
        if (s != this)
        {
          GameSprite ss = (GameSprite)s;
          if (Intersector.overlaps(ss.getBoundingRectangle(), m_bb))
          {
            if (s instanceof MovingBlock)
            {
              MovingBlock sss = (MovingBlock)s;
              sss.die();
            } else
            {
              ss.setVisible(false);
            }
            this.explodeHere(ss.getX() + ss.getWidth()/2, ss.getY());
          }
        }
      } 
    }
  }

  public void destroyTiles()
  {
    float xx = this.getX() + (this.getWidth() * m_scale)/2;
    float yy = this.getY() + (this.getHeight() * m_scale)-8;
    float xOff = 90;

    if (m_dx < 0)
    {
      xx = this.getX() - (this.getWidth()*m_scale)/2;
      xOff = -90;
    }

    int tileX = (int) Math.floor(xx/48);
    for (m_iter = 0; m_iter < 11; m_iter++)
    {
        int tileY = (int) Math.floor(yy/32);
        int c = this.getCellAt(tileX, tileY);
        if (c > 0)
        {
          m_platformTiles.setCell(tileX, tileY, null);
          explodeHere(xx+xOff,yy);
        }
        yy -= 32;
    }

  }

  public void explodeHere(float xx, float yy)
  {
      PooledEffect effect = m_gameLayer.sawEffectPool.obtain();
      effect.setPosition(xx, yy);
      m_gameLayer.addParticleEffect(effect);
      //this.playDeathSound();
      this.playSoundIfOnScreen("movingBlockDestroy", 0.8f, 0.6f);
  }

}