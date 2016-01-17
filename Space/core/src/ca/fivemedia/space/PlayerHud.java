package ca.fivemedia.space;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import ca.fivemedia.gamelib.*;
import java.text.*;

public class PlayerHud extends GamePanel {

  protected int m_energy = 10;
  protected int m_lives;
  protected GameText m_livesLabel;
  GameAnimateable m_lastEnergyAnimation1 = null;
  GameAnimateable m_lastEnergyAnimation2 = null;
  GameAnimateable m_lastEnergyAnimation3 = null;
  GameAnimationSequence[] m_cloneWarnAnimation = new GameAnimationSequence[3];
  protected GameSprite[] m_clones;
  protected TextureRegion m_cloneEmptyRegion, m_cloneFullRegion;
  int m_cloneCount = 3;
  DecimalFormat m_df = new DecimalFormat("0.00");
  String m_timeString = null;
  GameText m_timer;
  GameSprite m_timerBack;
  int m_lastEnergy = 0;


  public  PlayerHud (BitmapFont font, int lives, TextureAtlas textures) 
  {
    super();
    GameSprite back = new GameSprite(textures.findRegion("player_hud"));
    this.add(back);
    back.setOpacity(0.6f);
    m_livesLabel = new GameText(font,38f);
    this.add(m_livesLabel);


    //m_df.setRoundingMode(RoundingMode.FLOOR);

    GameSprite energyBar = new GameSprite(textures.findRegion("energy_bar"));
    this.add(energyBar);
    energyBar.setOpacity(0.6f);

    energyBar = new GameSprite(textures.findRegion("energy_bar"));
    this.add(energyBar);
    energyBar.setOpacity(0.6f);

    energyBar = new GameSprite(textures.findRegion("energy_bar"));
    this.add(energyBar);
    energyBar.setOpacity(0.6f);

    energyBar = new GameSprite(textures.findRegion("energy_bar"));
    this.add(energyBar);
    energyBar.setOpacity(0.6f);

    energyBar = new GameSprite(textures.findRegion("energy_bar"));
    this.add(energyBar);
    energyBar.setOpacity(0.6f);

    energyBar = new GameSprite(textures.findRegion("energy_bar"));
    this.add(energyBar);
    energyBar.setOpacity(0.6f);

    energyBar = new GameSprite(textures.findRegion("energy_bar"));
    this.add(energyBar);
    energyBar.setOpacity(0.6f);

    energyBar = new GameSprite(textures.findRegion("energy_bar"));
    this.add(energyBar);
    energyBar.setOpacity(0.6f);

    energyBar = new GameSprite(textures.findRegion("energy_bar"));
    this.add(energyBar);
    energyBar.setOpacity(0.6f);

    energyBar = new GameSprite(textures.findRegion("energy_bar"));
    this.add(energyBar);
    energyBar.setOpacity(0.6f);

    energyBar = new GameSprite(textures.findRegion("energy_bar_last"));
    this.add(energyBar);
    energyBar.setVisible(false);
    energyBar.setOpacity(0.6f);

    energyBar = new GameSprite(textures.findRegion("energy_bar_last"));
    this.add(energyBar);
    energyBar.setVisible(false);
    energyBar.setOpacity(0.6f);

    energyBar = new GameSprite(textures.findRegion("energy_bar_last"));
    this.add(energyBar);
    energyBar.setVisible(false);
    energyBar.setOpacity(0.6f);

    this.setLives(lives);
    this.setEnergy(10);

    AnimateColorTo f1 = new AnimateColorTo(0.395f, 0.3f,0.3f,0.3f, 1, 1, 1);
    AnimateDelay d1 = new AnimateDelay(0.2f);
    AnimateColorTo f2 = new AnimateColorTo(0.1f, 1,1,1, 0.3f,0.3f,0.3f);
    AnimateDelay d2 = new AnimateDelay(0.2f);
    GameAnimateable[] b = {f2,d2,f1,d1};
    m_lastEnergyAnimation1 = new GameAnimationSequence(b,-1);

    f1 = new AnimateColorTo(0.395f, 0.3f,0.3f,0.3f, 1, 1, 1);
    d1 = new AnimateDelay(0.2f);
    f2 = new AnimateColorTo(0.1f, 1,1,1, 0.3f,0.3f,0.3f);
    d2 = new AnimateDelay(0.2f);
    GameAnimateable[] b2 = {f2,d2,f1,d1};
    m_lastEnergyAnimation2 = new GameAnimationSequence(b2,-1);

    f1 = new AnimateColorTo(0.395f, 0.3f,0.3f,0.3f, 1, 1, 1);
    d1 = new AnimateDelay(0.2f);
    f2 = new AnimateColorTo(0.1f, 1,1,1, 0.3f,0.3f,0.3f);
    d2 = new AnimateDelay(0.2f);
    GameAnimateable[] b3 = {f2,d2,f1,d1};
    m_lastEnergyAnimation3 = new GameAnimationSequence(b3,-1);

    m_clones = new GameSprite[3];
    m_cloneFullRegion = textures.findRegion("bird_full");
    m_cloneEmptyRegion = textures.findRegion("clone_blownup");

    for (int i =0; i < 3; i++)
    {
        m_clones[i] = new GameSprite(textures.findRegion("bird_full"));
        this.add(m_clones[i]);
        m_clones[i].setOpacity(0.8f);
        m_clones[i].setColor(1,1,1,0.8f);
    }

    AnimateColorTo c = new AnimateColorTo(0.5f, 1,1,1,1,0.5f,0.5f,6);
    AnimateColorTo c2 = new AnimateColorTo(0.1f, 1,1,1,1,1,1,1);
    AnimateSpriteFrame sf = new AnimateSpriteFrame(textures, new String[] {"clone_blownup"}, 0.1f,1);
    GameAnimateable[] b4 = {c,c2,sf};
    m_cloneWarnAnimation[0] = new GameAnimationSequence(b4,1);

    c = new AnimateColorTo(0.5f, 1,1,1,1,0.5f,0.5f,6);
    c2 = new AnimateColorTo(0.1f, 1,1,1,1,1,1,1);
    sf = new AnimateSpriteFrame(textures, new String[] {"clone_blownup"}, 0.1f,1);
    GameAnimateable[] b5 = {c,c2,sf};
    m_cloneWarnAnimation[1] = new GameAnimationSequence(b5,1);

    c = new AnimateColorTo(0.5f, 1,1,1,1,0.5f,0.5f,6);
    c2 = new AnimateColorTo(0.1f, 1,1,1,1,1,1,1);
    sf = new AnimateSpriteFrame(textures, new String[] {"clone_blownup"}, 0.1f,1);
    GameAnimateable[] b6 = {c,c2,sf};
    m_cloneWarnAnimation[2] = new GameAnimationSequence(b6,1);

    m_timerBack = new GameSprite(textures.findRegion("time_back"));
    this.add(m_timerBack);
    m_timerBack.setOpacity(0.6f);
    
    m_timer = new GameText(font,153);
    this.add(m_timer);
    m_timer.setText("0.0");

  }

