package ca.fivemedia.space;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.math.Matrix4;
import ca.fivemedia.gamelib.*;

public class SplashScreen extends GameLayer {
  Matrix4 m_defaultMatrix = new Matrix4();
  GameSprite m_five, m_village,m_and, m_present;
  static TextureAtlas m_texture = null;

  private int m_state = 0;
  private float m_stateTime = 0;
  GameAnimateable seq4 = null;
  boolean m_soundPlaying = false;

  public SplashScreen() {
    //Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);

    if (m_texture == null)
      m_texture = new TextureAtlas("splash_atlas.txt");
    else
    {
      m_texture.dispose();
      m_texture = new TextureAtlas("splash_atlas.txt");
    }

    m_five = new GameSprite(m_texture.findRegion("fiveMediaLogo"));
    m_five.setPosition(265,440);
    this.add(m_five);
    m_five.setOpacity(0);

    m_village = new GameSprite(m_texture.findRegion("villageLogo"));
    m_village.setPosition(240,160);
    this.add(m_village);
    m_village.setOpacity(0);

    m_and = new GameSprite(m_texture.findRegion("and"));
    m_and.setPosition(433,350);
    this.add(m_and);
    m_and.setOpacity(0);

    m_present = new GameSprite(m_texture.findRegion("presents"));
    m_present.setPosition(433,59);
    this.add(m_present);
    m_present.setOpacity(0);

    AnimateFadeIn in1 = new AnimateFadeIn(0.5f);
    AnimateFadeIn in2 = new AnimateFadeIn(0.5f);
    AnimateFadeIn in3 = new AnimateFadeIn(0.5f);
    AnimateFadeIn in4 = new AnimateFadeIn(0.5f);

    AnimateDelay d1 = new AnimateDelay(0.5f);
    AnimateDelay d2 = new AnimateDelay(1.0f);
    AnimateDelay d3 = new AnimateDelay(2.0f);

    AnimateDelay d1a = new AnimateDelay(4.0f);
    AnimateDelay d2a = new AnimateDelay(3.5f);
    AnimateDelay d3a = new AnimateDelay(3.0f);
    AnimateDelay d4a = new AnimateDelay(1.8f);

    AnimateFadeOut out1 = new AnimateFadeOut(0.5f);
    AnimateFadeOut out2 = new AnimateFadeOut(0.5f);
    AnimateFadeOut out3 = new AnimateFadeOut(0.5f);
    AnimateFadeOut out4 = new AnimateFadeOut(0.5f);

    AnimateDelay d4 = new AnimateDelay(0.25f);

    GameAnimateable[] a1 = {in1,d1a,out1};
    GameAnimateable[] a2 = {d1,in2,d2a,out2};
    GameAnimateable[] a3 = {d2,in3,d3a,out3};
    GameAnimateable[] a4 = {d3,in4,d4a,out4,d4};

    GameAnimateable seq1 = new GameAnimationSequence(a1,1);
    GameAnimateable seq2 = new GameAnimationSequence(a2,1);
    GameAnimateable seq3 = new GameAnimationSequence(a3,1);
    seq4 = new GameAnimationSequence(a4,1);

    m_five.runAnimation(seq1);
    m_and.runAnimation(seq2);
    m_village.runAnimation(seq3);
    m_present.runAnimation(seq4);

    this.setCameraPosition(640,360);
    m_soundPlaying = false;

  }

  @Override
  public void update (float deltaTime) {

      m_stateTime += deltaTime;
      if (m_state == 0)
      {
        if (!m_soundPlaying)
        {
          if (m_stateTime > 0.1f)
          {
            this.playSound("splash", 0.7f);
            m_soundPlaying = true;
          }
        }

        if (m_stateTime > 2.5f)
        {
          GameMain.getSingleton().finishSetup();
          m_state = 1;
        }
      } else if (m_state == 1)
      {
        if (seq4.isRunning() == false)
        {
          GameLayer titleLayer = new TitleScreenLayer();
          this.replaceActiveLayer(titleLayer);
          this.cleanUp();
        }
      }
  }

  /*
  @Override
  protected void preCustomDraw()
  {

    m_spriteBatch.setProjectionMatrix(m_defaultMatrix);
    m_spriteBatch.begin();
    m_introSprite.draw(m_spriteBatch);
    menu.draw(m_spriteBatch);

    m_spriteBatch.end();
  }

  @Override
  protected void postCustomDraw()
  {
    m_spriteBatch.setProjectionMatrix(m_defaultMatrix);
    m_spriteBatch.begin();

    if (m_activeDialog != null)
    {
      if (m_activeDialog.isVisible())
        m_activeDialog.draw(m_spriteBatch);
    }

        m_spriteBatch.end();
  }
  */

  public void dispose()
  {
    //m_texture.dispose();
    //m_texture = null;
  }

  public void cleanUp()
  {
    m_texture.dispose();
    m_texture = null;
    this.removeSound("splash");
  }
}
