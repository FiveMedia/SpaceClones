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
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Disposable;

import com.badlogic.gdx.math.*;

import com.badlogic.gdx.physics.box2d.*;

import box2dLight.*;

import ca.fivemedia.gamelib.*;

public class MainGameLayer extends GameLayer implements GameMenuListener {

  public static ParticleEffectPool bombEffectPool;
  public static ParticleEffectPool sawEffectPool;
  public static ParticleEffectPool slimeEffectPool;
  public static ParticleEffectPool ufoEffectPool;

  public PlayerSprite playerSprite;
  public BuggySprite buggySprite;
  GameSprite m_backSprite, m_fadeOutSprite;
  public PlayerHud m_playerHud;
  GameSprite m_levelWonHud;
  Texture m_levelWonHudTexture = null;
  CoinHud m_coinHud;
  LevelTitle m_levelTitle;
  GameText m_selectTitle;
  public GameTileMap tiledMap;
  float stateTime;
  float tw = 48.0f;
  float th = 32.0f;
  int m_mapWidth, m_mapHeight;
  GameSprite m_greatSprite;
  GameAnimateable m_greatAnimation1, m_greatAnimation2;
  GameSprite m_unlockSprite;
  boolean m_mission2Unlocked = false;
  GameAnimateable m_unlockAnimation1, m_unlockAnimation2;
  int m_lives = 5;
  BombWeaponSprite m_bombWeapon;
  boolean m_ambientFadeDone = true;

  static int m_controllerValidTicks = 0;

  float m_playerXOffset = 0;
  float m_playerYOffset = 0;
  float m_playerXOffsetTarget = 0;
  float m_playerYOffsetTarget = 0;

  float m_levelXStart, m_levelYStart;

  Rectangle ploc = new Rectangle();
  Rectangle bloc = new Rectangle();
  static BitmapFont m_font24, m_font32, m_font16;

  int m_ClonesCollected = 0;
  boolean m_replay = false;

  ArrayList<BaseSprite> enemies;
  ArrayList<TutorialSprite> tutorials;
  ArrayList<CheckPointSprite> checkpoints;
  ArrayList<CollectibleSprite> collectibles;
  public ArrayList<PlatformSprite> platforms;
  ArrayList<BaseSprite> enemiesToAdd;
  static ArrayList<Body> m_bodies = new ArrayList<Body>(7500);

  boolean m_justDied = false;
  boolean m_noClonesLeft = false;

  boolean levelComplete = false;

  static int m_resumeControllerState = 10;

  //ArrayList<BlockSprite> blocks;
  //ArrayList<CollectibleSprite> collectibles;

  GamePanel getReadyPanel, gameOverPanel;

  LightningSprite m_lightning;

  static String APP_N = "space";
  FPSLogger fpsLogger;
  
  int m_level, m_stage;
  int m_replayLevel;
  GameInputManager inputManager;
  Matrix4 m_defaultMatrix, debugMatrix;

  boolean m_stageSelectionLevel = false;

  int m_afterTutorialSpecial = 0;

  static RayHandler rayHandler;
  static World world;
  Box2DDebugRenderer debugRenderer;
  PointLight pointLight;

  float m_ra, m_ga, m_ba, m_aa;

  MoonSprite m_moon = null;

  float m_maxX, m_maxY;

  float m_halfCameraWidth = 640f;
  float m_halfCameraHeight = 360f;

  TreeSprite m_ufo = null;
  TreeSprite m_explosion = null;
  static TextureAtlas treeTextures = null;
  static TextureAtlas gameSpritesTextures = null;
  float m_currA, m_stepA;
  boolean m_disableLights = false;

  BaseDialog m_activeDialog = null;

  GameSprite jumpButton, fireButton;
  DPadPanel dpad;
  //GameText m_debugTouch;

  int m_levelJustWon = -1;

  static TextureAtlas myTextures = null;
  static Texture m_backSprite1Texture = null;
  static Texture m_backTexture = null;
  static Texture m_fadeOutTexture = null;
  static Texture m_gameOverTexture = null;
  int m_volumeTicks = 0;
  public Vector2 m_playerLoc = new Vector2();

  boolean m_isSpeedLevel = false;

  int m_backTextureCode = 0;

  TutorialSprite m_tutorial  = null;
  BaseSprite m_baseSprite = null;
  PlatformSprite m_platformSprite = null;
  int m_iter = 0;
  int m_clonesSaved = 0;

  public boolean m_bridgePressed = false;
  public boolean m_bombPressed = false;
  public boolean m_leftShip = false;
  public boolean m_enteredShip = false;
  public boolean m_movedOnce = false;

  public GameSprite m_hintSprite;
  boolean m_buggyDying = false;

  float m_gameTimer = 0;
  float m_lastGameTimer = 0;
  boolean m_canExit = true;
  boolean m_goBackToStageSelect = false;


  public MainGameLayer(int stage, int level)
  {
    super();

    boolean reloadTextures = false;

    m_stage = stage;
    m_level = level;

    gameState = -1;

    Gdx.app.debug(APP_N, "Stage = " + m_stage + " level = " + m_level);

    if ((m_stage < 0) || (m_level < 0))
    {
      // this is stage select
      m_stageSelectionLevel = true;
      reloadTextures = true;
      gameState = -5;
    }

    m_defaultMatrix = m_camera.combined.cpy();
    m_defaultMatrix.setToOrtho2D(0, 0, 1280, 720);

    fpsLogger = new FPSLogger();
    //Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);

    inputManager = new GameInputManager();
    inputManager.setViewport(GameMain.getSingleton().m_viewport);

    if (reloadTextures) //Bug on Android TV, if app not fully shut down, comes back with textures all black
    {
      safeDispose(m_backSprite1Texture);
      m_backSprite1Texture = null;
      safeDispose(m_gameOverTexture);
      m_gameOverTexture = null;
      safeDispose(treeTextures);
      treeTextures = null;
      safeDispose(gameSpritesTextures);
      gameSpritesTextures = null;
      safeDispose(m_font32);
      m_font32 = null;
      safeDispose(m_font24);
      m_font24 = null;
      safeDispose(m_font16);
      m_font16 = null;
      safeDispose(m_fadeOutTexture);
      m_fadeOutTexture = null;
      safeDispose(myTextures);
      myTextures = null;
    }

    if (m_backSprite1Texture == null)
      m_backSprite1Texture = new Texture("blank_back.png");

    GameSprite backSprite1 = new GameSprite(m_backSprite1Texture);
    backSprite1.setPosition(0,0);

    if (m_gameOverTexture == null)
    {
      m_gameOverTexture = new Texture("GameOver.png");
    }

    GameSprite gameOverSprite = new GameSprite(m_gameOverTexture);
    gameOverSprite.setPosition(1280.0f/2-200, 720.0f/2-125);
    gameOverPanel = new GamePanel();
    gameOverPanel.add(backSprite1);
    gameOverPanel.add(gameOverSprite);
    gameOverPanel.setVisible(false);

    if (treeTextures == null)
    {
      treeTextures = new TextureAtlas("trees.txt");
    }

    if (gameSpritesTextures == null)
    {
      gameSpritesTextures = new TextureAtlas("game_sprites.txt");
    }

    m_greatSprite = new GameSprite(treeTextures.findRegion("super_great"));
    m_greatSprite.setVisible(false);
    m_greatSprite.setScale(0.05f);
    m_greatSprite.setOpacity(1f);
    m_greatSprite.setRotation(0);
    m_greatAnimation1 = new AnimateRotateTo(0.1f,0,359,6);
    m_greatAnimation2 = new AnimateScaleTo(0.6f,0.2f, 0.2f, 1.5f, 1.5f, 1);
    m_greatSprite.setPosition(400, 500);

    m_unlockSprite = new GameSprite(treeTextures.findRegion("mission2_unlocked"));
    m_unlockSprite.setVisible(false);
    m_unlockSprite.setScale(0.05f);
    m_unlockSprite.setOpacity(1f);
    m_unlockSprite.setRotation(0);
    m_unlockAnimation1 = new AnimateRotateTo(0.1f,0,359,6);
    m_unlockAnimation2 = new AnimateScaleTo(0.6f,0.2f, 0.2f, 1.5f, 1.5f, 1);
    m_unlockSprite.setPosition(340, 300);

    dpad = new DPadPanel(treeTextures);
    dpad.setPosition(25,10);
    dpad.setVisible(false);
    dpad.setOpacity(0.15f);
    String hudSize = this.getGlobal("hudSize");
    if (hudSize != null)
    {
      int hs = Integer.parseInt(hudSize);
      dpad.setDPadSize(hs);
    }

    inputManager.setDpad(dpad);

    if (inputManager.m_touchDevice)
    {
      dpad.setVisible(true);

    }

    if (m_backTexture != null)
    {
      m_backTexture.dispose();
      m_backTexture = null;
    }

    if ((m_stage == 4) && (m_level == 9))
    {
      m_backTexture = new Texture("stars_back.png");
      m_backTextureCode = 3;
    } else if (m_stage != 2)
    {
      m_backTexture = new Texture("level_back.png");
       m_backTextureCode = 1;
    } else 
    {
      m_backTexture = new Texture("stage2_back.png");
       m_backTextureCode = 2;
    }

    m_backSprite = new GameSprite(m_backTexture);

    m_backSprite.setPosition(0,0);
    m_backSprite.setVisible(true);

    if (m_font32 == null)
    {
      Gdx.app.debug(APP_N, "Fonts were NULL!!!! ****** !!!!!");
      m_font32 = new BitmapFont(Gdx.files.internal("Font32.fnt"), Gdx.files.internal("Font32.png"), false);
      m_font24 = new BitmapFont(Gdx.files.internal("Font24.fnt"), Gdx.files.internal("Font24.png"), false);
      m_font16 = new BitmapFont(Gdx.files.internal("Font16.fnt"), Gdx.files.internal("Font16.png"), false);
    }

    m_levelWonHudTexture = new Texture("level_won_hud.png");
    m_levelWonHud = new GameSprite(m_levelWonHudTexture);
    m_levelWonHud.setPosition(0,0);
    m_levelWonHud.setVisible(false);

    m_backSprite = new GameSprite(m_backTexture);

    m_playerHud = new PlayerHud(m_font24,5, treeTextures);
    m_playerHud.setPosition(36, 720.0f - 80);
    m_playerHud.setEnergy(10);

    m_coinHud = new CoinHud(m_font32, treeTextures);
    m_coinHud.setPosition(1100, 28);
    m_coinHud.setCoins(0);
    m_coinHud.setPercentComplete(0);
    m_coinHud.setVisible(false);

    m_levelTitle = new LevelTitle(m_font32);
    m_levelTitle.setPosition(1280.0f/2-375, 720.0f/2-100);
    m_levelTitle.setVisible(false);

    m_selectTitle = new GameText(m_font32);
    m_selectTitle.setPosition(625f, 685f);
    m_selectTitle.setVisible(false);

    if (m_fadeOutTexture == null)
      m_fadeOutTexture = new Texture("fade_out.png");

    m_fadeOutSprite = new GameSprite(m_fadeOutTexture);
    m_fadeOutSprite.setOpacity(0);

    if (bombEffectPool == null)
    {
      ParticleEffect bombEffect = new ParticleEffect();
      bombEffect.load(Gdx.files.internal("player_die.p"), Gdx.files.internal(""));
      bombEffect.setEmittersCleanUpBlendFunction(true);
      bombEffectPool = new ParticleEffectPool(bombEffect, 5, 15);
    }

    if (sawEffectPool == null)
    {
      ParticleEffect sawEffect = new ParticleEffect();
      sawEffect.load(Gdx.files.internal("saw_particle.p"), Gdx.files.internal(""));
      sawEffect.setEmittersCleanUpBlendFunction(true);
      sawEffectPool = new ParticleEffectPool(sawEffect, 20, 50);
    }

    if (slimeEffectPool == null)
    {
      ParticleEffect sawEffect = new ParticleEffect();
      sawEffect.load(Gdx.files.internal("slime_particle.p"), Gdx.files.internal(""));
      sawEffect.setEmittersCleanUpBlendFunction(true);
      slimeEffectPool = new ParticleEffectPool(sawEffect, 8, 12);
    }

    Gdx.app.debug(APP_N, "Initialized End. ********");

  }

  public void fadeIn(float duration)
  {
    m_fadeOutSprite.setOpacity(1.0f);
    m_fadeOutSprite.runAnimation(new AnimateFadeOut(duration));
  }

  public void fadeToBlack(float duration)
  {
    m_fadeOutSprite.setOpacity(0f);
    m_fadeOutSprite.runAnimation(new AnimateFadeIn(duration));
  }

  
  public void fadeAmbientIn(float duration)
  {
    if (m_ambientFadeDone)
    {
      if (m_currA < m_aa)
      {
        m_stepA = m_aa/duration;
        m_currA = 0.025f;
        m_ambientFadeDone = false;
      }
    }
  }

  public void fadeAmbientOut(float duration)
  {
      if (m_ambientFadeDone)
      {
        if (m_currA > 0.025f)
        {
          m_stepA = (0.025f - m_aa)/duration;
          m_currA = m_aa;
          m_ambientFadeDone = false;
        }
      }
  }

