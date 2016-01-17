package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import ca.fivemedia.gamelib.*;
import box2dLight.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;
import com.badlogic.gdx.math.*;



public class BombChildWeaponSprite extends GameSprite implements WeaponInterface {

  AnimateSpriteFrame m_animation, m_explodeAnimation;
  GameAnimateable m_fadeOut;
  PlayerSprite m_playerSprite;
  float m_xOff, m_yOff, m_currDir;
  int m_attackTicks, m_attackState;
  float m_stateTime;
  float m_dx = 0;
  PointLight pLight;
  TiledMapTileLayer tiles;
  MainGameLayer m_gameLayer;
  Circle m_circle;
  float m_time = 1.2f;
  CheckPointSprite m_partner = null;
  boolean m_warn = false;
  long m_fuseSoundId = -1;

  public  BombChildWeaponSprite (TextureAtlas myTextures, PlayerSprite player, MainGameLayer layer) {
    super(myTextures.findRegion("bomb_F1"));
    m_animation = new AnimateSpriteFrame(myTextures, new String[] {"bomb_F1", "bomb_F2"}, 0.4f, -1);
    m_explodeAnimation = new AnimateSpriteFrame(myTextures, new String[] {"bomb_explode_F2", "bomb_explode_F1"}, 0.1f, 2);
  
    m_circle = new Circle(0,0,this.getWidth() * 2.5f);
    m_playerSprite = player;
    m_xOff = 0;
    m_yOff = 0;
    m_attackTicks = 0;
    m_attackState = 0;
    this.setVisible(false);
    m_currDir = 1;
    pLight = layer.createPointLight(new Color(0.7f,0.2f, 0f, 0.7f), 150, 0,0);
    pLight.setActive(false);
    pLight.setXray(true);
    m_fadeOut = new AnimateFadeOut(1.0f);
    m_gameLayer = layer;

    tiles = (TiledMapTileLayer) layer.tiledMap.m_tiledMap.getLayers().get("platforms");
  }

  public void setTime(float tf)
  {
    m_time = tf;
  }

  public void setPartner(CheckPointSprite cs)
  {
    m_partner = cs;
  }

  public Circle getBoundingCircle()
  {
    m_circle.x = this.getX() + this.getOriginX();
    m_circle.y = this.getY() + this.getOriginY();
    return m_circle;
  }

  @Override
  public void update(float deltaTime)
  {
    if (m_gameLayer.gameState != 10)
      return;

    if (m_attackState == 3)
    {
      m_stateTime += deltaTime;
      if (m_stateTime >= m_time)
      {
        pLight.setDistance(150);
        m_attackState = 2;
        pLight.setDistance(80);
        m_stateTime = 0;

        if (m_partner != null)
          m_partner.explode();

        this.explodeCells();
        this.stopAllAnimations();
        this.runAnimation(m_explodeAnimation);
      } else
      {
        if (m_stateTime >= (m_time - 3))
        {
          if (m_warn == false)
          {
            m_warn = true;
            if (m_partner != null)
              m_partner.warn();
          }
        }
      }

      return;
    } else if (m_attackState == 2)
    {
      if (m_explodeAnimation.isRunning() == false)
      {
          this.setVisible(false);
          m_attackState = 0;
          pLight.setActive(false);
      }
    }

    if (this.isVisible() == false)
      return;

    if (m_attackState == 0)
      return;

  }

  public boolean didCollide(GameLayer layer, BaseSprite sprite)
  {
    if (sprite.isCircle() == false)
    {
      return  Intersector.overlaps(this.getBoundingCircle(), sprite.getBoundingRectangle());
    }

    return Intersector.overlaps(sprite.getBoundingCircle(), this.getBoundingCircle());
  }

