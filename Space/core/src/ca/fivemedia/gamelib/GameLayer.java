package ca.fivemedia.gamelib;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public abstract class GameLayer implements GameContainer {

	private ArrayList<GameDrawable> m_children = new ArrayList<GameDrawable>();
  private ArrayList<GameDrawable> m_childrenBack = new ArrayList<GameDrawable>();
	protected SpriteBatch m_spriteBatch;
	boolean m_clear;
	float deltaTime;
	public OrthographicCamera m_camera = null;
	private ArrayList<PooledEffect> m_particleEffects = new ArrayList<PooledEffect>();
	protected ZoomController m_zoomController;
	protected CameraPositionController m_cameraPositionController;
	public static ShapeRenderer m_shapeRenderer = null;
	protected TapStatus m_tapEvent;
	boolean m_particleEffectsActive;
  public int gameState;
  public static SoundManager m_soundManager = null;
  int m_iter = 0;
  GameDrawable m_drawable = null;
  public boolean m_wasJustShaking = false;

	public GameLayer(boolean clearOnRender, OrthographicCamera camera, SpriteBatch spriteBatch)
	{
		super();
		m_clear = clearOnRender;
		m_camera = camera;
		m_spriteBatch = spriteBatch;
		m_zoomController = new ZoomController(1.0f);
		m_cameraPositionController = new CameraPositionController(0f,0f);
    if (m_shapeRenderer == null)
		  m_shapeRenderer = new ShapeRenderer();
		m_tapEvent = new TapStatus();
		m_particleEffectsActive = true;
    m_soundManager = GameMain.getSingleton().m_soundManager;

	}

  public GameLayer()
  {
    super();
    m_clear = true;
    GameMain gm = GameMain.getSingleton();

    m_camera = gm.m_camera;
    m_spriteBatch = gm.m_spriteBatch;

    m_zoomController = new ZoomController(1.0f);
    m_cameraPositionController = new CameraPositionController(0f,0f);
    if (m_shapeRenderer == null)
      m_shapeRenderer = new ShapeRenderer();
    m_tapEvent = new TapStatus();
    m_particleEffectsActive = true;
    m_soundManager = GameMain.getSingleton().m_soundManager;

  }

  public ArrayList<GameDrawable> getChildren()
  {
    return m_children;
  }

  public ArrayList<GameDrawable> getChildrenBack()
  {
    return m_childrenBack;
  }

	public void addParticleEffect(PooledEffect effect)
	{
		m_particleEffects.add(effect);
	}

	public void pauseParticleEffects()
	{
		m_particleEffectsActive = false;
	}

	public void resumeParticleEffects()
	{
		m_particleEffectsActive = true;
	}

	public void add(GameDrawable obj)
	{
		m_children.add(obj);
    obj.setParent(this);
	}

  public void add(GameDrawable obj, boolean back)
  {
    if (back)
    {
      m_childrenBack.add(obj);
    } else
    {
      m_children.add(obj);
    }

    obj.setParent(this);
  }

  public void setGameState(int s)
  {
    gameState = s;
  }

	public void remove(GameDrawable obj)
	{
		m_children.remove(obj);
    obj.setParent(null);
	}

	public void removeAll()
	{

    for (GameDrawable d : m_children)
    {
      d.dispose();
    }

    for (GameDrawable d : m_childrenBack)
    {
      d.dispose();
    }

		m_children.clear();
    m_childrenBack.clear();
	}

  public abstract void update(float deltaTime);

  private void updateChildren(float deltaTime)
  {

    for (m_iter = 0; m_iter < m_children.size(); m_iter++)
    {
      m_drawable = m_children.get(m_iter);
      if (m_drawable.isVisible())
        m_drawable.update(deltaTime);
    }

    for (m_iter = 0; m_iter < m_childrenBack.size(); m_iter++)
    {
      m_drawable = m_childrenBack.get(m_iter);
      if (m_drawable.isVisible())
        m_drawable.update(deltaTime);
    }
  }

  public float getCameraX()
  {
    return m_cameraPositionController.getX();
  }

  public float getCameraY()
  {
    return m_cameraPositionController.getY();
  }

  public void shakeCamera(float duration)
  {
    m_cameraPositionController.shake(m_camera.position.x, m_camera.position.y, duration);
    m_wasJustShaking = true;
  }

  public boolean isShaking()
  {
    return m_cameraPositionController.isShaking();
  }

  public void setCameraPosition(float x, float y)
  {
  	m_cameraPositionController.setPosition(x,y);
  	m_cameraPositionController.updateCameraPosition(m_camera);
  }

  public void setCameraPosition(float x, float y, float duration)
  {
  	m_cameraPositionController.setPosition(x,y, duration);
  }

  public void setCameraZoom(float z)
  {
    m_zoomController.setZoom(z);
  }

  public void setCameraZoom(float z, float duration)
  {
    m_zoomController.setZoom(z, duration);
  }

  protected void preCustomDraw() {};
  protected void postCustomDraw() {};

	public void render()
	{
		deltaTime = Gdx.graphics.getDeltaTime();

		m_zoomController.step(deltaTime);
		m_cameraPositionController.step(deltaTime);
		m_cameraPositionController.updateCameraPosition(m_camera);

		this.update(deltaTime);
		this.updateChildren(deltaTime);

		m_camera.zoom = m_zoomController.getZoom();
    m_camera.update();

	  if (m_clear)
	  {
	      Gdx.gl.glClearColor(0, 0, 0, 1);
	      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}

		this.preCustomDraw();

    //draw all sprites/shapes
    m_shapeRenderer.setProjectionMatrix(m_camera.combined);
    m_spriteBatch.setProjectionMatrix(m_camera.combined);
    m_spriteBatch.begin();

    //Gdx.app.log("INFO", "Render Loop Begin");
    GameDrawable d = null;
    for (m_iter = 0; m_iter < m_children.size(); m_iter++)
    {
      m_drawable = m_children.get(m_iter);
      if (m_drawable.isVisible())
      {
        m_drawable.animate(deltaTime);
        m_drawable.draw(m_spriteBatch);
      }
    }

	  if (m_particleEffectsActive)
	  {
		 Iterator it = m_particleEffects.iterator();
	    while (it.hasNext())
	    {
	      	PooledEffect effect = (PooledEffect) it.next();
	      	effect.draw(m_spriteBatch, deltaTime);
    			if (effect.isComplete()) {
    				effect.free();
    				it.remove();
    			}
	    }
  	}

	  m_spriteBatch.end();
	  this.postCustomDraw();
	}

  public void setGlobal(String key, String value)
  {
      GameMain.getSingleton().setGlobal(key,value);
  }

  public String getGlobal(String key)
  {
      return GameMain.getSingleton().getGlobal(key);
  }

  public void addSound(String soundName, String file)
  {
    GameMain.getSingleton().addSound(soundName, file);
  }

  public void removeSound(String soundName)
  {
    GameMain.getSingleton().removeSound(soundName);
  }

	public void playSound(String soundName, float volume)
	{
		GameMain.getSingleton().playSound(soundName, volume);
	}

  public void stopSound(String soundName)
  {
    GameMain.getSingleton().stopSound(soundName);
  }

  public void setMusic(String musicName)
  {
    GameMain.getSingleton().setMusic(musicName);
  }

  public void setMusic(String musicName, float v)
  {
    GameMain.getSingleton().setMusic(musicName, v);
  }

  public long loopSound(String soundName, float volume)
  {
    return GameMain.getSingleton().loopSound(soundName, volume);
  }

  public void loadGame(int n)
  {
    GameMain.getSingleton().loadGame(n);
  }

  public void saveGame()
  {
    GameMain.getSingleton().saveGame();
  }

  public void eraseGame(int n)
  {
    GameMain.getSingleton().eraseGame(n);
  }

  public void loadGameDefaults()
  {
    GameMain.getSingleton().loadGameDefaults();
  }

  public void stopAllSounds()
  {
    GameMain.getSingleton().stopAllSounds();
  }

  public void replaceActiveLayer(GameLayer newLayer)
  {
    GameMain gm = GameMain.getSingleton();
    gm.replaceActiveLayer(newLayer);
  }

  public void drawBackSprites()
  {

    m_shapeRenderer.setProjectionMatrix(m_camera.combined);
    m_spriteBatch.setProjectionMatrix(m_camera.combined);
    m_spriteBatch.begin();

    GameDrawable d = null;
    for (m_iter = 0; m_iter < m_childrenBack.size(); m_iter++)
    {
      m_drawable = m_childrenBack.get(m_iter);
      if (m_drawable.isVisible())
      {
        m_drawable.animate(deltaTime);
        m_drawable.draw(m_spriteBatch);
      }
    }

    /*
    for (GameDrawable d : m_childrenBack)
    {
      if (d.isVisible())
      {
        d.animate(deltaTime);
        d.draw(m_spriteBatch);
      }
    } */

    m_spriteBatch.end();

  }

  public boolean isOnScreen(float xx, float yy)
  {
    float xl = xx + 75;
    float yt = yy - 100;
    float xr = xx - 75;
    float yb = yy + 100;

    boolean a = m_camera.frustum.pointInFrustum(xl, yt, 0);
    boolean a1 = m_camera.frustum.pointInFrustum(xr, yt, 0);
    boolean b = m_camera.frustum.pointInFrustum(xr,yb,0);
    boolean b1 = m_camera.frustum.pointInFrustum(xl,yb,0);
    return (a || b || a1 || b1);
  }

  public long loopSoundManageVolume(String soundName, GameSprite sprite, GameSprite player, float max, float min)
  {
    return GameMain.getSingleton().loopSoundManageVolume(soundName, sprite, player, max, min);
  }

}