  public void updateFade(float deltaTime)
  {
    if (!m_ambientFadeDone)
    {
      m_currA += (m_stepA * deltaTime);
      if (m_currA < 0.025f)
      {
        m_currA = 0.025f;
        m_ambientFadeDone = true;
      } else if (m_currA > m_aa)
      {
        m_currA = m_aa;
        m_ambientFadeDone = true;
      }

      this.setAmbientLight(m_ra, m_ga, m_ba, m_currA);
    }
  }

  public void setAmbientLight(float r, float g, float b, float a)
  {
    rayHandler.setAmbientLight(r, g, b, a);
  }

  public void resetAmbientLight()
  {
    rayHandler.setAmbientLight(m_ra, m_ga, m_ba, m_aa);
    m_currA = m_aa;
    m_ambientFadeDone = true;
  }

  public PointLight createPointLight(Color color, float dist, float xx, float yy)
  {
    int numRays = 16;
    if (inputManager.m_touchDevice)
    {
      numRays = 8;
    }

    if (inputManager.androidTV)
    {
      numRays = 32;
    }

    PointLight pLight = new PointLight(rayHandler,numRays, color, dist, xx,yy);
    return pLight;
  }

  public ConeLight createConeLight(Color color, float distance, float xx, float yy, float dirDegrees, float coneDegrees)
  {
    int numRays = 16;
    if (inputManager.m_touchDevice)
    {
      numRays = 8;
    }

    if (inputManager.androidTV)
    {
      numRays = 32;
    }

    ConeLight cLight = new ConeLight(rayHandler, numRays, color, distance, xx, yy, dirDegrees, coneDegrees);
    return cLight;
  }

  public float getFloat(String key, MapObject mp)
  {
      Float f = (Float) (mp.getProperties().get(key));
      float ff = f.floatValue();
      return ff;

  }

  public float getStrToFloat(String key, MapObject mp)
  {
      String f = (String) (mp.getProperties().get(key));
      Float ff = new Float(f);
      return ff.floatValue();

  }

  public int getStrToInt(String key, MapObject mp)
  {
      String f = (String) (mp.getProperties().get(key));
      Integer ff = new Integer(f);
      return ff.intValue();
  }

  public void setCameraPosition(float worldX, float worldY, float duration)
  {
    //float cameraWidth = 1280.0f * m_camera.zoom;
    //float cameraHeight = 720.0f * m_camera.zoom;

    float cameraWidth = m_camera.viewportWidth;
    float cameraHeight = m_camera.viewportHeight;
  
    float x = worldX;
    float halfCameraWidth = cameraWidth/2.0f;

    if (halfCameraWidth > x)
      x = halfCameraWidth;

    float y = worldY;
    float halfCameraHeight = cameraHeight/2.0f;

    if (halfCameraHeight > y)
      y = halfCameraHeight;

    //TODO: Assumes map is always 120 wide by 50 tall!!
    float maxX = (m_mapWidth*tw) - halfCameraWidth;
    if (x > maxX)
      x = maxX;

    float maxY = ((m_mapHeight)*th) - halfCameraHeight;
    if (y > maxY)
      y = maxY;

    super.setCameraPosition(x,y, duration);

  }

  public void setCameraPositionFZ(float worldX, float worldY, float duration, float z)
  {
    float cameraWidth = 1280.0f * z;
    float cameraHeight = 720.0f * z;

    //float cameraWidth = m_camera.viewportWidth;
    //float cameraHeight = m_camera.viewportHeight;
  
    float x = worldX;
    float halfCameraWidth = cameraWidth/2.0f;

    if (halfCameraWidth > x)
      x = halfCameraWidth;

    float y = worldY;
    float halfCameraHeight = cameraHeight/2.0f;

    if (halfCameraHeight > y)
      y = halfCameraHeight;

    //TODO: Assumes map is always 120 wide by 50 tall!!
    float maxX = (m_mapWidth*tw) - halfCameraWidth;
    if (x > maxX)
      x = maxX;

    float maxY = ((m_mapHeight)*th) - halfCameraHeight;
    if (y > maxY)
      y = maxY;

    super.setCameraPosition(x,y, duration);

  }

  public void setCameraPosition(float worldX, float worldY)
  {
  
    //Gdx.app.debug(APP_N,"setCameraPosition Input: " + worldX + ", " + worldY);

    //float cameraWidth = 1280.0f * m_camera.zoom;
    //float cameraHeight = 720.0f * m_camera.zoom;

    float cameraWidth = m_camera.viewportWidth * m_camera.zoom;
    float cameraHeight = m_camera.viewportHeight * m_camera.zoom;

    float x = worldX;
    float halfCameraWidth = cameraWidth/2.0f;

    if (halfCameraWidth > x)
      x = halfCameraWidth;

    float y = worldY;
    float halfCameraHeight = cameraHeight/2.0f;

    if (halfCameraHeight > y)
      y = halfCameraHeight;

    //TODO: Assumes map is always 120 wide by 50 tall!!
    float maxX = (m_mapWidth*tw) - halfCameraWidth;
    if (x > maxX)
      x = maxX;

    float maxY = ((m_mapHeight)*th) - halfCameraHeight;
    if (y > maxY)
      y = maxY;

    //x = x + halfCameraWidth;

    //Gdx.app.debug(APP_N,"setCameraPosition: " + x + ", " + y);
    super.setCameraPosition(x,y);
    //m_camera.position.set(x,y,0);

  }

