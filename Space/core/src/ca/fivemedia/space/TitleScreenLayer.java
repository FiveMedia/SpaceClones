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

public class TitleScreenLayer extends GameLayer implements GameMenuListener {
  GameInputManager m_inputManager;
  Matrix4 m_defaultMatrix = new Matrix4();
  GameSprite m_introSprite;
  float xx,yy; 
  static BitmapFont m_font32 = null;
  boolean m_musicStarted = false;
  static TextureAtlas m_treeTextures = null;
  static Texture m_introScreenTexture = null;
  int m_delayTicks = 0;
  BaseDialog m_activeDialog = null;
  int m_buttonDelay = 30;
  GameMenu menu = null;

  public TitleScreenLayer() {
    //Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);

    this.setMusic("99C_title_screen_BGM.ogg");

    if (m_introScreenTexture == null)
      m_introScreenTexture = new Texture("IntroSprite2.png");
    else
    {
      m_introScreenTexture.dispose();
      m_introScreenTexture = new Texture("IntroSprite2.png");
    }

    m_introSprite = new GameSprite(m_introScreenTexture);
    m_introSprite.setPosition(0,0);

    if (m_font32 == null)
      m_font32 = new BitmapFont(Gdx.files.internal("Font32.fnt"), Gdx.files.internal("Font32.png"), false);
    else
    {
      m_font32.dispose();
      m_font32 = new BitmapFont(Gdx.files.internal("Font32.fnt"), Gdx.files.internal("Font32.png"), false);
    }

    m_delayTicks = 0;

    Gdx.app.debug("space", "TitleScreenLayer 2");
    m_inputManager = new GameInputManager();
    m_inputManager.setViewport(GameMain.getSingleton().m_viewport);
    m_defaultMatrix = m_camera.combined.cpy();
    m_defaultMatrix.setToOrtho2D(0, 0, 1280, 720);

    if (m_treeTextures == null)
    {
      m_treeTextures = new TextureAtlas("trees.txt");
    } else
    {
      m_treeTextures.dispose();
      m_treeTextures = new TextureAtlas("trees.txt");
    }

    GameButton playButton = new GameButton(m_treeTextures, "Continue", m_font32);
    //playButton.setPosition((1280 - playButton.getWidth())/2, 400);

    GameButton optionsButton = new GameButton(m_treeTextures, "Options", m_font32);
    //GameButton quitButton = new GameButton(textures, "Quit", m_font32);

    GameButton newButton = new GameButton(m_treeTextures, "New Game", m_font32);

    GameButton exitButton = new GameButton(m_treeTextures, "Quit", m_font32);

    //GameButton testButton = new GameButton(m_treeTextures, "Test", m_font32);
    //Change below to have testButton instead of newButton for test mode
    menu = new GameMenu(playButton, optionsButton, newButton, exitButton, 1, 20, true, m_inputManager, this);
    //this.add(menu);

    Gdx.app.debug("space", "TitleScreenLayer 3");

    //menu.setPosition(502,400);
    menu.setPosition(850,585);
    m_musicStarted = false;


    this.setCameraPosition(640,360);
    Gdx.app.debug("space", "TitleScreenLayer done");
  }

  public void buttonSelected(int buttonNum)
  {
    if (m_activeDialog != null)
    {

      if (m_buttonDelay > 10)
      {
        if (buttonNum == 1)
        {
            m_activeDialog = null;
            return;
        }

        this.eraseGame(0);
        this.loadGameDefaults();
        this.loadGame(0);
        this.replaceActiveLayer(new MainGameLayer(-1,-1));
        this.cleanUp();
      }

      return;
    }

    if (buttonNum == 1)
    {
      //play
      this.loadGame(0);
      //this.testMode();   //uncomment line above and get rid of this line!
      this.replaceActiveLayer(new MainGameLayer(-1,-1));
      //this.replaceActiveLayer(new MainGameLayer(3,0));
      this.cleanUp();

    } else if (buttonNum == 2)
    {
      //options
       this.replaceActiveLayer(new OptionsScreenLayer());
       this.cleanUp();
    } else if (buttonNum == 3)
    {
      //production
      //pop up dialog - This will erase all progress and start new. Are you sure?
      this.playSound("menuOpen", 0.8f);
      m_activeDialog = new BaseDialog(m_font32, "No", "Yes", 1, m_treeTextures, this, m_inputManager);
      m_activeDialog.setTitle("Start New Game");
      m_activeDialog.setPrompt1("Are you sure you wish to start a new game?");
      m_activeDialog.setPrompt2("You will lose all previously saved progress.");
      m_activeDialog.setVisible(true);
      m_activeDialog.show();
      m_buttonDelay = 0;
    } else
    {
      System.exit(0);
    }
  }

  @Override
  public void update (float deltaTime) {

    m_buttonDelay++;

    if (m_activeDialog != null)
    {
      m_inputManager.handleInput();
      m_activeDialog.animate(deltaTime);
      if (m_buttonDelay > 10)
        m_activeDialog.update(deltaTime);

      return;
    }

    m_inputManager.handleInput(); 

    m_delayTicks++;

    menu.animate(deltaTime);
    menu.update(deltaTime);

    if (!m_musicStarted)
    {
      this.loopSound("music", 0.8f);  
      m_musicStarted = true; 
    }

    if (m_inputManager.isSpeedPressed())
    {
      if (m_delayTicks > 60)
        System.exit(0);
    }
    
  }

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

  public void dispose()
  {
    //m_texture.dispose();
    //m_texture = null;
  }

  public void testMode()
  {

      this.setGlobal("NumLives", "99");
      this.setGlobal("Stage1State", "Unlocked");
      this.setGlobal("Stage2State", "Unlocked");
      this.setGlobal("WonGame", "NO");
      this.setGlobal("Stage2NinetyNine", "NO");
      this.setGlobal("Stage3NinetyNine", "NO");

      int nb = 0;

      for (int stage = 1; stage < 3; stage++)
      {
          for (int lv = 0; lv < 12; lv++)
          {
              this.setGlobal("Stage" + stage + "Level" + lv + "State", "Unlocked");
              if ((lv > 1) && (lv < 12))
              {
                this.setGlobal("Stage" + stage + "Level" + lv + "Clones", "2");
                nb += 3;
              } else
              {
                this.setGlobal("Stage" + stage + "Level" + lv + "Clones", "0");
              }
          }
      }

      this.setGlobal("TotalClones", "" + nb);
      this.setGlobal("LevelCompleted", "-1");
      this.setGlobal("LevelCompletedClones", "-1");
      this.setGlobal("GameSaveID", "5");

  }

  public void cleanUp()
  {
    Gdx.app.debug("space", "TitleScreenLayer cleanUp called");
  }
}