  public void attack()
  {
    this.stopAllAnimations();
    this.runAnimation(m_animation);
    this.setVisible(true);
    this.setOpacity(1.0f);
    pLight.setActive(true);
    this.playSound("laser", 0.65f);
    m_fuseSoundId = this.loopSound("bombFuse", 0.8f);

    m_currDir = m_playerSprite.getDirection();
    m_attackTicks = 5;
    m_attackState = 3;
    if (m_currDir > 0)
    {
        m_xOff = 70;
    } else
    {
        m_xOff = 0;
    }

    m_yOff = 0;
    m_dx = 0;

    this.setPosition(m_playerSprite.getX() + m_playerSprite.getWidth()/2 - this.getWidth()/2, m_playerSprite.getY() + m_yOff);
    pLight.setPosition(this.getX() + 12, this.getY()+8);
    pLight.setDistance(50);
    pLight.setActive(true);

  }

  public void light()
  { 
    this.stopAllAnimations();
    this.runAnimation(m_animation);
    this.setVisible(true);
    this.setOpacity(1.0f);
    pLight.setPosition(this.getX() + 12, this.getY()+8);
    pLight.setDistance(50);
    pLight.setActive(true);
    m_attackState = 3;
  }

  public void handleCollision(GameLayer layer, BaseSprite sprite)
  { 
    //hit an enemy
    if ((m_attackState == 2) && (m_stateTime < 0.25))
    {
      sprite.hitByAttack();
    }
  }

  private void explode(int tx,int ty)
  {
    if (m_fuseSoundId >= 0)
      this.stopSound("bombFuse", m_fuseSoundId);

    m_fuseSoundId = -1;

    PooledEffect effect = m_gameLayer.ufoEffectPool.obtain();
    effect.setPosition(tx * 48 + 20, ty * 32 + 16);
    m_gameLayer.addParticleEffect(effect);
    this.playSound("explode", 0.6f);
    this.stopAllAnimations();

  }

  public void explodeCells(int tileX, int tileY)
  {
    //int i = 0;
    for (int cy = 0; cy < 5; cy++)
    {
      int ty = tileY + cy - 2;
      for (int cx=0; cx < 3; cx++)
      {
          int tx = tileX + cx - 1;
          TiledMapTileLayer.Cell c = tiles.getCell(tx,ty);
          if (c != null)
          {
            int id = c.getTile().getId();
            if ((id == 2) || (id == 10))
            {
              tiles.setCell(tx,ty, null);
              this.explode(tx,ty);
            }
            else if ((id == 3))
            {
              //chain reaction
              tiles.setCell(tx,ty, null);
              explodeCells(tx,ty);
              this.explode(tx,ty);
            }
          }
          //i++;
      }
    }
  }

  public void explodeCells()
  {

      float tw = 48;
      float th = 32;

      float xx = this.getX() + this.getWidth()/2;
      float yy = this.getY() - th;

      int tileX = (int) Math.floor(xx/tw);
      int tileY = (int) Math.floor(yy/th);

      this.explode(tileX,tileY+1);

    //int i = 0;
    for (int cy = 0; cy < 5; cy++)
    {
      int ty = tileY + cy - 2;
      for (int cx=0; cx < 3; cx++)
      {
          int tx = tileX + cx - 1;
          TiledMapTileLayer.Cell c = tiles.getCell(tx,ty);
          if (c != null)
          {
            int id = c.getTile().getId();
            if ((id == 2) || (id == 10))
            {
              tiles.setCell(tx,ty, null);
              this.explode(tx,ty);
            }
            else if ((id == 3))
            {
              //chain reaction
              tiles.setCell(tx,ty, null);
              this.explode(tx,ty);
              explodeCells(tx,ty);
            }
          } 
          //i++;
      }
    }
  }

  public boolean isActive()
  {
    if (m_attackState > 0)
      return true;

    return false;
  }

  public boolean stopMovingWhenAttacking()
  {
    return false;
  }

  public int getPauseTicks()
  {
    return 1;
  }

  public int getAttackTicks()
  {
    return 20;
  }
}