  @Override
  public void update (float deltaTime) {
  
    stateTime += deltaTime;
    if (gameState == -5)
    {
      //start of a stage or level select level
      //this.stopSound("music");
      m_wasJustShaking = false;
      this.stopSound("heartbeat");
      m_greatSprite.setVisible(false);
      m_unlockSprite.setVisible(false);
      m_playerHud.setVisible(true);
      m_coinHud.setVisible(true);

      this.loadLevel(m_stage, m_level);
      this.setCameraZoom(1.0f);
      this.setCameraPosition(buggySprite.getX() + buggySprite.getWidth()/2, buggySprite.getY());

      m_selectTitle.setVisible(true);
      this.fadeIn(0.7f);
      gameState = 99;
      
      //String lc = this.getGlobal("LevelCompleted");
      //int lci = Integer.parseInt(lc);
      //if (lci > 0)
      //{
      //  gameState = 95;
      //}

      String nl= this.getGlobal("NumLives");
      int nli = Integer.parseInt(nl);
      if (nli < 0)
      {
        //dead, must resurrect with 5 more lives
        //TODO: DEAD OUT OF LIVES!!!
        gameState = 180;
      }

      String tClones = this.getGlobal("TotalClones");
      int totClones = Integer.parseInt(tClones);
      m_coinHud.setCoins(totClones);
      m_coinHud.setPercentComplete(this.getPercentComplete() );

      stateTime = 0;
      m_gameTimer = 0;
      m_lastGameTimer = 0;
    }
    else if (gameState == -2)
    {
      m_wasJustShaking = false;
      playerSprite.setLocationToCheckpoint();
      buggySprite.setLocationToCheckpoint();
      m_selectTitle.setVisible(false);
      m_playerHud.setVisible(true);
      m_coinHud.setVisible(false);
      m_playerHud.setClones(3);

      stateTime = 0;
      this.stopAllSounds();
      this.loopSound("music", 0.7f);
      this.loopSound("music2", 0.0f);

      m_gameTimer = 0;
      m_lastGameTimer = 0;
      m_playerHud.setTime(0);

      String nl= this.getGlobal("NumLives");
      int nli = Integer.parseInt(nl);
      playerSprite.setLives(nli);
      m_playerHud.setEnergy(playerSprite.getEnergy());
      m_playerHud.setLives(playerSprite.getLives());
      this.setGlobal("NumLives", "" + playerSprite.getLives());
      this.saveGame();
      this.resetLevel();
      this.resetAmbientLight();
      this.fadeIn(0.5f);
      pauseGame();

      if ((!m_justDied) && (!m_replay))
      {
        Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
        this.setCameraZoom(0.4f);
        this.setCameraPosition(m_levelXStart, m_levelYStart);
        gameState = 0;
        m_levelTitle.setVisible(true);
      } else
      {
        m_playerXOffset = 0;
        m_playerYOffset = 0;
        this.setCameraZoom(1.0f);
        this.setCameraPosition(buggySprite.getX()  + m_playerXOffset + buggySprite.getWidth()/2, buggySprite.getY() + m_playerYOffset);
        gameState = 2;
        stateTime = 0;
      }

    } else if (gameState == -1)
    {
      //Get Ready just before level starts
      //getReadyPanel.setVisible(true);
      this.stopSound("heartbeat");
      m_playerHud.setClones(3);
      m_selectTitle.setVisible(false);
      m_playerHud.setVisible(true);
      m_coinHud.setVisible(false);

      this.loadLevel(m_stage, m_level);
      stateTime = 0;
      gameState = 0;
      m_gameTimer = 0;
      m_lastGameTimer = 0;
      m_playerHud.setTime(0);
      this.stopAllSounds();
      this.setCameraZoom(0.4f);
      this.setCameraPosition(m_levelXStart, m_levelYStart);
      m_levelTitle.setVisible(true);
      m_playerHud.setEnergy(playerSprite.getEnergy());
      m_playerHud.setLives(playerSprite.getLives());
      pauseGame();
      this.fadeIn(1.0f);
      m_wasJustShaking = false;
      inputManager.handleInput();

      if (m_level < 0)
      {
        m_coinHud.setPercentComplete(this.getPercentComplete());
      }

    } else if (gameState == 0)
    { 
      m_playerHud.setTime(0);
      m_wasJustShaking = false;
      m_fadeOutSprite.animate(deltaTime);
      this.setCameraPosition(m_levelXStart, m_levelYStart);
      inputManager.handleInput();
      if (stateTime > 2.1f)
      {
        //this.stopSound("music");
        //this.stopSound("music2");
        stateTime = 0;
        gameState = 2;
        this.playSound("intro1", 0.9f);
        //getReadyPanel.setVisible(false);
        m_levelTitle.setVisible(false);
        this.setCameraPosition(buggySprite.getX()  + m_playerXOffset + buggySprite.getWidth()/2, buggySprite.getY() + m_playerYOffset, 1.0f);
        this.setCameraZoom(1.0f, 1.0f);
        inputManager.clearTouches();

      } 
    } else if (gameState == 2)
    {
      m_playerHud.setTime(0);
      if (m_replay)
        this.setCameraPosition(buggySprite.getX()  + m_playerXOffset + buggySprite.getWidth()/2, buggySprite.getY() + m_playerYOffset);
        
      m_fadeOutSprite.animate(deltaTime);
      if (stateTime > 0.77f)
      {
        m_replay = false;
        gameState = 3;
        stateTime = 0;
        inputManager.clearTouches();
        //this.setCameraZoom(1.0f, 1.0f);
        if (m_justDied)
        {
          gameState = 4;
          stateTime = 1.0f;
        }
        //this.playSound("intro1", 0.9f);
      }
    } else if (gameState == 3)
    {
        m_fadeOutSprite.animate(deltaTime);
        buggySprite.headlightsOn();
        if (stateTime > 0.1f)
        {
          stateTime = 0;
          gameState = 4;
          m_playerHud.setVisible(true);

        }

    } else if (gameState == 4)
    {

        inputManager.handleInput();
        buggySprite.headlightsOn();
        if ((stateTime > 0.05f) && (inputManager.isLeftPressed() || inputManager.isRightPressed() || inputManager.isDownPressed() || inputManager.isSpeedPressed()))
        {
          stateTime = 0;
          gameState = 10;
          m_gameTimer = 0;
          m_lastGameTimer = 0;
          m_playerHud.setVisible(true);

          //this.stopSound("music");
          this.loopSound("music", 0.7f);
          this.loopSound("music2", 0.0f);
          //this.loopSound("rain", 0.2f);
          //getReadyPanel.setVisible(false);
          m_levelTitle.setVisible(false);
          resumeGame();
        }
    }else if (gameState == 1)
    {
      m_fadeOutSprite.animate(deltaTime);
      gameOverPanel.setVisible(true);
      pauseGame();
      this.stopAllSounds();
      gameState = -10;
      this.eraseGame(0);
      this.loadGameDefaults();
      this.loadGame(0);
      this.saveGame();
      stateTime = 0;

    } else if (gameState == -10)
    {
      m_fadeOutSprite.animate(deltaTime);
      if (stateTime > 4)
      {
        this.fadeToBlack(0.5f);
        gameState = -11;
        stateTime = 0;
      }

    } else if (gameState == -11)
    {
        m_fadeOutSprite.animate(deltaTime);
        if (stateTime > 0.5f)
        {
          this.replaceActiveLayer(new TitleScreenLayer());
          this.cleanUp();
        }
    } else if (gameState == 10)
    {

      if (m_activeDialog == null)
      {
        m_gameTimer += deltaTime;
        if ((m_gameTimer - m_lastGameTimer) >= 0.02f)
        {
          m_playerHud.setTime(m_gameTimer);
          m_lastGameTimer = m_gameTimer;
        }
      }

      //update player movement and animation frame.
      inputManager.handleInput();
      m_controllerValidTicks++;
      if (m_controllerValidTicks > 30)
      {
        m_controllerValidTicks = 0;
        if (inputManager.isValid() == false)
        {
          gameState = 350;
          m_resumeControllerState = 10;
          return;
        }
      }

      m_playerHud.animate(deltaTime);
      updateFade(deltaTime);
      
      if (inputManager.isSpeedPressed())
      {
        
        //pause button
        gameState = 300;
        stateTime = 0;
        this.pauseGame(); 
        this.playSound("menuOpen", 0.8f);
        m_activeDialog = new BaseDialog(m_font32, "Exit Level", "Continue", 2, treeTextures, this, inputManager);
        m_fadeOutSprite.setOpacity(0.25f);
        m_activeDialog.setTitle("PAUSED");
        m_activeDialog.setVisible(true);
        m_activeDialog.show();
      }

      float xm = 2;
      if ((playerSprite.isHover()) || (m_buggyDying))
        xm = 16;

      if (m_playerXOffset != m_playerXOffsetTarget)
      {
        if (m_playerXOffset < m_playerXOffsetTarget)
        {
          m_playerXOffset += xm;
          if (m_playerXOffset > m_playerXOffsetTarget)
            m_playerXOffset = m_playerXOffsetTarget;
        } else
        {
          m_playerXOffset -= xm;
          if (m_playerXOffset < m_playerXOffsetTarget)
            m_playerXOffset = m_playerXOffsetTarget;
        }
      }

      if (m_playerYOffset != m_playerYOffsetTarget)
      {
        if (m_playerYOffset < m_playerYOffsetTarget)
        {
          m_playerYOffset += xm;
          if (m_playerYOffset > m_playerYOffsetTarget)
            m_playerYOffset = m_playerYOffsetTarget;
        } else
        {
          m_playerYOffset -= xm;
          if (m_playerYOffset < m_playerYOffsetTarget)
            m_playerYOffset = m_playerYOffsetTarget;
        }
      }
      
      if (this.isShaking() == false)
      {
        if (m_wasJustShaking)
        {
          m_playerXOffsetTarget = 0;
          m_playerYOffsetTarget = 0;
          m_wasJustShaking = false;
        }
        if (buggySprite.m_focus)
          this.setCameraPosition(buggySprite.getX()  + m_playerXOffset + buggySprite.getWidth()/2, buggySprite.getY() + m_playerYOffset);
        else
          this.setCameraPosition(playerSprite.getX() + m_playerXOffset + playerSprite.getWidth()/2, playerSprite.getY() + m_playerYOffset);   
      } else
      {
        float tx = playerSprite.getX() + playerSprite.getWidth()/2;
        float ty = playerSprite.getY();

        m_playerXOffset = -(tx - m_cameraPositionController.getX());
        m_playerYOffset = -(ty - m_cameraPositionController.getY());

        m_playerXOffsetTarget = m_playerXOffset;
        m_playerYOffsetTarget = m_playerYOffset;

      }
      this.handleCollisions();
      if (m_lightning != null)
      {
        m_lightning.update(deltaTime);
        if (m_lightning.isVisible())
        {
          m_lightning.animate(deltaTime);
        }
      }

      if ((playerSprite.isAlive() == false) || (buggySprite.isAlive() == false) || m_noClonesLeft)
      {
          gameState = 80;
          stateTime = 0;
          playerSprite.loseLife();
          m_playerHud.setLives(playerSprite.getLives());
          playerSprite.setVisible(false);
          this.fadeToBlack(0.5f);
      }

    } else if (gameState == 25)
    {
      if (stateTime > 0.75f)
      {
        this.wonLevel();
      }
    } else if (gameState == 26)
    {
      inputManager.handleInput();
    } else if (gameState == 51)
    {
      //tutorial/paused for some reason
      if ((m_stage == 4) && (m_level == 10))
      {
        gameState = 25;
        stateTime = 0;
        this.fadeToBlack(0.7f);
      } else
      {
        this.resumeGame();
        gameState = 10;
      }
    } else if (gameState == 495)
    {
      m_fadeOutSprite.animate(deltaTime);
      if (stateTime > 3.1f)
        this.replaceActiveLayer(new MainGameLayer(3,0));

    } else if (gameState == 497)
    {
      m_fadeOutSprite.animate(deltaTime);
      if (stateTime > 1.0f)
        this.replaceActiveLayer(new TitleScreenLayer());

    }else if (gameState == 498)
    {
      m_fadeOutSprite.animate(deltaTime);
      this.wonGame();
      this.fadeToBlack(1.0f);
      gameState = 497;
      stateTime = 0;
    } else if (gameState == 499)
    {
      m_fadeOutSprite.animate(deltaTime);
      gameState = 504;
      this.wonLevel();
    } else if (gameState == 500)
    {
      //level end (no plasma cube level end)

      //fade out, then fade back in.
      this.playSound("winSting", 0.9f);
      this.pauseGame();
      gameState = 502;
      stateTime = 0;
      this.setCameraZoom(0.4f, 1.0f);
      this.setCameraPosition(buggySprite.getX() + buggySprite.getWidth()/2, buggySprite.getY());
      this.wonLevel();
    } else if (gameState == 502)
    {
      this.setCameraPosition(buggySprite.getX() + buggySprite.getWidth()/2, buggySprite.getY());
      inputManager.handleInput();
      if (stateTime > 1.0)
      {
        if (this.checkIfWonGame())
        {
          this.fadeToBlack(3.0f);
          gameState = 495;
          stateTime = 0;
        } else
        {
          gameState = 503;
          m_levelWonHud.setVisible(true);
          if (m_clonesSaved == 3)
          {
            this.playSound("superGreat", 0.9f);
            m_greatSprite.setVisible(true);
            m_greatSprite.runAnimation(m_greatAnimation1);
            m_greatSprite.runAnimation(m_greatAnimation2);
            m_greatSprite.animate(deltaTime);
          }

          if (m_mission2Unlocked)
          {
            //mission 2 unlocked for the first time!
            m_unlockSprite.setVisible(true);
            m_unlockSprite.runAnimation(m_unlockAnimation1);
            m_unlockSprite.runAnimation(m_unlockAnimation2);
            m_unlockSprite.animate(deltaTime);
            if (m_clonesSaved != 3)
            {
              this.playSound("superGreat", 0.9f);
            }
          }
        }
      }
    } else if (gameState == 503)
    {
      m_greatSprite.animate(deltaTime);
      m_unlockSprite.animate(deltaTime);
      this.setCameraPosition(buggySprite.getX() + buggySprite.getWidth()/2, buggySprite.getY());
      inputManager.handleInput();
      if (inputManager.isJumpPressed())
      {
        //A button - next level
        gameState = 504;
        m_replay = false;

        if (m_goBackToStageSelect)
        {
          this.saveGame();
          m_level = -1;
          m_stage = -1;
          this.fadeToBlack(0.7f);
          gameState = 101;
          m_activeDialog = null;
          stateTime = 0;
          stopAllSounds();
        }

      } else if (inputManager.isFirePressed())
      {
        //B button - replay
        gameState = 504;
        m_replay = true;
      } else if (inputManager.isBridgePressed())
      {
        //go back to mission selection
        this.saveGame();
        m_level = -1;
        this.fadeToBlack(0.7f);
        gameState = 101;
        m_activeDialog = null;
        stateTime = 0;
        stopAllSounds();
      }
    } else if (gameState == 504)
    {
      this.setCameraPosition(buggySprite.getX() + buggySprite.getWidth()/2, buggySprite.getY());

      m_fadeOutSprite.animate(deltaTime);
      if (stateTime > 2.9f)
      {
        m_greatSprite.setVisible(false);
        m_unlockSprite.setVisible(false);
        this.fadeToBlack(0.5f);
        m_levelWonHud.setVisible(false);
        gameState = 505;
        stateTime = 0;
      }
    } else if (gameState == 505)
    {
      m_fadeOutSprite.animate(deltaTime);
      if (stateTime > 0.75f)
      {
        this.resumeGame();
        gameState = -1;
        stateTime = 0;
        if (m_replay)
        {
          m_level = m_replayLevel;
          gameState = -2;
        }
      }
    } else if (gameState == 80)  //dead, just waiting for fade out
    {
      m_fadeOutSprite.animate(deltaTime);
      if (stateTime > 0.5f)
      {
        m_justDied = true;
        gameState = -2;
        stateTime = 0;
        this.setGlobal("NumLives", "" + playerSprite.getLives());
        m_lives = playerSprite.getLives();
        if (playerSprite.getLives() < 1)
        {
          gameState = 1; // GAME OVER!
          this.fadeIn(0.5f);
        }
      }
    } else if (gameState == 95)
    {
      m_fadeOutSprite.animate(deltaTime);
      //animate won level getting Clones, then transition to 100
      //find the level door you just won
      String lc = this.getGlobal("LevelCompleted");
      String lcClones = this.getGlobal("LevelCompletedClones");
      String lastB = this.getGlobal("Stage" + m_stage + "Level" + lc + "Clones");


      int lv = Integer.parseInt(lc);
      int Clones = Integer.parseInt(lcClones);
      int lastClones = 0;
      if (lastB != null)
        lastClones = Integer.parseInt(lastB);

      //Gdx.app.debug("MGL","level completed = " + lv + " CloneGot=" + Clones + " lastGot=" + lastClones);

      gameState = 99;
      stateTime = 0;

      for (GameDrawable door1 : this.getChildren())
      {
        if (door1 instanceof LevelDoor)
        {
          LevelDoor door = (LevelDoor) door1;
          if (door.getLevel() == lv)
          {
            playerSprite.setPosition(door.getX()-20, door.getY());
            buggySprite.setPosition(door.getX()-20, door.getY());
          }
        }
      }

      if (gameState == 99)
      {
        this.setCameraPosition(buggySprite.getX()+buggySprite.getWidth()/2, buggySprite.getY());
        this.setGlobal("LevelCompleted", "-1");
        this.setGlobal("LevelCompletedClones", "-1");
      }

    } else if (gameState == 96)
    {
      m_fadeOutSprite.animate(deltaTime);
      if (stateTime > 5.25f)
      {
        stateTime = 0;
        this.setCameraZoom(1.0f, 0.75f);
        String lcClones = this.getGlobal("LevelCompletedClones");
        int clones = Integer.parseInt(lcClones);

        String lc = this.getGlobal("LevelCompleted");
        int lv = Integer.parseInt(lc);

        String lastB = this.getGlobal("Stage" + m_stage + "Level" + lv + "Clones");
        int lastClones = 0;
        if (lastB != null)
          lastClones = Integer.parseInt(lastB);

        String tClones = this.getGlobal("TotalClones");
        int totClones = Integer.parseInt(tClones);

        m_coinHud.addCoins(totClones, totClones + clones-lastClones);

        totClones = totClones + clones - lastClones;

        this.setGlobal("Stage" + m_stage + "Level" + lc + "Clones", lcClones);

        this.setGlobal("LevelCompleted", "-1");
        this.setGlobal("LevelCompletedClones", "-1");
        this.setTotalCloneCount();
        this.saveGame();

        //Gdx.app.debug("MGL","level completed just after save " + lv);
        gameState = 100;
        playerSprite.setVisible(false);
        buggySprite.setVisible(true);
        buggySprite.setFocus(true);

      }
    } else if (gameState == 99)
    {
      m_fadeOutSprite.animate(deltaTime);
      this.setCameraPosition(buggySprite.getX() + buggySprite.getWidth()/2, buggySprite.getY());
      if (stateTime > 0.8f)
      {
        gameState = 100;
        this.loopSound("music", 0.7f);
        //buggySprite.headlightsOn();
        //this.loopSound("music2", 0.0f);
      }
    } else if (gameState == 100)
    {
      //stage selection level
      //update player movement and animation frame.
      inputManager.handleInput();
      m_coinHud.update(deltaTime);
      m_coinHud.animate(deltaTime);

      m_controllerValidTicks++;
      if (m_controllerValidTicks > 30)
      {
        m_controllerValidTicks = 0;
        if (inputManager.isValid() == false)
        {
          gameState = 350;
          m_resumeControllerState = 100;
          return;
        }
      }

      //TouchDetails t = inputManager.getTouch(0);
      //if (t.isDown)
      //{
      //  m_debugTouch.setText("X=" + t.currX + ", Y=" + t.currY);
      //}
      //playerSprite.setUpMovement(inputManager);
      Rectangle cp = playerSprite.getBoundingRectangle();

      playerSprite.setPosition(buggySprite.getX() + buggySprite.getWidth()/2 - 24, buggySprite.getY());
      this.setCameraPosition(buggySprite.getX() + buggySprite.getWidth()/2, buggySprite.getY());

      
      ploc.width = cp.width * 0.2f;
      ploc.x  = cp.x + cp.width * 0.4f;
      ploc.y =  cp.y + 6f;
      ploc.height = cp.height * 0.8f;

      for (GameDrawable door1 : this.getChildren())
      {
        if (door1 instanceof LevelDoor)
        {
          LevelDoor door = (LevelDoor) door1;
          if (Intersector.overlaps(door.getBoundingRectangle(), ploc))
          {
            if (inputManager.isUpPressed())
            {
              //enter level
              if (door.isLocked() == false)
              {
                //enter level
                if (door.isStage())
                {
                  m_stage = door.getLevel();
                } else
                {              
                  m_level = door.getLevel();
                  this.fadeToBlack(0.7f);
                }

                playerSprite.goThroughDoor();
                buggySprite.goThroughDoor();
                gameState = 101;
                stateTime = 0;
              }
            }
          }
        }

      }

      if (!this.isOnScreen(playerSprite.getX()+50, playerSprite.getY()+50))
      {
        playerSprite.setLocationToCheckpoint();
        buggySprite.setLocationToCheckpoint();
      }

      if(inputManager.isSpeedPressed())
      {
        this.replaceActiveLayer(new TitleScreenLayer());
        this.cleanUp();
      }

    } else if (gameState == 101)
    {
      m_fadeOutSprite.animate(deltaTime);
      if (stateTime > 0.8f)
      {
        Gdx.app.debug("MainGameLayer","GameState = 101: stage=" + m_stage + " level = " + m_level);
        this.replaceActiveLayer(new MainGameLayer(m_stage, m_level));
        this.cleanUp();
        if ((m_stage > 0) && (m_level > -1))
          this.stopSound("music");
        else
        {
          this.stopSound("music2");
        }

        return;
      }
    } else if (gameState == 110)
    {
      //completed stage
      m_fadeOutSprite.animate(deltaTime);
      if (stateTime > 1.1f)
      {
        this.replaceActiveLayer(new MainGameLayer(-1,-1));
        this.cleanUp();
        return;
      }
    } else if (gameState == 120) // unlock bonus level
    {
      m_coinHud.update(deltaTime);
      m_coinHud.animate(deltaTime);

      if ((stateTime > 0.25f) && (m_coinHud.isAnimating() == false))
      {
        boolean found = false;
        /*
        for (GameDrawable door1 : this.getChildren())
        {
          if (door1 instanceof LevelDoor)
          {
            LevelDoor door = (LevelDoor) door1;
            //Gdx.app.debug(APP_N, "door found num = " + door.getLevel());
            int l = door.getLevel();
            if (l == 10)
            {
              //found bonus door
              if (door.isLocked())
              {
                found = true;
                this.setCameraPosition(door.getX() + 50, door.getY() + 50, 0.5f);
                break;
              }
            }
          }
        }*/

        if (found)
        {
          gameState = 121;
          stateTime = 0;
        } else
        {
          Gdx.app.debug(APP_N, "DID NOT found door");
          gameState = 100;
        }
      }

    } else if (gameState == 121)
    {
      m_coinHud.update(deltaTime);
      m_coinHud.animate(deltaTime);
      if (stateTime > 0.75f)
      {
        for (GameDrawable door1 : this.getChildren())
        {
          if (door1 instanceof LevelDoor)
          {
            LevelDoor door = (LevelDoor) door1;
            int l = door.getLevel();
            if (l == 11)
            {
              //found bonus door
              if (door.isLocked())
              {
                door.unlock();
                this.setGlobal("Stage" + m_stage + "Level11State", "Unlocked");
                //Gdx.app.debug(APP_N, "found door and unlock stge = " + m_stage);
                this.playSound("bonusDoor", 0.7f);
                break;
              }
            }
          }
        }
        stateTime = 0;
        gameState = 122;
      }

    } else if (gameState == 122)
    {
      if (stateTime > 0.75f)
      {
        gameState = 123;
        stateTime = 0;
        this.setCameraPosition(playerSprite.getX(), playerSprite.getY()+playerSprite.getHeight()/2, 0.5f);
      }
    } else if (gameState == 123)
    {
      if (stateTime > 0.75f)
      {
        stateTime = 0;
        gameState = 100;
      }
    } else if (gameState == 180)
    {
      m_fadeOutSprite.animate(deltaTime);
      if (stateTime > 0.8f)
      {
        gameState = 182;
        stateTime = 0;
        this.playSound("restart", 0.7f);
      }
    } else if (gameState == 182)
    {
      m_lives = (int) (stateTime/0.11f);
      if (m_lives >= 5)
      {
        m_lives = 5;
        gameState = 100;
        this.setGlobal("NumLives", "5");
        this.saveGame();
        playerSprite.setLives(m_lives);
      }

      m_playerHud.setLives(m_lives);

    } else if (gameState == 185)
    {
      gameState = 100;
    } else if (gameState == 300)
    {
      //paused

      if (stateTime > 0.05f)
      {
        inputManager.handleInput();
      }

      m_activeDialog.animate(deltaTime);
      m_activeDialog.update(deltaTime);


    } else if (gameState == 350)
    {
        stateTime = 0;
        this.pauseGame(); 
        this.playSound("menuOpen", 0.8f);
        m_activeDialog = new BaseDialog(m_font32, treeTextures);
        m_fadeOutSprite.setOpacity(0.25f);
        m_activeDialog.setTitle("Controller Lost - Please Connect!");
        m_activeDialog.setVisible(true);
        m_activeDialog.show();
        gameState = 351;
    } else if (gameState == 351)
    {
      if (stateTime > 0.5f)
      {
        stateTime = 0f;
        if (inputManager.isControllerConnected())
        {
          inputManager.reInitialize();
          gameState = 352;
          m_activeDialog.setTitle("Controller Connected - Press A");
        }
      }
      m_activeDialog.animate(deltaTime);
    } else if (gameState == 352)
    {
      inputManager.handleInput();
      if (inputManager.isJumpPressed())
      {
        gameState = m_resumeControllerState;
        this.resumeGame();
        stateTime = 0;
        m_fadeOutSprite.setOpacity(0f);
        m_activeDialog = null;
      }
    } else if (gameState == 1000)
    {
      // UFO Crashes cut scene
      this.pauseGame();
      this.stopSound("music");
      this.stopSound("music2");

      //add UFO to screen and position.
      m_ufo = new TreeSprite(treeTextures, "ufo", 2, 1, 1.0f, "None", this, playerSprite.getX(), playerSprite.getY(), 0.4f);
      m_ufo.setPosition(playerSprite.getX()+ 1800, playerSprite.getY() + 1600);
      this.add(m_ufo, true);

      AnimateMoveTo moveAnimation = new AnimateMoveTo (4.5f, m_ufo.getX(), m_ufo.getY(), 1560, 450); 
      m_ufo.runAnimation(moveAnimation);

      AnimateScaleTo su = new AnimateScaleTo(3.7f, 0.05f, 0.05f, 1.0f, 1.0f);
      m_ufo.runAnimation(su);

      AnimateRotateTo r1 = new AnimateRotateTo(0.25f,8f,-8f, 1);
      AnimateRotateTo r2 = new AnimateRotateTo(0.25f,-8f,8f, 1);
      GameAnimateable[] a = {r1,r2};
      GameAnimateable seq = new GameAnimationSequence(a,9);
      m_ufo.runAnimation(seq);

      this.setCameraPosition(m_ufo.getX()-400, m_ufo.getY()-400, 1.0f);
      this.setCameraZoom(1.25f, 1.0f);

      this.playSound("ufo", 0.3f);

      stateTime = 0f;
      gameState = 1001;
    } else if (gameState == 1001)
    {
      if (stateTime > 1.5f)
      {
        this.setCameraPosition(1560, 450, 2.0f);
        //Gdx.app.debug("TEST", "ufo x = " + m_ufo.getX() + " ufo y = " + m_ufo.getY());
        //Gdx.app.debug("TEST", "camera x = " + m_camera.position.x + " ufo y = " + m_camera.position.y);
        stateTime = 0f;
        gameState = 1002;
      }

    } else if (gameState == 1002)
    {
      if (stateTime > 3.1f)
      {
         this.playSound("crash", 0.45f);
         this.shakeCamera(3.75f);

          stateTime = 0f;
          gameState = 1005;

          m_explosion = new TreeSprite(treeTextures, "explosion", 2, 1, 1.0f, "Point", this, m_ufo.getX(), m_ufo.getY(), 0.4f);
          m_explosion.setPosition(m_ufo.getX(), m_ufo.getY()-80);
          this.add(m_explosion, true);
          AnimateScaleTo s = new AnimateScaleTo(5.0f, 0.5f, 0.5f, 3.45f, 2.3f);
          m_explosion.runAnimation(s);

          AnimateRotateTo ar = new AnimateRotateTo(0.5f, 8f, 85f);
          m_ufo.runAnimation(ar);

          //start flame animation...maybe have screen shake?
      }
    } else if (gameState == 1005)
    {
        if (stateTime > 4.75f)
        {
          // change flame to smokey/green
          this.loopSound("ectoRelease", 0.1f);
          AnimateFadeOut f = new AnimateFadeOut(0.75f);
          m_explosion.runAnimation(f);

          TreeSprite t = new TreeSprite(treeTextures, "smoke", 3, 1, 1.0f, "GreenGlow", this, 0,0, 0.8f);
          t.setPosition(m_ufo.getX()-30, m_ufo.getY()+20);
          t.setOrigin(t.getOriginX(), 0);
          this.add(t, true);


          TreeSprite t2 = new TreeSprite(treeTextures, "smoke", 3, 1, 1.0f, "GreenGlow", this, 0,0, 0.8f);
          t2.setOrigin(t2.getOriginX(), 0);
          t2.setRotation(180);
          t2.setPosition(m_ufo.getX()-103, m_ufo.getY()+30);
          this.add(t2, true);

          AnimateScaleTo g = new AnimateScaleTo(0.6f, 1.25f, 1.25f, 1.25f, 1.5f);
          AnimateScaleTo s = new AnimateScaleTo(0.2f, 1.25f, 1.5f, 1.25f, 1.25f);
          //AnimateRotateTo r = new AnimateRotateTo(f,0f,359f, -1);
          GameAnimateable[] a = {g,s};
          GameAnimateable seq = new GameAnimationSequence(a,-1);
          t.runAnimation(seq);


          AnimateScaleTo g2 = new AnimateScaleTo(0.5f, 1.25f, 1.25f, 1.25f, 1.5f);
          AnimateScaleTo s2 = new AnimateScaleTo(0.4f, 1.25f, 1.5f, 1.25f, 1.25f);
          //AnimateRotateTo r = new AnimateRotateTo(f,0f,359f, -1);
          GameAnimateable[] a2 = {g2,s2};
          GameAnimateable seq2 = new GameAnimationSequence(a2,-1);
          t2.runAnimation(seq2);

          stateTime = 0f;
          gameState = 1010;
        }
    } else if (gameState == 1010)
    {
        if (stateTime > 0.7f)
        {
          m_explosion.setVisible(false);
          gameState = 1011;
          stateTime = 0;
        }
    } else if (gameState == 1011)
    {
        if (stateTime > 1.5f)
        {
          this.setCameraPosition(playerSprite.getX(), playerSprite.getY(), 0.5f);
          this.setCameraZoom(1.0f, 0.5f);
          stateTime = 0f;
          gameState = 1020;
        }
    } else if (gameState == 1020)
    {
      if (stateTime > 0.6f)
      {
        stateTime = 0;
        gameState = 10;
        this.resumeGame();
        this.loopSound("music", 0.7f);
        this.loopSound("music2", 0.0f);
      }
    }

      //start ufo sound

      //move ufo on diagonal path to crash into buildings

      //make crash sound when crashes

      //run smoke animation

      //when smoke clears, show UFO embedded in buildings

      //start green glow animation from ship

      //then pause for 3 seconds

      //then pan and zoom back to normal to AJ

      // continue



    if (enemiesToAdd.size() > 0)
    {
      for (BaseSprite s : enemiesToAdd)
      {
        this.addEnemy(s);
      }

      enemiesToAdd.clear();
    }

    if (m_moon != null)
    {
      float cx = m_camera.position.x;
      float cy = m_camera.position.y;
      float xx = m_halfCameraHeight * 0.2f - 0.2f* cx / m_maxX * m_halfCameraWidth + 270;
      float yy = (m_maxY - cy) / m_maxY * 0.3f * m_halfCameraHeight + 190;
      m_moon.setPosition(cx+ xx, cy + yy);
    } 
  }

