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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class MovingBlock extends BaseSprite implements SwitchChangedListener {

  boolean m_movingSoundPlaying = false;
  long m_soundId = 0;
  float m_sawVolume = 0.05f;
  MainGameLayer m_gameLayer = null;
  GameAnimateable m_spawnSpecialAnimation = null;
  float m_scale = 1.0f;
  int m_startState = 0;
  int m_switchState = 1;
  boolean m_switchAttached = false;
  boolean m_perm = false;
  TiledMapTile m_topTile, m_leftTile, m_rightTile, m_bottomTile;
  boolean m_destroyStuff = false;
  int m_iter  = 0;
  Rectangle m_bb = new Rectangle();
  float m_xOff, m_yOff;

  public MovingBlock(TextureAtlas myTextures, TiledMapTileLayer pTiles, TiledMapTileLayer lTiles, float scale, String move, float speed, MainGameLayer layer, float pt, float pb) {
    super(myTextures.findRegion("moving_block"),pTiles,lTiles);
    if (pb < 0)
    {
      //flat top/special
      if (layer.m_stage == 2)
        this.setRegion(myTextures.findRegion("moving_block_b_r"));
      else
        this.setRegion(myTextures.findRegion("moving_block_b"));

      m_perm = true;

      TiledMap tm = layer.tiledMap.m_tiledMap;
      TiledMapTileSets ts = tm.getTileSets();
      m_topTile = ts.getTile(1);
      m_leftTile = ts.getTile(6);
      m_rightTile = ts.getTile(5);
      m_bottomTile = ts.getTile(7);
    } else
    {
      if (layer.m_stage == 2)
        this.setRegion(myTextures.findRegion("moving_block_r"));
    }

    m_deathSoundVolume = 0.55f;
    m_soundPrefix = "movingBlock";
    m_numSounds = 0;
    m_destroyStuff = false;
    m_hideOnDead = true;
    m_scale = scale;

    if (move.equals("Static"))
    {
      m_moveController = new StaticMoveController(0f);
    }
    else if (move.equals("Vertical"))
    {
      boolean shake = false;

      if (pb >= 0)
        shake = true;

       m_moveController = new FallMoveController(4.0f * speed, true, true, pt, pb, shake);
    } else if (move.equals("Horizontal"))
    {
      m_moveController = new SingleDirectionMoveController(4.0f * speed, true,0);
      if (m_scale > 1.5f)
      {
        m_destroyStuff = true;
      }
    }

    //m_walkAnimation = new AnimateSpriteFrame(myTextures, new String[] {"GreenCloud_F1"}, 1.0f, -1);
    //this.runAnimation(m_walkAnimation);
    m_numSounds = 0;
    //m_soundPrefix = "saw";

    m_walkAnimation = new AnimateSpriteFrame(myTextures, new String[] {"moving_block"}, 1.0f, -1);
  
    this.setScale(scale);
    m_sawVolume = 0.04f * scale + 0.08f;

    m_gameLayer = layer;

    m_standardDeathAnimation = new AnimateFadeOut(0.2f);
    m_deathAnimation = m_standardDeathAnimation;

    m_bb.width = this.getBoundingRectangle().width * 0.9f;
    m_bb.height = this.getBoundingRectangle().height * 0.9f;
    m_xOff = this.getBoundingRectangle().width * 0.05f;
    m_yOff = this.getBoundingRectangle().height * 0.05f;

  }

  public void die()
  {
    if (m_dying)
      return;

    super.die();
    m_dying = true;
    if (m_movingSoundPlaying)
    {
      this.stopSound("movingBlock1", m_soundId);
      m_movingSoundPlaying = false;
      m_soundId = 0;
    }

    if (m_scale < 2)
    {

    PooledEffect effect = m_gameLayer.sawEffectPool.obtain();
    effect.setPosition(this.getX()+this.getWidth()/2, this.getY());
    m_gameLayer.addParticleEffect(effect);
    this.playDeathSound();
    } else
    {
      this.bigBlockExplode();
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

  public void move()
  {
    if (m_pause)
      return;

    if (((m_startState == 0) && (m_switchState == 1)) || ((m_startState == 1) && (m_switchState == 0)))
    {
      m_moveController.move(this, m_internalPlayer, m_platformTiles, m_climbableTiles);
      if ((m_moveController.isMotionActive() == false) && (m_switchAttached))
      {
          //Gdx.app.debug("SingleDirMoveController", "MOVE STOPPED - toggling start state");
          m_startState++;
          if (m_startState > 1)
            m_startState = 0;
      }

      if (m_moveController.isDone())
      {
          float xx = this.getX();
          float bottomY = this.getY();

          int tileX = (int) Math.floor(xx/tw);
          int tileY = (int) Math.floor(bottomY/th);
          
          TiledMapTileLayer.Cell cc = new TiledMapTileLayer.Cell();
          cc.setTile(m_bottomTile);
          m_platformTiles.setCell(tileX, tileY, cc);

          cc = new TiledMapTileLayer.Cell();
          cc.setTile(m_bottomTile);
          m_platformTiles.setCell(tileX+1, tileY, cc);

          cc = new TiledMapTileLayer.Cell();
          cc.setTile(m_bottomTile);
          m_platformTiles.setCell(tileX+2, tileY, cc);

          cc = new TiledMapTileLayer.Cell();
          cc.setTile(m_leftTile);
          m_platformTiles.setCell(tileX, tileY+1, cc);

          cc = new TiledMapTileLayer.Cell();
          cc.setTile(m_topTile);
          m_platformTiles.setCell(tileX+1, tileY+1, cc);

          cc = new TiledMapTileLayer.Cell();
          cc.setTile(m_rightTile);
          m_platformTiles.setCell(tileX+2, tileY+1, cc);

          cc = new TiledMapTileLayer.Cell();
          cc.setTile(m_topTile);
          m_platformTiles.setCell(tileX, tileY+2, cc);

          cc = new TiledMapTileLayer.Cell();
          cc.setTile(m_topTile);
          m_platformTiles.setCell(tileX+1, tileY+2, cc);

          cc = new TiledMapTileLayer.Cell();
          cc.setTile(m_topTile);
          m_platformTiles.setCell(tileX+2, tileY+2, cc);

          this.setVisible(false);

      }
    } else
    {
      m_dx = 0;
      m_dy = 0;
    }

    if (m_movingSoundPlaying == false)
    {
      if (m_parent.isOnScreen(this.getX(), this.getY()))
      {
        if ((m_soundId == 0) && (m_dx != 0))
        {
          m_soundId = loopSoundManageVolume("movingBlock1", this, m_internalPlayer, 0.85f,0.55f);
          m_movingSoundPlaying = true;
        }
      }
    } else if ((m_parent.isOnScreen(this.getX(), this.getY()) == false) || (m_dx == 0))
    {
      if (m_soundId >= 0)
      {
        this.stopSound("movingBlock1", m_soundId);
        m_movingSoundPlaying = false;
        m_soundId = 0;
      }
    }

    if ((m_dx != 0) && m_destroyStuff)
    {
      this.destroySprites();
      this.destroyTiles();
    }
  }

  @Override
  public void hitPlayer(PlayerSprite player)
  {

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
    float xx = this.getX() + this.getWidth() + 116;
    float yy = this.getY() + this.getHeight() + 64;
    float xOff = 90;

    if (m_dx < 0)
    {
      xx = this.getX() - 116;
      xOff = -90;
    }

    int tileX = (int) Math.floor(xx/48);
    for (m_iter = 0; m_iter < 9; m_iter++)
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

  public void bigBlockExplode()
  {
    float xx = this.getX() - 120;
    float yy = this.getY() - 80;
    float xOff = 16;
    if (m_dx < 0)
      xOff = -16;

    for (int xi = 0; xi < 9; xi++)
    {
      for (int yi = 0; yi < 9; yi++)
      {
         explodeHere(xx+xOff + xi * 48,yy + yi * 32);
      }
    }

  }

  @Override
  public void resetLevel()
  {
    if (m_wasSpawned)
    {
      m_dx = 0;
      m_dy = 0;
      this.stopAllAnimations();
      this.setOpacity(1);
      this.setVisible(false);
      m_dying = false;
      m_alive = false;
      m_spawning = false;
      m_pauseMoveTicks = 0;
      m_moveController.reset();
    } else if (m_startX >= 0)
    {
      this.setPosition(m_startX, m_startY);
      m_dx = 0;
      m_dy = 0;
      m_moveController.reset();
      m_pauseMoveTicks = 0;
    }

    if (m_soundId != 0)
      this.stopSound("movingBlock1", m_soundId);
    else
      this.stopSound("movingBlock1");

    m_movingSoundPlaying = false;
    m_soundId = 0;

    this.setScale(m_scale);
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

  @Override
  public void hitByBlock()
  {

    this.stopAllAnimations();
    m_deathAnimation = m_flattenAnimation;
    this.setOrigin(this.getOriginX(), 0);
    this.runAnimation(m_deathAnimation);
    m_dying = true;
    this.playSound("plasmaCubeCrush", 0.4f);
  }

  public void setFastSpawn(boolean soundOn)
  {
    m_spawnAnimation = new AnimateFadeIn(0.05f);

    float max = 0.7f;
    float min = 0.45f;

    if (soundOn)
    {
      max = 0.35f;
      min = 0.15f;
    }

    AnimateDelay d = new AnimateDelay(0.2f);
    AnimatePlaySound fi = new AnimatePlaySound("fogVertical", m_internalPlayer, max, min, soundOn);
    GameAnimateable[] a = {d,fi};
    m_spawnSpecialAnimation = new GameAnimationSequence(a,1);
  }

  public void setSwitch(SwitchSprite s, int startState)
  {
    m_startState = startState;
    s.addLight(this);
    m_switchState = s.getState();
    m_switchAttached = true;
    //Gdx.app.debug("SawSprite", "Switch attached.");
  }

  public void trigger()
  {
    if (m_moveController != null)
        m_moveController.triggerMotion();

  }

  public void spawn()
  {

    if (m_moveController != null)
      m_moveController.reset();

    m_wasSpawned = true;
    m_pauseMoveTicks = 0;

    this.setOrigin(this.getOriginX(), m_originalOriginY);

    m_deathAnimation = m_standardDeathAnimation;
    if (m_walkAnimation.isRunning() == false)
      this.runAnimation(m_walkAnimation);

    this.setVisible(true);
    m_alive = true;
    m_dying = false;
    m_spawning = true;

    this.runAnimation(m_spawnAnimation);
    m_dx = 0;
    m_dy = 0;
    //Gdx.app.debug("SawSprite", "Spawning");

    this.setScale(m_scale);

    if (m_spawnSpecialAnimation != null)
    {
      this.runAnimation(m_spawnSpecialAnimation);
    }
  }

  public void switchStateChanged(int state, boolean animate)
  {
    m_switchState = state;
  }

  public void endLevel(float mx, float my)
  {
    super.endLevel(mx, my);
    this.stopSound("movingBlock1");
  }

}