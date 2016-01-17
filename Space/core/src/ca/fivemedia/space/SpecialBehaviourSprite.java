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


public class SpecialBehaviourSprite extends GameSprite {
    int m_type = 0;
    public SpecialBehaviourSprite(TextureAtlas myTextures, int type) {
        super(myTextures.findRegion("blank"));
        m_type = type;
    }

    public int getType()
    {
      return m_type;
    }

  @Override
  public void draw(SpriteBatch s)
  {
    return;
  }
}