  public void handleCollisions()
  {
      boolean buggyActive = false;
    
      if (playerSprite.isVisible())
      {
        Rectangle cp = playerSprite.getBoundingRectangle();
        ploc.width = cp.width * 0.6f;
        ploc.x  = cp.x + cp.width * 0.2f;
        ploc.y =  cp.y + (0.1f * cp.height);
        ploc.height = cp.height * 0.8f;

        cp = buggySprite.getBoundingRectangle();
        bloc.width = cp.width * 0.6f;
        bloc.x  = cp.x + cp.width * 0.2f;
        bloc.y =  cp.y + (0.1f * cp.height);
        bloc.height = cp.height * 0.8f;
        buggyActive = false;

      } else
      {
        Rectangle cp = buggySprite.getBoundingRectangle();
        ploc.width = cp.width * 0.6f;
        ploc.x  = cp.x + cp.width * 0.2f;
        ploc.y =  cp.y + (0.1f * cp.height);
        ploc.height = cp.height * 0.8f;
        buggyActive = true;
      }

      m_playerLoc.set(ploc.x, ploc.y);
      m_soundManager.updateVolumes(m_playerLoc);

      WeaponInterface activeWeapon = playerSprite.getActiveWeapon();

      if (playerSprite.isVisible())
      {
        for (m_iter = 0; m_iter < tutorials.size(); m_iter++)
        {
          m_tutorial = tutorials.get(m_iter);
          if (m_tutorial.isValid() == true)
          {
            if (Intersector.overlaps(m_tutorial.getBoundingRectangle(), ploc))
            {
              m_tutorial.activate(this.getCameraX(), this.getCameraY());
              this.pauseGame();
              playerSprite.showTutorial();
              if (m_tutorial.getSpecial() == 99)
              {
                this.setLivesTo99(m_stage);
              }

            }
          }
        }
      } else
      {
        for (m_iter = 0; m_iter < tutorials.size(); m_iter++)
        {
          m_tutorial = tutorials.get(m_iter);
          if (m_tutorial.isValid() == true)
          {
            if (Intersector.overlaps(m_tutorial.getBoundingRectangle(), buggySprite.getBoundingRectangle()))
            {
              m_tutorial.activate(this.getCameraX(), this.getCameraY());
              this.pauseGame();
              playerSprite.showTutorial();
              if (m_tutorial.getSpecial() == 99)
              {
                this.setLivesTo99(m_stage);
              }
            }
          }
        }        
      }

      
      //if (playerSprite.isCollidable())
      //{
        /*
        for (m_iter = 0; m_iter < checkpoints.size(); m_iter++)
        {
          CheckPointSprite cs = checkpoints.get(m_iter);
          if (Intersector.overlaps(cs.getBoundingRectangle(), ploc))
          {
            if (cs.isCameraChangeEvent())
            {
              this.setCameraZoom(cs.getZoom(), cs.getZoomTime());
              m_playerXOffsetTarget = cs.getOffsetX();
              m_playerYOffsetTarget = cs.getOffsetY();
            } else if (cs.getGameState() > 0) 
            {
              if (cs.getSpecial() != 5000)
              {
                if (cs.isActive())
                {
                  gameState = cs.getGameState();
                  cs.setActive(false);
                }
              }
            } else if (cs.getSpecial() > 0)
            {
              if (cs.getSpecial() == 1)
              {
                //hoverboard!
                playerSprite.stopHover(true);
              } else if (cs.getSpecial() == 2) //light off
              {
                fadeAmbientOut(0.75f);
                //rayHandler.setAmbientLight(0.0075f,0.0075f,0.0075f,0.02f);
              } else if (cs.getSpecial() == 3) //light on
              {
                fadeAmbientIn(0.75f);
                //this.resetAmbientLight();
              } else if (cs.getSpecial() == 4)
              {
                playerSprite.dieNow();
              }
            } else
            {
              playerSprite.setCheckpoint(cs.getX(), cs.getY());
            }
          }
        }
        */
        

        for (m_iter = 0; m_iter < collectibles.size(); m_iter++)
        {
          CollectibleSprite cs = collectibles.get(m_iter);
          if (cs.isCollidable())
          {
            if (Intersector.overlaps(cs.getBoundingRectangle(), ploc))
            {
              cs.collect();
              if (cs.getType() == 0)
              {
                // One Energy Power Up
                playerSprite.addEnergy(1);
                m_playerHud.setEnergy(playerSprite.getEnergy());
              } else if (cs.getType() == 1)
              {
                playerSprite.addLives(1);
                m_playerHud.setLives(playerSprite.getLives());
              }
            }
          }
        }

      if (playerSprite.isVisible())
      {
        activeWeapon.didCollide(this, playerSprite);
      }

      for (m_iter = 0; m_iter < enemies.size(); m_iter++)
      {
        BaseSprite enemy = enemies.get(m_iter);
        //collisions
        if (enemy.isCollidable())
        {
           enemy.playSpriteSound();

           activeWeapon.didCollide(this, enemy);
        
          //hit player
          boolean collision = false;
          if (enemy.isCircle())
          {
            collision = Intersector.overlaps(enemy.getBoundingCircle(), ploc);
          } else
          {
            collision = Intersector.overlaps(enemy.getBoundingRectangle(), ploc);
          }

          if (collision)
          {
            //player collided with enemty
            if (enemy.isCollidable())
            {
              if (buggyActive)
              {
                if (buggySprite.isCollidable())
                {
                  if (buggySprite.hitByAttack(enemy))
                  {
                    if (enemy instanceof SlimeSprite)
                    {
                      SlimeSprite ss = (SlimeSprite) enemy;
                      ss.die();
                    }
                  }
                }
              } else
              {
                if (playerSprite.isCollidable())
                {
                  if (playerSprite.hitByAttack(enemy))
                  {
                    enemy.hitPlayer(playerSprite);
                    if (enemy instanceof SlimeSprite)
                    {
                      SlimeSprite ss = (SlimeSprite) enemy;
                      ss.die();
                    }
                    //m_playerHud.setEnergy(playerSprite.getEnergy());
                  }
                }
              } 
            }
          }
        }
      }

      if (buggyActive == false)
      {

        for (m_iter = 0; m_iter < enemies.size(); m_iter++)
        {
          BaseSprite enemy = enemies.get(m_iter);
          //collisions
          if (enemy.isCollidable())
          {
             enemy.playSpriteSound();
          
            //hit player
            boolean collision = false;
            if (enemy.isCircle())
            {
              collision = Intersector.overlaps(enemy.getBoundingCircle(), bloc);
            } else
            {
              collision = Intersector.overlaps(enemy.getBoundingRectangle(), bloc);
            }

            if (collision)
            {
              //player collided with enemty
              if (enemy.isCollidable())
              {
                  if (buggySprite.isCollidable())
                  {
                    if (buggySprite.hitByAttack(enemy))
                    {
                      if (enemy instanceof SlimeSprite)
                      {
                        SlimeSprite ss = (SlimeSprite) enemy;
                        ss.die();
                      }
                    }
                  }
              }
            }
          }
        }
      }

      m_playerHud.setEnergy(playerSprite.getEnergy());

      levelComplete = false;

      m_noClonesLeft = true;

      if (m_level == 0)
        m_noClonesLeft = false;

      for (m_iter = 0; m_iter < checkpoints.size(); m_iter++)
      {
        CheckPointSprite cs = checkpoints.get(m_iter);

        if ((cs.getGameState() > 0) && (cs.getSpecial() == 5000) && (cs.isVisible()))
          m_noClonesLeft = false;

        if (Intersector.overlaps(cs.getBoundingRectangle(), buggySprite.getBoundingRectangle()))
        {
          if (cs.getGameState() > 0) 
          {
            if (cs.getSpecial() == 5000)
            {
              if (cs.isActive())
              {
                gameState = cs.getGameState();
                cs.win();
                m_clonesSaved = this.countClones();
                cs.setActive(false);
              }
            } else
            {
              gameState = cs.getGameState();
              cs.setActive(false);
            }
          }
        }
      }

      if ((levelComplete) || (inputManager.isTestPressed()))
      {


        this.pauseGame();

        gameState = 20;

        this.stopSound("platformOn");
        this.stopSound("platformOff");
        this.stopSound("heartbeat");

        stateTime = 0f;
      } 
  }

