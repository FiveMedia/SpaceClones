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

public class OptionsScreenLayer extends GameLayer implements GameMenuListener {
  GameInputManager m_inputManager;
  Matrix4 m_defaultMatrix = new Matrix4();
  GameSprite m_introSprite;
  //GameText m_hudSize;
  GameText m_controllerName;
  //GameText m_controllerStatus;
  //int m_hudSetting = 1;
  BitmapFont m_font32;
  //DPadPanel dpad;
  TextureAtlas textures = null;
  int checkTicks = 30;
  //Texture m_controlsTexture = null;

  public OptionsScreenLayer() {
    Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
    m_introSprite = new GameSprite(new Texture("help.png"));
    m_introSprite.setPosition(0,0);
    this.add(m_introSprite);

    m_font32 = new BitmapFont(Gdx.files.internal("Font32.fnt"), Gdx.files.internal("Font32.png"), false);
    //m_hudSize = new GameText(m_font32, 300);
    //m_hudSize.setVisible(true);

    m_controllerName = new GameText(m_font32, 400);
    //m_controllerStatus = new GameText(m_font32, 400);

    //m_controlsTexture = new Texture("controls.png");
    //GameSprite cs = new GameSprite(m_controlsTexture);
    //this.add(cs);
    //cs.setPosition(465,55);

    String hudSizeString = this.getGlobal("hudSize");
    //String hudSizeString = null;
    if (hudSizeString == null)
    {
      this.setGlobal("hudSize", "1");
      hudSizeString = "1";
    }

    //m_hudSetting = Integer.parseInt(hudSizeString);
    //this.setSizeText(m_hudSetting);

    
    //this.add(m_hudSize);
    //m_hudSize.setPosition(510,510);

    this.add(m_controllerName);
    m_controllerName.setPosition(110,260);

    //this.add(m_controllerStatus);
    //m_controllerStatus.setPosition(100,200);

    m_inputManager = new GameInputManager(true);
    m_inputManager.setViewport(GameMain.getSingleton().m_viewport);
    m_defaultMatrix = m_camera.combined.cpy();
    m_defaultMatrix.setToOrtho2D(0, 0, 1280, 720);

    textures = new TextureAtlas("trees.txt");
    //GameButton sizeButton = new GameButton(textures, "Change Size", m_font32);
    //playButton.setPosition((1280 - playButton.getWidth())/2, 400);

    GameButton backButton = new GameButton(textures, "Exit", m_font32);

    m_controllerName.setText(m_inputManager.getControllerName());
    //playButton.setPosition((1280 - playButton.getWidth())/2, 400);

    //GameButton button1, GameButton button2, int defaultButton, float spacer, boolean verticalAlign, InputManager inputManager) {
      
    GameMenu menu = new GameMenu(backButton, 30, true, m_inputManager, this);
    this.add(menu);

    menu.setPosition(170,110);

    //dpad = new DPadPanel(textures);
    //dpad.setPosition(25,10);
    //dpad.setVisible(true);
    //dpad.setOpacity(0.95f);
    //this.add(dpad);

    this.setCameraPosition(640,360);

  }

  public void setSizeText(int hs)
  {
    /*
    if (hs == 0)
    {
      m_hudSize.setText("Touch Controls Size: Small");
    } else if (hs == 1)
    {
      m_hudSize.setText("Touch Controls Size: Standard");
    } else if (hs == 2)
    {
      m_hudSize.setText("Touch Controls Size: Big");
    } else if (hs == 3)
    {
      m_hudSize.setText("Touch Controls Size: Bigger");
    } else if (hs == 4)
    {
      m_hudSize.setText("Touch Controls Size: Biggest");
    }*/
  }

  public void buttonSelected(int buttonNum)
  {
    if (buttonNum == 1)
    {
      //exit
      this.replaceActiveLayer(new TitleScreenLayer());
      this.cleanUp();
    }
  }

  @Override
  public void update (float deltaTime) {
    m_inputManager.handleInput(); 
    checkTicks++;
    if (checkTicks > 90)
    {
      m_controllerName.setText(m_inputManager.getControllerName());
      checkTicks = 0;
    }
    //m_controllerStatus.setText(m_inputManager.getControllerStatus());   
  }


  @Override
  protected void postCustomDraw()
  {

  }

  public void cleanUp()
  {
    m_font32.dispose();
    textures.dispose();
    m_inputManager.cleanUp();
    m_inputManager = null;
  }
}
