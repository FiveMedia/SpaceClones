package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import ca.fivemedia.gamelib.*;


public class TrampolineSprite extends GameSprite {

  AnimateSpriteFrame m_animation;
  int m_ignoreTicks = 0;
  PlayerSprite m_player;
  float deltaX = 0;
  float deltaY = 1.1f;

  public  TrampolineSprite (TextureAtlas myTextures, PlayerSprite player, String tDir) {
    super(myTextures.findRegion(tDir+"_F1"));
    m_animation = new AnimateSpriteFrame(myTextures, new String[] {tDir+"_F2", tDir+"_F1"}, 0.25f, 1);
    m_player = player;
    if (tDir.equals("trampL"))
    {
      deltaX = -0.8f;
      deltaY = 0.85f;
    } else if (tDir.equals("trampR"))
    {
      deltaX = 0.8f;
      deltaY = 0.85f;
    }
  }

  public void update(float deltaTime)
  {
    if (m_ignoreTicks > 0)
      m_ignoreTicks--;
  }

  public boolean bounce()
  {
    if (m_ignoreTicks < 1)
    {
      m_ignoreTicks = 10;
      this.stopAllAnimations();
      this.runAnimation(m_animation);
      this.playSound("bounce",0.4f);
      m_player.bounce(deltaX, deltaY);
      return true;
    }
    return false;
  }
}