  @Override
  protected void preCustomDraw()
  {
    m_spriteBatch.setProjectionMatrix(m_defaultMatrix);
    m_spriteBatch.begin();
    m_backSprite.draw(m_spriteBatch);
    m_spriteBatch.end();

    if (m_lightning != null)
    {
      if (m_lightning.isVisible())
      {
        m_spriteBatch.setProjectionMatrix(m_camera.combined);
        m_spriteBatch.begin();
        m_lightning.draw(m_spriteBatch);
        m_spriteBatch.end();
      }
    }

    if (m_moon != null)
    {
      if (m_moon.isVisible())
      {
        m_spriteBatch.setProjectionMatrix(m_camera.combined);
        m_spriteBatch.begin();
        m_moon.draw(m_spriteBatch);
        m_spriteBatch.end();
      }
    }

    super.drawBackSprites();

    if (tiledMap != null)
      tiledMap.draw(); // will only draw if visible in implementation
    //m_spriteBatch.begin();
    //m_font24.draw(m_spriteBatch, "24 Test", 25, 64);
   // m_spriteBatch.end();
  }

  public void enterBuggyShiftCamera()
  {
    float xDiff = playerSprite.getX() + playerSprite.getWidth()/2 - buggySprite.getX() - buggySprite.getWidth()/2;
    float yDiff = playerSprite.getY() - buggySprite.getY();

    m_playerYOffset = yDiff;
    m_playerXOffset = xDiff;

    m_playerXOffsetTarget = 0;
    m_playerYOffsetTarget = 0;

  }

  public void setPlayerXOffsetTarget(float offset)
  {
    m_playerXOffsetTarget = offset;
  }

  public float getPlayerXOffsetTarget()
  {
    return m_playerXOffsetTarget;
  }

  public void buggyDieWithoutFocus()
  {
      Gdx.app.debug("XXX","Buggy Dying *************");
      buggySprite.setFocus(true);
      playerSprite.buggyDying();
      this.enterBuggyShiftCamera();
      m_buggyDying = true;
  }

  @Override
  protected void postCustomDraw()
  {

    if (rayHandler != null)
    {
      rayHandler.setCombinedMatrix(m_camera.combined);
      rayHandler.updateAndRender();
    } 

    //debugMatrix = m_spriteBatch.getProjectionMatrix().cpy().scale(1,1, 0);
    //debugRenderer.render(world, debugMatrix);

    m_spriteBatch.setProjectionMatrix(m_defaultMatrix);
    m_spriteBatch.begin();
    m_playerHud.draw(m_spriteBatch);
    if (m_levelWonHud.isVisible())
      m_levelWonHud.draw(m_spriteBatch);

    m_coinHud.draw(m_spriteBatch);
    m_levelTitle.draw(m_spriteBatch);
    gameOverPanel.draw(m_spriteBatch);
    //getReadyPanel.draw(m_spriteBatch);
    m_selectTitle.draw(m_spriteBatch);


    if (m_greatSprite.isVisible())
    {
      m_greatSprite.draw(m_spriteBatch);
    }

    if (m_unlockSprite.isVisible())
    {
      m_unlockSprite.draw(m_spriteBatch);
    }

    if (dpad.isVisible())
    {
      dpad.draw(m_spriteBatch);
      //m_debugTouch.draw(m_spriteBatch);
    }

    if (m_fadeOutSprite.getOpacity() > 0)
      m_fadeOutSprite.draw(m_spriteBatch);

    if (m_activeDialog != null)
    {
      if (m_activeDialog.isVisible())
        m_activeDialog.draw(m_spriteBatch);
    }

    m_spriteBatch.end();

  }

  public void wonLevel()
  {
      this.stopSound("footsteps");
      this.stopSound("buggyDrive");
      this.stopSound("heartbeat");
      this.stopSound("countdown");
      this.stopSound("movingBlock1");
      this.stopSound("bombFuse");

      m_replayLevel = m_level;
      m_level++;

      //update game status
      //unlock next door

      if (m_level < 11)
      {
        this.setGlobal("Stage" + m_stage + "Level" + m_level + "State", "Unlocked");
      }

      this.setGlobal("NumLives", "" + playerSprite.getLives());

      this.setGlobal("LevelCompleted", "" + (m_level-1));
      this.setGlobal("LevelCompletedClones", "" + m_clonesSaved);

      m_mission2Unlocked = false;
      if (m_level == 11) //one greater than level completed - remember!
      {
        //unlock mission 2
        if (m_stage == 1)
        {
          String stageLocked = this.getGlobal("Stage" + (m_stage+1) + "State");
          
          if (stageLocked.equals("Locked"))
          {
            m_mission2Unlocked = true;
          }
        }
        this.setGlobal("Stage" + (m_stage+1) + "State", "Unlocked");
      }
      
      if (m_level > 1)
      {
        String lastB = this.getGlobal("Stage" + m_stage + "Level" + (m_level-1) + "Clones");
        int lastClones = 0;
        if (lastB != null)
          lastClones = Integer.parseInt(lastB);

        if (m_clonesSaved > lastClones)
        {
          this.setGlobal("Stage" + m_stage + "Level" + (m_level-1) + "Clones", "" + m_clonesSaved);
          this.setGlobal("LevelCompletedClones", "-1");
        }
        
        this.setTotalCloneCount();
      } 

      this.saveGame();

      m_goBackToStageSelect = false;

      if ((this.testBonusLevel(m_stage)) && (m_level > 1))
      {
        //TODO: some kind of thing like super great...and show unlocked bonus level!
        this.setGlobal("Stage" + m_stage + "Level11State", "Unlocked");
        m_level = 11; //force goto bonus level automatically
        this.saveGame();
      } else
      {
        if (m_level == 11)
        {
          String stat = this.getGlobal("Stage" + m_stage + "Level" + m_level + "State");
          if (stat.equals("Unlocked"))
          {
            //goto bonus level, all good.
          } else
          {
            //if they click next, we want to goto stage select
            m_goBackToStageSelect = true;
          }
        } else if (m_level == 12)
        {
          //completed bonus level
          //if they click next, we want to goto stage select
          m_goBackToStageSelect = true;
        }
      }
  }

