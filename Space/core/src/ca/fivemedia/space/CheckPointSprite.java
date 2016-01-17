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
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;

// Move quicker than zombies...but pause every 2 to 4 seconds for between 1 and 3 seconds, then either hone in X, Y
// So they seek you out.

public class CheckPointSprite extends GameSprite {

    boolean m_cameraType = false;
    float m_zoom, m_delayT, m_offsetX;
    int m_gameState = -1;
    boolean m_active = true;
    int m_specialType = -1;
    float m_offsetY;
    protected GameAnimateable m_idleAnimation, m_winAnimation;
    BombChildWeaponSprite m_bomb = null;
    MainGameLayer m_gameLayer = null;
    int m_id;

    public CheckPointSprite(TextureAtlas myTextures, float startX, float startY, int gameState, float time, int id, MainGameLayer parent) {
        super(myTextures.findRegion("clone_stand_F1"));
        m_gameState = gameState;

        float ff = MathUtils.random(0.6f, 1.3f);
        AnimateSpriteFrame stand = new AnimateSpriteFrame(myTextures, new String[] {"clone_stand_F1"}, 0.5f * ff, 1);
        AnimateSpriteFrame idle = new AnimateSpriteFrame(myTextures, new String[] {"clone_idle_F1", "clone_idle_F2"}, 0.4f, 3);
        AnimateMoveTo jmp1  = new AnimateMoveTo(0.1f, startX, startY, startX, startY + 8);
        AnimateMoveTo jmp2  = new AnimateMoveTo(0.1f, startX, startY + 8, startX, startY);
        GameAnimateable[] a = {stand, jmp1, jmp2, jmp1, jmp2, jmp1, jmp2, stand, idle, jmp1, jmp2, jmp1, jmp2,idle};
        m_idleAnimation = new GameAnimationSequence(a,-1);
        this.runAnimation(m_idleAnimation);

        GameAnimateable[] bb = {stand, jmp1, jmp2, jmp1, jmp2, jmp1, jmp2};
        m_winAnimation = new GameAnimationSequence(bb,-1);

        m_specialType = 5000;

        m_bomb = new BombChildWeaponSprite(myTextures, null, parent);
        m_bomb.setTime(time);
        m_bomb.setPartner(this);
        m_bomb.setVisible(true);
        m_bomb.setPosition(startX + 8, startY);
        m_bomb.light();
        m_gameLayer = parent;
        m_id = id;
    }

    public void warn()
    {
      m_gameLayer.m_playerHud.animateClone(m_id);
    }

    public void addBombToWorld()
    {
      m_gameLayer.add(m_bomb);
    }

    public void explode()
    {
      this.stopAllAnimations();
      this.setVisible(false);
      PooledEffect effect = m_gameLayer.bombEffectPool.obtain();
      effect.setPosition(this.getX()+this.getWidth()/2, this.getY());
      m_gameLayer.addParticleEffect(effect);
      m_active = false;
      this.playSound("blueCloneDie", 0.7f);
    }

    public void win()
    {
      if ((this.isVisible()) && (m_winAnimation.isRunning() == false))
      {
        this.stopAllAnimations();
        this.runAnimation(m_winAnimation);
      }
    }

    public CheckPointSprite(TextureAtlas myTextures, float startX, float startY, int gameState) {
        super(myTextures.findRegion("checkPoint"));
        m_gameState = gameState;
    }

    public CheckPointSprite(TextureAtlas myTextures, float startX, float startY, float zoom, float delayT, float offsetX, float offsetY, int gameState)
    {
      super(myTextures.findRegion("checkPoint"));
      m_zoom = zoom;
      m_delayT = delayT;
      m_offsetX = offsetX;
      m_offsetY = offsetY;
      m_cameraType = true;
      m_gameState = gameState;

    }

    public CheckPointSprite(TextureAtlas myTextures, float startX, float startY, String dummy, int specialType) {
        super(myTextures.findRegion("checkPoint"));
        m_specialType = specialType;
    }

    public boolean isActive()
    {
      return m_active;
    }

    public void setActive(boolean v)
    {
      m_active = v;
    }

    public boolean isCameraChangeEvent()
    {
      return m_cameraType;
    }

    public int getSpecial()
    {
      return m_specialType;
    }

    public int getGameState()
    {
      return m_gameState;
    }

    public float getZoom()
    {
      return m_zoom;
    }

    public float getZoomTime()
    {
      return m_delayT;
    }

    public float getOffsetX()
    {
      return m_offsetX;
    }

    public float getOffsetY()
    {
      return m_offsetY;
    }


    public void resetLevel()
    {
      this.setActive(true);
    }
}