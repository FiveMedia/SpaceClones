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


// Move quicker than zombies...but pause every 2 to 4 seconds for between 1 and 3 seconds, then either hone in X, Y
// So they seek you out.

public class TutorialSprite extends GameSprite {

    GameSprite m_backSprite;
    ArrayList<GameScrollTextPanel> m_textPanels;
    GameScrollTextPanel m_currentPanel;
    int m_currentPanelNumber;
    float m_stateTime;
    boolean m_valid;
    int m_state = 0;
    AnimateSpriteFrame m_talkAnimation;
    protected GameInputManager m_inputManager;
    int m_special = 0;

    public TutorialSprite(TextureAtlas myTextures, float startX, float startY, GameContainer parent, BitmapFont font, String[] lines, GameInputManager inputManager) {

    super(myTextures.findRegion("Professor_F1"));
    //m_walkAnimation = new AnimateSpriteFrame(myTextures, new String[] {"Spider_F1"}, 2.0f, -1);



    m_backSprite = new GameSprite(new Texture("tutorial_back.png"));
    m_backSprite.setPosition(startX + this.getWidth() + 15, startY + this.getHeight()/2);
    m_backSprite.setOrigin(m_backSprite.getOriginX(), m_backSprite.getHeight()/2);
    parent.add(m_backSprite);
    m_backSprite.setVisible(false);

    m_textPanels = new ArrayList<GameScrollTextPanel>();

    ArrayList<String> newLines = new ArrayList<String>();
    boolean includesNext = false;

    for (int i=0; i < lines.length; i++)
    {
        if (lines[i].equals("BREAK"))
        {
            includesNext = true;
            String[] stringArray = newLines.toArray(new String[newLines.size()]);
            GameScrollTextPanel textPanel = new GameScrollTextPanel(font,stringArray, 5.0f);
            m_textPanels.add(textPanel);
            parent.add(textPanel);
            textPanel.setVisible(false);
            newLines.clear();
        } else
        {
            newLines.add(lines[i]);
        }
    }

    if (includesNext == false)
    {
        GameScrollTextPanel textPanel = new GameScrollTextPanel(font,lines, 5.0f);
        m_textPanels.add(textPanel);
        parent.add(textPanel);
        textPanel.setVisible(false);
    } else
    {
        String[] stringArray = newLines.toArray(new String[newLines.size()]);
        GameScrollTextPanel textPanel = new GameScrollTextPanel(font,stringArray, 5.0f);
        m_textPanels.add(textPanel);
        parent.add(textPanel);
        textPanel.setVisible(false);
        newLines.clear();
    }

    m_currentPanel = m_textPanels.get(0);

    Gdx.app.debug("Tutorial", "Panels Count =  " + m_textPanels.size());

    m_valid = true;

    m_talkAnimation = new AnimateSpriteFrame(myTextures, new String[] {"Professor_F1", "Professor_F2"}, 0.25f, -1);

    m_inputManager = inputManager;
   

    //(float duration, float fromScaleX, float fromScaleY, float toScaleX, float toScaleY)
  }

  public void setSpecial(int s)
  {
    //1 = hoverboard
    m_special = s;
  }

  public int getSpecial()
  {
    return m_special;
  }

  public boolean isValid()
  {
    return m_valid;
  }

  public void activate(float cameraX, float cameraY)
  {
    m_currentPanel = m_textPanels.get(0);
    m_currentPanelNumber = 0;
    this.setPosition(cameraX - 615, cameraY - 290);
    m_backSprite.setPosition(this.getX() + this.getWidth() + 15, this.getY() - 30);
    for (GameScrollTextPanel panel : m_textPanels)
    {
        panel.setPosition(m_backSprite.getX() + 25, m_backSprite.getY() + m_backSprite.getHeight() - 10);
    }

    AnimateScaleTo scaleAnimation = new AnimateScaleTo(0.5f, 1.0f, 0.05f, 1.0f, 1.0f);
    this.setVisible(true);
    m_backSprite.runAnimation(scaleAnimation);
    m_valid = false;
    this.playSound("tutorial_in", 0.5f);
  }

  public void update(float deltaTime)
  {
    m_stateTime += deltaTime;
    if ((m_stateTime > 0.75f) && (m_state == 0))
    {
        m_currentPanel.run();
        m_state = 1;
        this.loopSound("tutorial_voice",0.55f);
        this.runAnimation(m_talkAnimation);
    }

    if (m_state == 1)
    {
        if (m_currentPanel.isDone())
        {
            this.stopAllAnimations();
            this.stopSound("tutorial_voice");
            m_state = 2;
        }
    }

    if ((m_stateTime > 0.6f) && (m_inputManager.nextPressed()) && (m_state < 3))
    {
        m_currentPanelNumber++;
        Gdx.app.debug("Tutorial", "Current Panel Number =  " + m_currentPanelNumber);
        if (m_currentPanelNumber >= m_textPanels.size())
        {
            //really done.
            m_state = 3;
            this.stopAllAnimations();
            m_backSprite.stopAllAnimations();
            AnimateScaleTo scaleAnimation = new AnimateScaleTo(0.3f, 1.0f, 1.0f, 1.0f, 0.001f);
            m_backSprite.runAnimation(scaleAnimation);
            AnimateFadeOut fadeOut = new AnimateFadeOut(0.3f);
            this.runAnimation(fadeOut);
            m_currentPanel.setVisible(false);
            this.stopSound("tutorial_voice");
            m_stateTime = 0;
            m_valid = false;
            this.playSound("tutorial_out", 0.5f);
        } else
        {
            Gdx.app.debug("Tutorial", "Showing next panel: " + m_currentPanelNumber);
            m_state = 1;
            this.stopAllAnimations();
            this.stopSound("tutorial_voice");
            m_currentPanel.setVisible(false);
            m_currentPanel = m_textPanels.get(m_currentPanelNumber);
            m_currentPanel.setVisible(true);
            m_stateTime = 0;
            m_currentPanel.run();
            this.loopSound("tutorial_voice",0.55f);
            this.runAnimation(m_talkAnimation);
        }


    } else if ((m_state == 3) && (m_stateTime > 0.4f))
    {
        this.getParent().setGameState(51);
        this.setVisible(false);
        m_valid = false;
    }
  }

  public void setVisible(boolean visible)
  {
    m_backSprite.setVisible(visible);
    if (visible)
    {
        GameScrollTextPanel panel = m_textPanels.get(0);
        panel.setVisible(visible);
    } else
    {
        for (GameScrollTextPanel panel : m_textPanels)
        {
            panel.setVisible(visible);
        }
    }

    super.setVisible(visible);
  }

}