  public boolean checkIfWonGame()
  {
    String bs = this.getGlobal("WonGame");
    if (bs != null)
    {
      if (bs.equals("YES"))
      {
        return false;
      }
    }

    this.setTotalCloneCount();
    String tb = this.getGlobal("TotalClones");
    int tbi = 0;
    if (tb != null)
    {
      tbi = Integer.parseInt(tb);
    }

    if (tbi >= 66)
      return true;

    return false;

  }

  public void wonGame()
  {
      this.stopSound("footsteps");
      this.stopSound("buggyDrive");
      this.stopSound("heartbeat");
      this.stopSound("countdown");
      this.stopSound("movingBlock1");
      this.stopSound("bombFuse");

      this.setGlobal("WonGame", "YES");
      this.saveGame();
  }

  public void loadLevel(int stage, int lv)
  {

    Gdx.app.debug(APP_N, "Load Level Start: " + stage + " - " + lv);

    this.setTotalCloneCount();
    boolean ufoBoss = false;
    m_buggyDying = false;

    this.removeAll();
    m_ambientFadeDone = true;
    GameSprite skullSprite = null;

    //any possible looping sounds good to stop here!
    this.stopSound("movingBlock1");
    this.stopSound("bombFuse");
    this.stopSound("alarm");
    this.stopSound("footsteps");
    this.stopSound("buggyDrive");

    m_ClonesCollected = 0;

    if (rayHandler != null)
    {
      rayHandler.removeAll();
    }

    if (world != null)
    {
      for (Body b : m_bodies)
      {
        world.destroyBody(b);
      }
      m_bodies.clear();
    }

    if (world == null)
      world = new World(new Vector2(0, 0), true);
    //debugRenderer = new Box2DDebugRenderer();

    if (rayHandler == null)
    {
      rayHandler = new RayHandler(world);
      rayHandler.useDiffuseLight(false);
    }


    if (m_stageSelectionLevel)
    {
      this.setMusic("99C_level_select_BGM.ogg");
    } else
    {
      this.setMusic("99C_gameplay_BGM_base_stem.ogg");
      this.setMusic("99C_gameplay_BGM_clone_stem.ogg", 0);
    } 


    if (m_backTextureCode != 3)
      {
        safeDispose(m_backTexture);
        m_backTexture = new Texture("stars_back.png");
        m_backTextureCode = 3;
        m_backSprite.setTexture(m_backTexture);
      }

      /*
    if ((m_stage == 4) && (m_level == 9))
    {
      if (m_backTextureCode != 3)
      {
        safeDispose(m_backTexture);
        m_backTexture = new Texture("stars_back.png");
        m_backTextureCode = 3;
        m_backSprite.setTexture(m_backTexture);
      }
    } else if (m_stage != 2)
    {
      if (m_backTextureCode != 1)
      {
        safeDispose(m_backTexture);
        m_backTexture = new Texture("level_back.png");
        m_backTextureCode = 1;
        m_backSprite.setTexture(m_backTexture);
      }
    } else 
    {
      if (m_backTextureCode != 2)
      {
        safeDispose(m_backTexture);
        m_backTexture = new Texture("stage2_back.png");
        m_backTextureCode = 2;
        m_backSprite.setTexture(m_backTexture);
      }
    } */

    if (tiledMap != null)
    {
      tiledMap.dispose();
    }

    tiledMap = null;
    //if (playerSprite != null)
    //{
    //  m_lives = playerSprite.getLives();
    //} else
    //{
    //  m_lives = 5;
    //}

    m_lives = Integer.parseInt(this.getGlobal("NumLives"));
    m_playerHud.setLives(m_lives);
    Gdx.app.debug(APP_N, "Load Level Start: " + stage + " - " + lv);

    playerSprite = null;
    buggySprite = null;

    if (!m_stageSelectionLevel)
    {
      tiledMap = new GameTileMap("Level_" + stage + "-" + lv + ".tmx", m_camera);
    } else
    {
      if (m_stage < 0)
      {
        tiledMap = new GameTileMap("StagePicker.tmx", m_camera);
      } else
      {
        tiledMap = new GameTileMap("stage" + m_stage + ".tmx", m_camera);
      }
    }

    Gdx.app.debug(APP_N, "Load Level NEXT: " + stage + " - " + lv);


    MapProperties mapProps = tiledMap.m_tiledMap.getProperties();
    String title = (String) mapProps.get("Title");

    String levelType = "Regular";
    if (mapProps.containsKey("LevelType"))
      levelType = (String) mapProps.get("LevelType");

    String lighting = "Regular";
    if (mapProps.containsKey("Lighting"))
      lighting = (String) mapProps.get("Lighting");

    if (lighting.equals("Black"))
    {
      if (inputManager.m_touchDevice) 
      {
        rayHandler.setAmbientLight(0.02f, 0.02f, 0.02f,0.2f);
        m_ra = 0.02f;
        m_ga = 0.02f;
        m_ba = 0.02f;
        m_aa = 0.2f;
      } else
      {
        rayHandler.setAmbientLight(0.02f, 0.02f, 0.02f,0.2f);
        m_ra = 0.02f;
        m_ga = 0.02f;
        m_ba = 0.02f;
        m_aa = 0.2f;
      }
    } else if (lighting.equals("Dark"))
    {
      if (inputManager.m_touchDevice) 
      {
        rayHandler.setAmbientLight(0.06f, 0.06f, 0.06f,0.6f);
        m_ra = 0.06f;
        m_ga = 0.06f;
        m_ba = 0.06f;
        m_aa = 0.6f;
      } else
      {
        rayHandler.setAmbientLight(0.03f, 0.03f, 0.03f,0.25f);
        m_ra = 0.03f;
        m_ga = 0.03f;
        m_ba = 0.03f;
        m_aa = 0.25f;
      }
    } else if (lighting.equals("Dusk"))
    {
      if (inputManager.m_touchDevice) 
      {
        rayHandler.setAmbientLight(0.08f, 0.08f, 0.08f,0.8f);
        m_ra = 0.08f;
        m_ga = 0.08f;
        m_ba = 0.08f;
        m_aa = 0.8f;
      } else
      {
        rayHandler.setAmbientLight(0.04f, 0.04f, 0.04f,0.56f);
        m_ra = 0.04f;
        m_ga = 0.04f;
        m_ba = 0.04f;
        m_aa = 0.56f;
      }
    } else
    {
      if (inputManager.m_touchDevice)
      {
        rayHandler.setAmbientLight(0.1f, 0.1f, 0.1f,0.85f);
        m_ra = 0.1f;
        m_ga = 0.1f;
        m_ba = 0.1f;
        m_aa = 0.85f;
      } else
      {
        rayHandler.setAmbientLight(0.052f, 0.052f, 0.052f,0.76f);
        m_ra = 0.052f;
        m_ga = 0.052f;
        m_ba = 0.052f;
        m_aa = 0.76f;
      }
    }

    m_currA = m_aa;
    m_ambientFadeDone = true;

    //tiledMap = new GameTileMap("level_test.tmx", m_camera);
    m_mapWidth = tiledMap.getMapWidth();
    m_mapHeight = tiledMap.getMapHeight();

    tw = (float) tiledMap.getTilePixelWidth();
    th = (float) tiledMap.getTilePixelHeight();

    stateTime = 0;
    platforms = new ArrayList<PlatformSprite>();

    int np = 20; // 15 peppers to start
    
    TiledMapTileLayer pLayer = (TiledMapTileLayer) tiledMap.m_tiledMap.getLayers().get("platforms");
    TiledMapTileLayer lLayer = (TiledMapTileLayer) tiledMap.m_tiledMap.getLayers().get("ladders");
    MapLayer objectsLayer = (MapLayer) tiledMap.m_tiledMap.getLayers().get("objects");
    MapObjects mapObjects = objectsLayer.getObjects();

    if (myTextures == null)
      myTextures = new TextureAtlas("player_tiles.txt");
    

    playerSprite = new PlayerSprite(gameSpritesTextures, pLayer, lLayer, platforms, inputManager, m_lives, world, this);
    buggySprite = new  BuggySprite(gameSpritesTextures, pLayer, lLayer, platforms, inputManager, m_lives, world, this);

    m_bombWeapon = new BombWeaponSprite(gameSpritesTextures, playerSprite, this);
    playerSprite.setActiveWeapon(m_bombWeapon);

    m_isSpeedLevel = false;

    if (levelType.equals("Speed"))
    {
      playerSprite.setSpeed(1.25f);
      m_isSpeedLevel = true;
    } 


    m_levelTitle.setTitle(title);
    m_selectTitle.setText(title);

    //pepper = new PepperSprite(myTextures,playerSprite, np);

    enemies = new ArrayList<BaseSprite>();
    tutorials = new ArrayList<TutorialSprite>();
    checkpoints = new ArrayList<CheckPointSprite>();
    collectibles = new ArrayList<CollectibleSprite>();
    enemiesToAdd = new ArrayList<BaseSprite>();

    ArrayList<GameSprite> frontSprites = new ArrayList<GameSprite>();
    
    boolean levelCameraSet = false;

    //blocks = new ArrayList<BlockSprite>();
   //collectibles = new ArrayList<CollectibleSprite>();

    //set player start
    MapObject playerStart = mapObjects.get("CheckPoint1");
    float px = this.getFloat("x", playerStart);
    float py = this.getFloat("y", playerStart);
    playerSprite.setPosition(px,py);
    playerSprite.setCheckpoint(px,py);
    buggySprite.setPosition(px,py);
    buggySprite.setCheckpoint(px,py);
    float pw = 1;
    float ph = 1;

    for (MapObject obj : mapObjects)
    {
      //Gdx.app.debug(APP_N, "Map Object Iterate");
      MapProperties p = obj.getProperties();
      String t = (String) p.get("type");
      //Gdx.app.debug(APP_N, "type = " + t);
      px = this.getFloat("x", obj);
      py = this.getFloat("y", obj);
      pw = this.getFloat("width", obj);
      //Gdx.app.debug(APP_N, "x = " + px + " y= " + py);
      int w_tiles = (int) (pw/tw);

       if (t.equals("Tree"))
       {
          String baseName = "tree";
          int numFrames = 1;
          int dir = 1;
          float scale = 1.0f;
          String light = "none";
          float as = 0;
          float spx = 1.0f;

          if (p.containsKey("startDir"))
          {
            dir = this.getStrToInt("startDir", obj);
          }

          if (p.containsKey("Base"))
          {
            baseName = (String) p.get("Base");
          }

          if (p.containsKey("NumFrames"))
          {
            numFrames = this.getStrToInt("NumFrames", obj);
          }

          if (p.containsKey("Scale"))
          {
            scale = this.getStrToFloat("Scale", obj);
          }

          if (p.containsKey("speed"))
          {
            spx = this.getStrToFloat("speed", obj);
          }

          if (p.containsKey("Light"))
          {
            light =  (String) p.get("Light");
          }

          boolean back = true;
          boolean front = false;
          if (p.containsKey("Order"))
          {
            String order =  (String) p.get("Order");
            if (order.equals("Front"))
            {
              front = true;
              back = false;
            }
          }

          if (p.containsKey("AnimationSpeed"))
          {
            as = this.getStrToFloat("AnimationSpeed", obj);
          }

          float lr = 1.0f;
          if (p.containsKey("LightRange"))
          {
            lr = this.getStrToFloat("LightRange", obj);
          }

          int switchCode = -1;
          if (p.containsKey("Switch"))
          {
            switchCode= this.getStrToInt("Switch", obj);
            //Gdx.app.debug(APP_N, "SwitchCode = " + switchCode);
          }

          int startState = 0;
          if (p.containsKey("StartState"))
          {
            startState = this.getStrToInt("StartState", obj);
          }

          float xxx = px;
          for (int i = 0; i < w_tiles; i++)
          {
            TreeSprite enemy = new TreeSprite(treeTextures, baseName, numFrames, dir, scale, light, this, xxx, py, as, lr);
            enemy.setPosition(xxx,py);
            if (!front)
            {
              this.add(enemy, back);
            } else
            {
              frontSprites.add(enemy);
            }

            enemy.setPlayer(playerSprite);
            if (spx != 1.0f)
            {
              enemy.setSpeed(spx);
            }
            
            if (switchCode >= 0)
            {
              for (GameDrawable dd : getChildren())
              {
                if (dd instanceof SwitchSprite)
                {
                  SwitchSprite ss = (SwitchSprite) dd;
                  if (ss.getCode() == switchCode)
                  {
                    enemy.setSwitch(ss, startState);
                  }
                }
              }
            }

            xxx += tw;
          }
       } else if (t.equals("Switch"))
       {

        int code = 0;
        if (p.containsKey("Code"))
        {
          code = this.getStrToInt("Code", obj);
        }

        int levelReset = 0;
        if (p.containsKey("LevelReset"))
        {
          levelReset = this.getStrToInt("LevelReset", obj);
        }

        int st = 0;
        if (p.containsKey("State"))
        {
          st = this.getStrToInt("State", obj);
        }

        int sd = 1;
        if (p.containsKey("startDir"))
        {
          sd = this.getStrToInt("startDir", obj);
        }

        SwitchSprite enemy = new SwitchSprite(treeTextures, playerSprite, st,code, levelReset,sd);
        enemy.setPosition(px,py);
        this.add(enemy);

       }
    }
    
    int bIndex = 1;
    //Gdx.app.debug(APP_N, "About to iterate MapObjects");
    for (MapObject obj : mapObjects)
    {
      //Gdx.app.debug(APP_N, "Map Object Iterate");
      MapProperties p = obj.getProperties();
      String t = (String) p.get("type");
      //Gdx.app.debug(APP_N, "type = " + t);
      px = this.getFloat("x", obj);
      py = this.getFloat("y", obj);
      pw = this.getFloat("width", obj);
      ph = this.getFloat("height", obj);
      //Gdx.app.debug(APP_N, "width = " + pw);
      int w_tiles = (int) (pw/tw);

      //
      Gdx.app.debug(APP_N, "2");

       if (t.equals("Slime"))
       {
          float sp = 1.0f;
          if (p.containsKey("Speed"))
          {
            sp = this.getStrToFloat("Speed", obj);
          }

          float sc = 1.0f;
          if (p.containsKey("Scale"))
          {
            sc = this.getStrToFloat("Scale", obj);
          }

          int vertical = 0;
          if (p.containsKey("Vertical"))
          {
            String v = (String) p.get("Vertical");
            if (v.equals("R"))
            {
              vertical = 1;
            } else
            {
              vertical = -1;
            }
          }

          int trigger = 0;
          if (p.containsKey("Trigger"))
          {
            trigger = this.getStrToInt("Trigger", obj);
          }

          SlimeSprite enemy = new SlimeSprite(gameSpritesTextures,pLayer,lLayer, sp, this, vertical,sc);
          enemy.setSpeed(sp);
          enemy.setPlayer(playerSprite);

          enemy.setPosition(px + vertical * 6,py);
          enemy.setStartDirection(this.getStrToFloat("StartDir", obj));
          enemy.setDirection(this.getStrToFloat("StartDir", obj));

          if (trigger >= 0)
          {
            enemy.setTrigger(trigger);
          }

          this.add(enemy);
          enemies.add(enemy);
          enemy.setPlayer(playerSprite);

       } else if (t.equals("Seeker"))
       {

          float sp = 1.0f;
          if (p.containsKey("Speed"))
          {
            sp = this.getStrToFloat("Speed", obj);
          }

          int trigger = 0;
          if (p.containsKey("Trigger"))
          {
            trigger = this.getStrToInt("Trigger", obj);
          }

          Seeker enemy = new Seeker(gameSpritesTextures,pLayer,lLayer, playerSprite, this);
          enemy.setPosition(px ,py);
          enemy.setPlayer(playerSprite);

          if (trigger >= 0)
          {
            enemy.setTrigger(trigger);
          }

          enemies.add(enemy);
          this.add(enemy);

       } else if (t.equals("SlimeBig"))
       {
          float sp = 1.0f;
          if (p.containsKey("Speed"))
          {
            sp = this.getStrToFloat("Speed", obj);
          }

          float sc = 1.0f;
          if (p.containsKey("Scale"))
          {
            sc = this.getStrToFloat("Scale", obj);
          }

          int vertical = 0;
          if (p.containsKey("Vertical"))
          {
            String v = (String) p.get("Vertical");
            if (v.equals("R"))
            {
              vertical = 1;
            } else
            {
              vertical = -1;
            }
          }

          int trigger = 0;
          if (p.containsKey("Trigger"))
          {
            trigger = this.getStrToInt("Trigger", obj);
          }

          int dt = 0;
          if (p.containsKey("StartDelay"))
          {
            dt = (int)(this.getStrToFloat("StartDelay", obj) * 60.0f);
          }


          SlimeSpriteBig enemy = new SlimeSpriteBig(gameSpritesTextures,pLayer,lLayer, sp, this, vertical,sc,dt);
          enemy.setSpeed(sp);
          enemy.setPlayer(playerSprite);

          enemy.setPosition(px + vertical * 6,py);
          enemy.setStartDirection(this.getStrToFloat("StartDir", obj));
          enemy.setDirection(this.getStrToFloat("StartDir", obj));

          if (trigger >= 0)
          {
            enemy.setTrigger(trigger);
          }

          this.add(enemy);
          enemies.add(enemy);
          enemy.setPlayer(playerSprite);

       }  else if (t.equals("MovingBlock"))
       {
          float scale = 1.0f;
          float xxx = px;
          float speed = 1.0f;
          int trigger = -1;

          if (p.containsKey("Scale"))
          {
            scale = this.getStrToFloat("Scale", obj);
          }

          if (p.containsKey("Speed"))
          {
            speed = this.getStrToFloat("Speed", obj);
          }

          int startDir = -5;
          if (p.containsKey("StartDir"))
          {
            startDir = this.getStrToInt("StartDir", obj);
          }

          if (p.containsKey("Trigger"))
          {
            trigger = this.getStrToInt("Trigger", obj);
          }

          int switchCode = -1;

          if (p.containsKey("Switch"))
          {
            switchCode = this.getStrToInt("Switch", obj);
          }

          int ignoreDie = -1;
          if (p.containsKey("IgnoreDie"))
          {
            ignoreDie = this.getStrToInt("IgnoreDie", obj);
          }

          int ignoreReverse = -1;
          if (p.containsKey("IgnoreReverse"))
          {
            ignoreReverse = this.getStrToInt("IgnoreReverse", obj);
          }

          int startState = 0;
          if (p.containsKey("StartState"))
          {
            startState = this.getStrToInt("StartState", obj);
          }

          boolean back = false;
          if (p.containsKey("Order"))
          {

            String ar = (String) p.get("Order");
            if (ar.equals("Back"))
            {
              back = true;
            }
          }

          String move = "Static";

          if (p.containsKey("Move"))
          {
            move = (String) p.get("Move");
          }

          float pt = 1.0f;
          float pb = 1.0f;
          if (p.containsKey("PauseTop"))
          {
            pt = this.getStrToFloat("PauseTop", obj);
          }

          if (p.containsKey("PauseBottom"))
          {
            pb = this.getStrToFloat("PauseBottom", obj);
          }

          for (int i = 0; i < w_tiles; i++)
          {

            MovingBlock enemy = new MovingBlock(gameSpritesTextures,pLayer,lLayer, scale, move, speed, this, pt, pb);
            enemy.setPosition(xxx,py);
            enemy.setStartPosition(xxx,py);
            xxx += tw;
            this.add(enemy, back);
            enemies.add(enemy);
            enemy.setPlayer(playerSprite);
            if (trigger >= 0)
            {
              enemy.setTrigger(trigger);
            }
            if (startDir >= -1)
            {
              enemy.setStartDirection(startDir);
            }
            if (ignoreDie == 1)
            {
              enemy.setIgnoreDie();
            }

            if (ignoreReverse == 1)
            {
              enemy.setIgnoreReverse();
            }

            if (switchCode >= 0)
            {
              for (GameDrawable dd : getChildren())
              {
                if (dd instanceof SwitchSprite)
                {
                  SwitchSprite ss = (SwitchSprite) dd;
                  if (ss.getCode() == switchCode)
                  {
                    enemy.setSwitch(ss, startState);
                  }
                }
              }
            }
          }
       } else if (t.equals("LevelDoor"))
       {
          Gdx.app.debug(APP_N, "adding level door");
          String dt = (String) p.get("doorType");

          String ltitle = (String) p.get("title");
          int level = this.getStrToInt("level", obj);
          boolean isStage = false;
          if (dt.equals("stage"))
          {
            isStage = true;
          }

          //Gdx.app.debug(APP_N, "adding level door stage= " + stage + " level="  + level);

          LevelDoor s = new LevelDoor(myTextures, treeTextures, isStage, level, this, m_font16, ltitle);
          s.setPosition(px,py);

          if (isStage)
          {
            String stageState = this.getGlobal("Stage" + level + "State");
            if ((stageState == null) || (stageState.equals("Unlocked")))
              s.unlock();
          } else
          {
            if (level >= 1) //level 0 is special, only use if not a real level (cut scene like cemetary)
            {
              String levelState = this.getGlobal("Stage" + stage + "Level" + level + "State");
              if (levelState.equals("Unlocked"))
              {
                s.unlock();
                try
                {
                  String levelClones = this.getGlobal("Stage" + stage+ "Level" + level + "Clones");
                  s.setClones(Integer.parseInt(levelClones));
                } catch (Exception e)
                {
                  s.setClones(0);
                }
              }
            } else
            {
              //exit
              s.unlock();
              s.setClones(-1);
            }
          }

          this.add(s);

           Gdx.app.debug(APP_N, "2c");
       } else if (t.equals("LevelDoorNO"))
       {
          Gdx.app.debug(APP_N, "adding level door NO");
          String dt = (String) p.get("doorType");

          String ltitle = (String) p.get("title");
          int level = this.getStrToInt("level", obj);
          boolean isStage = false;
          if (dt.equals("stage"))
          {
            isStage = true;
          }

          //Gdx.app.debug(APP_N, "adding level door stage= " + stage + " level="  + level);

          LevelDoorNO s = new LevelDoorNO(myTextures, treeTextures, isStage, level, this, m_font16, ltitle);
          s.setPosition(px,py);
          this.add(s);
       } else if (t.equals("CheckPoint"))
       {
          int state = -1;

          if (p.containsKey("State"))
          {
            state = this.getStrToInt("State", obj);
          }

          CheckPointSprite cs = new CheckPointSprite(myTextures,  px, py, state);
          cs.setPosition(px,py);
          this.add(cs);
          checkpoints.add(cs);

          //enemies.add(enemy); 
       } else if (t.equals("LevelStartCamera"))
       {
          levelCameraSet = true;
          this.setCameraPosition(px,py);
          m_levelXStart = px;
          m_levelYStart = py;
       } else if (t.equals("Collectible"))
       {
          String cType = (String) p.get("CollectType");
          CollectibleSprite cs = new CollectibleSprite(myTextures, cType);
          cs.setPosition(px,py);
          this.add(cs);
          collectibles.add(cs);
       } else if (t.equals("Platform"))
       {
          String mv = (String) p.get("move");

          float sp = 1.0f;
          float onDT = -1f;
          float offDT = -1f;
          int groupId = -1;
          int trigger = 0;
          float delayT = 0;
          String spriteName = "moving_platform";

          if (p.containsKey("speed"))
          {
            sp = this.getStrToFloat("speed", obj);
          }

          if (p.containsKey("trigger"))
          {
            trigger = this.getStrToInt("trigger", obj);
          }

          if (p.containsKey("group"))
          {
            groupId = this.getStrToInt("group", obj);
          }

          if (p.containsKey("off"))
          {
            offDT = this.getStrToFloat("off", obj);
          }

          if (p.containsKey("on"))
          {
            onDT = this.getStrToFloat("on", obj);
          }

          if (p.containsKey("delay"))
          {
            delayT = this.getStrToFloat("delay", obj);
          }

          if (p.containsKey("sprite"))
          {
            spriteName = (String) p.get("sprite");
          }

          PlatformSprite cs = null;

          if (p.containsKey("LightRange"))
          {
            float lr  = this.getStrToFloat("LightRange", obj);
            cs = new PlatformSprite(myTextures, pLayer,lLayer, mv, sp, onDT, offDT, delayT, trigger, groupId, playerSprite, spriteName, lr, this);
            cs.setPosition(px,py);
            cs.setStartPosition(px,py);
          } else
          {
            cs = new PlatformSprite(myTextures, pLayer,lLayer, mv, sp, onDT, offDT, delayT, trigger, groupId, playerSprite, spriteName);
            cs.setPosition(px,py);
            cs.setStartPosition(px,py);
          }

          if (p.containsKey("startDir"))
          {
            float dir = this.getStrToFloat("startDir", obj);
            cs.setDirection(dir);
            cs.setStartDirection(dir);
          }

          this.add(cs);
          platforms.add(cs);
       } else if (t.equals("PlatformReverse"))
       {
          SpecialBehaviourSprite sb = new SpecialBehaviourSprite(myTextures, 0);
          sb.setPosition(px,py);
          this.add(sb);
       }  else if (t.equals("PlatformReverseStop"))
       {
          SpecialBehaviourSprite sb = new SpecialBehaviourSprite(myTextures, 3);
          sb.setPosition(px,py);
          this.add(sb);
       } else if (t.equals("EnemyDie"))
       {

          for (int i = 0; i < w_tiles; i++)
          {
            SpecialBehaviourSprite sb = new SpecialBehaviourSprite(myTextures, 1);
            sb.setPosition(px+(i*tw),py);
            this.add(sb);
            //Gdx.app.debug(APP_N, "Enemy Die added at: " + (px+(w_tiles*tw)));
          }
       } else if (t.equals("CameraChange"))
       {
          float delayT = 0.75f;
          float zoom = 1.0f;
          float offsetX = 0;
          float offsetY = 0;

          if (p.containsKey("delay"))
          {
            delayT = this.getStrToFloat("delay", obj);
          }
          if (p.containsKey("zoom"))
          {
            zoom = this.getStrToFloat("zoom", obj);
          }
          if (p.containsKey("offsetX"))
          {
            offsetX = this.getStrToFloat("offsetX", obj);
          }

          if (p.containsKey("offsetY"))
          {
            offsetY = this.getStrToFloat("offsetY", obj);
          }

          int state = -1;

          if (p.containsKey("State"))
          {
            state = this.getStrToInt("State", obj);
          }

          CheckPointSprite cs = new CheckPointSprite(myTextures,  px, py, zoom, delayT, offsetX,offsetY, state);
          cs.setPosition(px,py);
          this.add(cs);
          checkpoints.add(cs); 

       }  else if (t.equals("LightingOff"))
       {

          CheckPointSprite cs = new CheckPointSprite(myTextures,  px, py, "LightOff", 2);
          cs.setPosition(px,py);
          this.add(cs);
          checkpoints.add(cs); 

       } else if (t.equals("LightingOn"))
       {

          CheckPointSprite cs = new CheckPointSprite(myTextures,  px, py, "LightOn", 3);
          cs.setPosition(px,py);
          this.add(cs);
          checkpoints.add(cs); 

       } else if (t.equals("PlayerDie"))
       {
        float xxx = px;
          for (int i = 0; i < w_tiles; i++)
          {
            CheckPointSprite cs = new CheckPointSprite(myTextures,  xxx, py, "PlayerDie", 4);
            cs.setPosition(xxx,py);
            xxx += tw;
            this.add(cs);
            checkpoints.add(cs); 
          }
       } else
       {
          Gdx.app.debug(APP_N, "unknown sprite");
       }

       Gdx.app.debug(APP_N, "3");
    }

    this.add(buggySprite);
    this.add(playerSprite);
    playerSprite.setVisible(false);
    buggySprite.setFocus(true);
    this.add(m_bombWeapon);
    m_bombWeapon.addAll(this);

    for (GameSprite gs : frontSprites)
    {
      this.add(gs, false);
    }

    frontSprites.clear();

    for (MapObject obj : mapObjects)
    {
      Gdx.app.debug(APP_N, "Map Object Iterate");
      MapProperties p = obj.getProperties();
      String t = (String) p.get("type");
      Gdx.app.debug(APP_N, "type = " + t);
      px = this.getFloat("x", obj);
      py = this.getFloat("y", obj);
      pw = this.getFloat("width", obj);
      int w_tiles = (int) (pw/tw);

       if (t.equals("Tutorial"))
       {
          ArrayList<String> lines = new ArrayList<String>();
          for (int i=1; i < 25; i++)
          {
            String lineName = "Line" + i;
            if (p.containsKey(lineName))
            {
              String line = (String)p.get(lineName);
              lines.add(line);
            } else
            {
              break;
            }
          }

          int special = 0;
          if (p.containsKey("Special"))
          {
            special= this.getStrToInt("Special", obj);
          }

          String[] stringArray = lines.toArray(new String[lines.size()]);
          TutorialSprite tut = new TutorialSprite(myTextures, px, py, this, m_font24, stringArray, inputManager);
          tut.setSpecial(special);
          tut.setPosition(px,py);
          tutorials.add(tut);
          this.add(tut);
          tut.setVisible(false);
       } else if (t.equals("EndLevel"))
       {
          int state = -1;
          float tm = 30;

          if (p.containsKey("State"))
          {
            state = this.getStrToInt("State", obj);
          }

          int clone = 1;
          if (p.containsKey("Clone"))
          {
            clone = this.getStrToInt("Clone", obj);
          }


          if (p.containsKey("Time"))
          {
            tm = this.getStrToFloat("Time", obj);
          }

          CheckPointSprite cs = new CheckPointSprite(gameSpritesTextures,  px, py, state, tm, clone, this);
          cs.setPosition(px,py);
          this.add(cs);
          cs.addBombToWorld();
          checkpoints.add(cs);


          //enemies.add(enemy); 
       } else if (t.equals("BlockTile"))
       {
          float xxx = px;
          if (p.containsKey("Fade"))
          {
            for (int i = 0; i < w_tiles; i++)
            {

              BlockTile enemy = new BlockTile(treeTextures, "block_tile", playerSprite);
              enemy.setPosition(xxx,py);
              xxx += tw;
              this.add(enemy);
            }
          } else
          {
            for (int i = 0; i < w_tiles; i++)
            {

              BlockTile enemy = new BlockTile(treeTextures);
              enemy.setPosition(xxx,py);
              xxx += tw;
              this.add(enemy);
            }
          }
       }
    }

    if (m_stage == 2)
      m_lightning = new LightningSprite(treeTextures, this);
    else
      m_lightning = null;

   if ((m_stage == 1) && (m_level >= 0))
      m_moon = new MoonSprite(treeTextures, this);
    else
      m_moon = null;

    //m_mapWidth = tiledMap.getMapWidth();
    //m_mapHeight = tiledMap.getMapHeight();

    //tw = (float) tiledMap.getTilePixelWidth();
    //th = (float) tiledMap.getTilePixelHeight();

    if (ufoEffectPool == null)
    {
      ParticleEffect ufoEffect = new ParticleEffect();
      ufoEffect.load(Gdx.files.internal("ufo_explode.p"), Gdx.files.internal(""));
      ufoEffect.setEmittersCleanUpBlendFunction(true);
      ufoEffectPool = new ParticleEffectPool(ufoEffect, 30, 15);
    }

    if (m_stage > 0)
    {
      PolygonShape shape = new PolygonShape();
      shape.setAsBox(tw/2,th/2);

      PolygonShape shape2 = new PolygonShape();
      shape2.setAsBox(1,th/2);

      FixtureDef fixtureDef = new FixtureDef();
      fixtureDef.shape = shape;
      fixtureDef.density = 0.1f;
      fixtureDef.restitution = 0.1f;

      FixtureDef fixtureDef2 = new FixtureDef();
      fixtureDef2.shape = shape2;
      fixtureDef2.density = 0.1f;
      fixtureDef2.restitution = 0.1f;
      
      for (int tx = 0; tx < m_mapWidth; tx++)
      {
        for (int ty = 0; ty < m_mapHeight; ty++)
        {
            TiledMapTileLayer.Cell c = pLayer.getCell(tx,ty);
            if (c != null)
            {
              BodyDef bodyDef = new BodyDef();
              bodyDef.type = BodyDef.BodyType.StaticBody;
              if (c.getTile().getId() < 50)
              {  
                bodyDef.position.set(tx*tw + (tw/2),th*ty + th/2);
                Body body = world.createBody(bodyDef);
                body.createFixture(fixtureDef);
                m_bodies.add(body);
              } else
              {
                bodyDef.position.set(tx*tw + (tw/2) - 1,th*ty + (th/2));
                Body body = world.createBody(bodyDef);
                body.createFixture(fixtureDef2);
                m_bodies.add(body);
              }
              
            }
        }
      }
      shape.dispose();
      shape2.dispose();

      if (m_level > 0)
      {
        m_canExit = true;
        this.loopSound("music", 0.7f);
        this.loopSound("music2", 0.0f);
      } else
        m_canExit = false;
    } else
    {
      m_canExit = false;
    }

    m_maxX = (m_mapWidth*tw) - m_halfCameraWidth;
    m_maxY = (m_mapHeight*th) - m_halfCameraHeight;
    buggySprite.setCanExit(m_canExit);

    Gdx.app.debug(APP_N, "Load Level End: " + lv);
  }

