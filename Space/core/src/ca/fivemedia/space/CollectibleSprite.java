package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import ca.fivemedia.gamelib.*;


public class CollectibleSprite extends GameSprite {

  GameAnimateable m_animation;
  GameAnimateable m_useAnimation, m_animation2;
  int m_type;
  boolean m_used = false;

  public  CollectibleSprite (TextureAtlas myTextures, String cType) {
    super();
    TextureRegion region = null;
    m_animation2 = null;
    if (cType.equals("OneEnergy"))
    {
      region = myTextures.findRegion("OneEnergy_F1");
      this.setRegion(region);
      
      m_type = 0;
      m_animation = new AnimateSpriteFrame(myTextures, new String[] {"OneEnergy_F1", "OneEnergy_F2", "OneEnergy_F3", "OneEnergy_F4", "OneEnergy_F5", "OneEnergy_F6"}, 0.4f, -1);
    } else if (cType.equals("OneLife"))
    {
      region = myTextures.findRegion("OneLife_F1");
      m_type = 1;
      m_animation = new AnimateSpriteFrame(myTextures, new String[] {"OneLife_F1", "OneLife_F2", "OneLife_F3", "OneLife_F4", "OneLife_F5", "OneLife_F6"}, 0.4f, -1);
    } else if (cType.equals("Gun"))
    {
      region = myTextures.findRegion("gun");
      m_type = 10;
      //this.setRegion(myTextures.findRegion("gun"));
      AnimateScaleTo grow = new AnimateScaleTo(0.5f, 1.0f, 1.0f, 1.2f, 1.2f);
      AnimateDelay delay = new AnimateDelay(0.15f);
      AnimateScaleTo shrink = new AnimateScaleTo(0.5f, 1.2f, 1.2f, 1.0f, 1.0f);
      GameAnimateable[] a = {grow,delay,shrink};
      m_animation = new GameAnimationSequence(a,-1);

    } else if (cType.equals("Hover"))
    {
      m_type = 20;
      region = myTextures.findRegion("hoverboard_F1");
      //AnimateScaleTo grow = new AnimateScaleTo(0.45f, 1.0f, 1.0f, 1.1f, 1.1f);
      //AnimateDelay delay = new AnimateDelay(0.1f);
      //AnimateScaleTo shrink = new AnimateScaleTo(0.35f, 1.1f, 1.1f, 1.0f, 1.0f);
      //GameAnimateable[] a = {grow,delay,shrink};
      m_animation = null;
    } else if (cType.equals("Coin"))
    {
      region = myTextures.findRegion("coin_F1");
      m_type = 30;
      m_animation = new AnimateSpriteFrame(myTextures, new String[] {"coin_F1", "coin_F2", "coin_F3", "coin_F4", "coin_F5", "coin_F6"}, 0.6f, -1);
      AnimateRotateTo r1 = new AnimateRotateTo(0.2f,0f,4f, 1);
      AnimateRotateTo r2 = new AnimateRotateTo(0.4f,4f,-4f, 1);
      AnimateRotateTo r3 = new AnimateRotateTo(0.2f,-4f,0f, 1);
      GameAnimateable[] a = {r1,r2,r3};
      m_animation2 = new GameAnimationSequence(a,-1);
    }

    this.setRegion(region);
    this.setSize(region.getRegionWidth(), region.getRegionHeight());

    if (cType.equals("Coin"))
    {
      this.setOrigin(this.getWidth()/2, this.getOriginY() + this.getHeight()-8);
    }
      
    if (m_type != 20)
    {
      AnimateScaleTo grow = new AnimateScaleTo(0.25f,1.0f, 1.0f, 1.5f, 1.5f);
      AnimateScaleTo shrink = new AnimateScaleTo(0.15f,1.5f, 1.5f, 0.05f, 0.05f);
      AnimateFadeOut fade = new AnimateFadeOut(0.17f);
      AnimateVisible v = new AnimateVisible(false);
      GameAnimateable[] a = {grow,shrink,fade,v};
      m_useAnimation = new GameAnimationSequence(a,1);
    } else
    {
      //AnimateFadeOut fade = new AnimateFadeOut(0.17f);
      //AnimateVisible v = new AnimateVisible(false);
      //GameAnimateable[] a = {fade,v};
      m_useAnimation = new AnimateVisible(false);
    }

    if (m_animation != null)
      this.runAnimation(m_animation);

    if (m_animation2 != null)
      this.runAnimation(m_animation2);

  }

  public boolean isCollidable()
  {
    return (this.isVisible() && (!m_used));
  }

  public void collect()
  {
    m_used = true;
    this.stopAllAnimations();
    this.runAnimation(m_useAnimation);
    if (this.getType() == 30)
      this.playSound("gotCoin", 0.7f);
    else
      this.playSound("gotSound", 0.6f);
  }

  public int getType()
  {
    return m_type;
  }

  public void resetLevel()
  {
    if (m_type != 30) //Don't reset the cons/birds you save
    {
      m_used = false;
      this.setVisible(true);
      this.stopAllAnimations();
      this.setOpacity(1.0f);
      this.setScale(1.0f,1.0f);

      if (m_animation != null)
        this.runAnimation(m_animation);

      if (m_animation2 != null)
        this.runAnimation(m_animation2);
    }
  }
}

