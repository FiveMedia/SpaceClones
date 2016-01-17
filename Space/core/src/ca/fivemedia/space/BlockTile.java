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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


public class BlockTile extends GameSprite {
  PlayerSprite m_player = null;
  GameAnimateable m_fadeIn, m_fadeOut;
  int m_state = 0;

  public BlockTile(TextureAtlas myTextures) {
    super(myTextures.findRegion("block_tile"));
  }

  public BlockTile(TextureAtlas myTextures, String frame) {
    super(myTextures.findRegion(frame));
  }

  public BlockTile(TextureAtlas myTextures, String frame, PlayerSprite player) {
    super(myTextures.findRegion(frame));
    m_player = player;
    m_fadeOut = new AnimateFadeOut(0.5f);
    AnimateDelay d = new AnimateDelay(2.0f);
    AnimateFadeIn fi = new AnimateFadeIn(1.0f);

    GameAnimateable[] a = {d,fi};
    m_fadeIn = new GameAnimationSequence(a,1);
    m_state = 0;

  }

  public boolean isCollidable()
  {
    return false;
  }

  @Override
  public void update(float deltaTime)
  {
    if (m_player == null)
      return;

    if (m_state == 0)
    {
      //solid
       if (Intersector.overlaps(this.getBoundingRectangle(), m_player.getBoundingRectangle()))
       {
        this.runAnimation(m_fadeOut);
        m_state = 1;
       }
    } else if (m_state == 1)
    {
      if (m_fadeOut.isRunning() == false)
      {
        m_state = 2;
      }
    } else if (m_state == 2)
    {
       if (!(Intersector.overlaps(this.getBoundingRectangle(), m_player.getBoundingRectangle())))
       {
          this.runAnimation(m_fadeIn);
          m_state = 3;
       }
    } else if (m_state == 3)
    {
      if (m_fadeIn.isRunning() == false)
      {
        m_state = 0;
      }
    }
  }

  @Override
  public void setVisible(boolean v)
  {
    super.setVisible(v);
  }

}