  public void addEnemy(BaseSprite s)
  {
    this.add(s);
    enemies.add(s);
  }

  public void addEnemyQueue(BaseSprite s)
  {
    enemiesToAdd.add(s);
  }

  public void removeEnemy(BaseSprite s)
  {
    this.remove(s);
    enemies.remove(s);
  }

  public void addCollectible(CollectibleSprite s)
  {
    this.add(s);
    collectibles.add(s);
  }

  public void resetLevel()
  {


    stopAllSounds();

    m_playerHud.setClones(3);
    m_playerHud.setEnergy(10);
    this.loadLevel(m_stage, m_level);

  }

  public void pauseGame()
  {

    for (BaseSprite enemy : enemies) 
    {
        enemy.pause();
    }

    playerSprite.pause();
    buggySprite.pause();

  }

  public void resumeGame()
  {

    for (BaseSprite enemy : enemies) 
    {
        enemy.resume();
    }

    playerSprite.resume();
    buggySprite.resume();
  }

  public void buttonSelected(int buttonNum)
  {
    if (gameState == 300)
    {
      //paused
      if (buttonNum == 2)
      {
        //pause button
        gameState = 10;
        this.resumeGame();
        stateTime = 0;
        m_fadeOutSprite.setOpacity(0f);
        m_activeDialog = null;
        return;
      } else
      {
                //go back to level picker
        this.setGlobal("LevelCompleted", "-1");
        this.setGlobal("LevelCompletedClones", "-1");
        this.setGlobal("NumLives", "" + playerSprite.getLives());
        this.saveGame();
        m_level = -1;
        this.fadeToBlack(0.7f);
        gameState = 101;
        m_activeDialog = null;
        stateTime = 0;
        stopAllSounds();
        return;
      }
    }
  }