  public void setLives(int lives)
  {
    m_lives = lives;
    m_livesLabel.setText("" + m_lives);
  }

  public void setEnergy(int energy)
  {
    if (energy < m_lastEnergy)
    {
      this.playSound("airDeplete", 0.85f);
    }

    m_lastEnergy = m_energy;
    m_energy = energy;
    GameDrawable child = m_children.get(12);
    child.setVisible(false);
    child = m_children.get(13);
    child.setVisible(false);
    child = m_children.get(14);
    child.setVisible(false);

    for (int i=2; i < 12; i++)
    {
      child = m_children.get(i);
      if (m_energy >= (i-1))
      {
        child.setVisible(true);
      } else
      {
        child.setVisible(false);
      }
    }

    if (m_energy == 3)
    {
      child = m_children.get(12);
      child.setVisible(true);
      if (m_lastEnergyAnimation1.isRunning() == false)
        child.runAnimation(m_lastEnergyAnimation1);
      child = m_children.get(13);
      child.setVisible(true);
      if (m_lastEnergyAnimation2.isRunning() == false)
        child.runAnimation(m_lastEnergyAnimation2);
      child = m_children.get(14);
      child.setVisible(true);
      if (m_lastEnergyAnimation3.isRunning() == false)
        child.runAnimation(m_lastEnergyAnimation3);

    } else if (m_energy == 2)
    {
      child = m_children.get(12);
      child.setVisible(true);
      if (m_lastEnergyAnimation1.isRunning() == false)
        child.runAnimation(m_lastEnergyAnimation1);
      child = m_children.get(13);
      child.setVisible(true);
      if (m_lastEnergyAnimation2.isRunning() == false)
        child.runAnimation(m_lastEnergyAnimation2);
      child = m_children.get(14);
      child.stopAllAnimations();
    } else if (m_energy == 1)
    {
      child = m_children.get(12);
      child.setVisible(true);
      if (m_lastEnergyAnimation1.isRunning() == false)
        child.runAnimation(m_lastEnergyAnimation1);
      child = m_children.get(13);
      child.stopAllAnimations();
      child = m_children.get(14);
      child.stopAllAnimations();
    } else
    {
      child = m_children.get(12);
      child.stopAllAnimations();
      child = m_children.get(13);
      child.stopAllAnimations();
      child = m_children.get(14);
      child.stopAllAnimations();
    }
  }

  public void setClones(int c)
  {
    m_cloneCount = c;

    for (int i = 0; i < 3; i++)
    {
      GameSprite g = m_clones[i];
      g.stopAllAnimations();
      if (i < m_cloneCount)
      { 
        g.setRegion(m_cloneFullRegion);
        g.stopAllAnimations();
        g.setColor(1,1,1,g.getOpacity());
      } else
      {
        g.setRegion(m_cloneEmptyRegion);
        g.stopAllAnimations();
        g.setColor(1,1,1,g.getOpacity());
      }
    }
  }

  public void animateClone(int c)
  {
    GameSprite g = m_clones[c];
    g.runAnimation(m_cloneWarnAnimation[c]);
    this.playSound("countdown", 0.5f);
  }

  public void setTime(float ts)
  {
    m_timer.setText(m_df.format(ts));
  }

  public void setPosition(float x, float y)
  {
    super.setPosition(x,y);
    GameDrawable child = m_children.get(0);
    child.setPosition(x,y); //hud back

    child = m_children.get(1);
    child.setPosition(x+5,y+38); // lives label
    
    x += 47;
    float xt = x;
    for (int i = 2; i < 12; i++)  // each energy bar
    {
      child = m_children.get(i);
      xt = x + ((i-2) * 33);
      child.setPosition(xt, y+8);
      if (i < 5)
      {
        child = m_children.get(i+10);
        child.setPosition(xt, y+8);
      }
    } 

    for (int i=0;i < 3;i++)
    {
      m_clones[i].setPosition(xt + 44 + (i*(m_clones[i].getWidth()-8)), y+2);
    }

    m_timerBack.setPosition(x + 1010, y);
    m_timer.setPosition(x + 1010, y + 38);
  }
}
