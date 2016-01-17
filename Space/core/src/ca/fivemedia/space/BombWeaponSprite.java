package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import ca.fivemedia.gamelib.*;
import java.util.Iterator;
import java.util.ArrayList;


public class BombWeaponSprite extends GameSprite implements WeaponInterface {

  AnimateSpriteFrame m_animation;
  PlayerSprite m_playerSprite;
  float m_xOff, m_yOff, m_currDir;
  int m_attackTicks, m_attackState;
  ArrayList<BombChildWeaponSprite> m_bullets;

  public BombWeaponSprite (TextureAtlas myTextures, PlayerSprite player, GameContainer parent) {
    super(myTextures.findRegion("blank"));
    m_animation = new AnimateSpriteFrame(myTextures, new String[] {"man_bomb_F1"}, 0.1666f, 1);
    m_bullets = new ArrayList<BombChildWeaponSprite>();

    for (int i = 0; i < 10; i++)
    {
      BombChildWeaponSprite b = new BombChildWeaponSprite(myTextures, player, (MainGameLayer) parent);
      m_bullets.add(b);
      //parent.add(b);
      b.setVisible(false);
    }

    m_playerSprite = player;
    m_xOff = 0;
    m_yOff = 0;
    m_attackTicks = 0;
    m_attackState = 0;
    this.setVisible(false);
    m_currDir = 1;
  }

  public void addAll(MainGameLayer layer)
  {
    for (BombChildWeaponSprite b : m_bullets)
    {
      layer.add(b);
    }
  }

  @Override
  public void update(float deltaTime)
  {

  }

  public boolean didCollide(GameLayer layer, BaseSprite sprite)
  {
    boolean col = false;
    for (BombChildWeaponSprite b : m_bullets)
    {
      if (b.isVisible())
      {
        col = b.didCollide(layer, sprite);
        if (col)
        {
          b.handleCollision(layer, sprite);
          break;
        }
      }
    }
    return col;
  }

  public void attack()
  {

    m_playerSprite.stopAllAnimations();
    m_playerSprite.runAnimation(m_animation);

    for (BombChildWeaponSprite b : m_bullets)
    {
      if (b.isVisible() == false)
      {
        b.attack();
        return;
      }
    }

  }

  public void handleCollision(GameLayer layer, BaseSprite sprite)
  {
    //already dealt with in gun's case
  }

  public boolean isActive()
  {
    return true;
  }

  public boolean stopMovingWhenAttacking()
  {
    return true;
  }

  public int getPauseTicks()
  {
    return 0;
  }

  public int getAttackTicks()
  {
    return 10;
  }
}