  public void setTotalCloneCount()
  {
    int tb = 0;
    for (int s = 1; s < 3; s++)
    {
      for (int lv = 1; lv < 12; lv++)
      {
        String bs = this.getGlobal("Stage" + s + "Level" + lv + "Clones");
        if (bs != null)
        {
          try
          {
            int b = Integer.parseInt(bs);
            tb += b;
          } catch (Exception e)
          {
            this.setGlobal("Stage" + s + "Level" + lv + "Clones","0");
          }
        }
      }
    }
    this.setGlobal("TotalClones", "" + tb);
  }

  public int getPercentComplete()
  {
    float tb = 0;
    int lc = 0;
    for (int s = 1; s < 3; s++)
    {
      for (int lv = 1; lv < 12; lv++)
      {
        String bs = this.getGlobal("Stage" + s + "Level" + lv + "Clones");
        if (bs != null)
        {
          int b = Integer.parseInt(bs);
          tb += (float) b;
          if (b > 0)
            lc++;
        }
      }
    }

    return (int) ((tb / 66.0f) * 100.0f);
  }

  public int getStagePercentComplete(int s)
  {
    float tb = 0;
    int lc = 0;
    for (int lv = 1; lv < 12; lv++)
    {
      String bs = this.getGlobal("Stage" + s + "Level" + lv + "Clones");
      if (bs != null)
      {
        int b = Integer.parseInt(bs);
        tb += (float) b;
        if (b > 0)
          lc++;
      }
    }

    return (int) ((tb / 33.0f) * 100.0f);
  }

  public void setLivesTo99(int stage)
  {
    String ul = this.getGlobal("Stage" + stage + "NinetyNine");
    if (ul == null)
    {
      playerSprite.setLives(99);
      m_playerHud.setLives(playerSprite.getLives());
      this.setGlobal("Stage" + stage + "NinetyNine", "YES");
      this.saveGame();
    } else if (!(ul.equals("YES")))
    {
      playerSprite.setLives(99);
      this.setGlobal("Stage" + stage + "NinetyNine", "YES");
      m_playerHud.setLives(playerSprite.getLives());
      this.saveGame();
    }
  }               
  //Test if bonus level should open (for first time)
  public boolean testBonusLevel(int stage)
  {

    //Gdx.app.debug(APP_N, "test bonus level Stage = " + stage);

    String ul = this.getGlobal("Stage" + stage + "Level11State");

    if (ul.equals("Unlocked"))
      return false;

    //Gdx.app.debug(APP_N, "test bonus level Stage next " + stage);

    int nl = 0;
    for (int lv = 1; lv < 11; lv++)
    {
      String bs = this.getGlobal("Stage" + stage + "Level" + lv + "Clones");
      int b = Integer.parseInt(bs);
      if (b == 3)
        nl++;
    }

    //Gdx.app.debug(APP_N, "test bonus level Stage nl =  " + nl);

    if (stage < 3)
    {
      if (nl >= 10)
      {
        //Gdx.app.debug(APP_N, "test bonus level Stage true");
        return true;
      }
    }

    return false;
  }

  public void safeDispose(Disposable d)
  {
    if (d != null)
    {
      d.dispose();
      d = null;
    }
  }

  public int countClones()
  {
    int cnt = 0;
    for (m_iter = 0; m_iter < checkpoints.size(); m_iter++)
    {
      CheckPointSprite cs = checkpoints.get(m_iter);
      if ((cs.isVisible()) && (cs.getGameState() > 0) && (cs.getSpecial() == 5000))
      {
        cnt++;
        cs.win();
      }
    }
    return cnt;
  }

  public void cleanUp()
  {
    
    this.removeAll();
    tiledMap.dispose();
    tiledMap = null;

    if (m_levelWonHudTexture != null)
    {
      m_levelWonHudTexture.dispose();
      m_levelWonHudTexture = null;
    }

    if (rayHandler != null)
    {
      //rayHandler.removeAll();
      rayHandler.dispose();
      rayHandler = null;
    }

    if (world != null)
    {
      for (Body b : m_bodies)
      {
        world.destroyBody(b);
      }
      m_bodies.clear();
      world.dispose();
      world = null;
    }

    System.gc();
    Gdx.app.debug(APP_N, "Main Dispose");

  }

  public void manageHints(float tm)
  {
    //if ()
  }


/*
  @Override public boolean keyDown(int keycode) {

        return false;
    }

    @Override public boolean keyUp(int keycode) {

        return false;
    }

    @Override public boolean keyTyped(char character) {

        return false;
    }

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override public boolean scrolled(int amount) {
        return false;
    }
    */
}
