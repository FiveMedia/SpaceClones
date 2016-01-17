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

public class CoinHud extends GamePanel {

  protected GameText m_numCoinsText;
  protected GameText m_percentComplete;
  protected GameSprite m_coin;
  int m_numCoins = 0;
  int m_targetCoins = 0;
  float m_stateTime = 0;
  int m_state = 0;
  private GameAnimateable m_coinCollectAnimation;

  public  CoinHud (BitmapFont font, TextureAtlas textures) 
  {
    super();
    m_coin = new GameSprite(textures.findRegion("bird_full"));
    this.add(m_coin);
    m_numCoinsText= new GameText(font,64f);
    this.add(m_numCoinsText);

    m_percentComplete= new GameText(font,64f);
    this.add(m_percentComplete);
  
    AnimateScaleTo g = new AnimateScaleTo(0.2f, 1.0f, 1.0f, 1.3f, 1.3f);
    AnimateDelay d = new AnimateDelay(0.1f);
    AnimatePlaySound p = new AnimatePlaySound("countCoin", 0.6f);
    AnimateScaleTo s = new AnimateScaleTo(0.1f, 1.3f, 1.3f, 1.0f, 1.0f);
    AnimateDelay d2 = new AnimateDelay(0.35f);
    GameAnimateable[] a = {g,d,p,s,d2};
    m_coinCollectAnimation = new GameAnimationSequence(a,1);

  }

  @Override
  public void update(float deltaTime)
  {
    super.update(deltaTime);
    if(m_state > 0)
    {
      m_stateTime += deltaTime;
      if (m_state == 1)
      {
        //Gdx.app.debug("CoinHud","STATE = 1");
        m_coin.runAnimation(m_coinCollectAnimation);
        m_stateTime = 0;
        m_state = 2;
      } else if (m_state == 2)
      {
        if (m_stateTime > 0.3f)
        {
          //Gdx.app.debug("CoinHud","STATE = 2");
          m_numCoins++;
          m_numCoinsText.setText("" + m_numCoins);
          m_state = 3;
          m_stateTime = 0;
          //Gdx.app.debug("CoinHud","numCoins now equals " + m_numCoins + " target is" + m_targetCoins);
        }
      } else if (m_state == 3)
      {
        //Gdx.app.debug("CoinHud","STATE = 3");
        if (!(m_coinCollectAnimation.isRunning()))
        {
          //Gdx.app.debug("CoinHud","STATE 3 TRANSITION");
          if (m_numCoins < m_targetCoins)
          {
            m_stateTime = 0;
            m_state = 1;
            //Gdx.app.debug("CoinHud","REPEAT");
          } else
          {
            m_state = 0;
          }
        }
      }
    }
  }

  public boolean isAnimating()
  {
    if (m_state > 0)
      return true;

    return false;
  }

  public void setCoins(int coins)
  {
    m_numCoins = coins;
    m_numCoinsText.setText("" + coins);
  }

  public void setPercentComplete(int c)
  {
    m_percentComplete.setText("" + c + " %");
  }

  public void addCoins(int sCoins, int fCoins)
  {

    setCoins(sCoins);
    //m_numCoinsText.setText("" + coins);
    //m_coin.runAnimation(m_coinCollectAnimation);
    //this.playSound("gotSound",0.5f);
    m_state = 1;
    m_stateTime = 0;
    m_targetCoins = fCoins;

  }

  public void setPosition(float x, float y)
  {
    super.setPosition(x,y);
    //m_coin.setPosition(x + 65,y);
    //m_numCoinsText.setPosition(x,y+40);
    //m_percentComplete.setPosition(x - 160,y+40);

    m_coin.setPosition(x,y);
    m_numCoinsText.setPosition(x-60,y+40);
    m_percentComplete.setPosition(x + 70,y+40);
  }
}
