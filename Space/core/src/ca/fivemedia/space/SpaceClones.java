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
import com.badlogic.gdx.files.*;

public class SpaceClones extends GameMain {
  
  public void setupGame()
  {

      m_camera = new OrthographicCamera(1280,720);
      m_camera.update();

      float w = Gdx.graphics.getWidth();
      float h = Gdx.graphics.getHeight();

      m_viewport = new FitViewport(1280,720, m_camera);
      m_viewport.update((int)w, (int)h, true);

      this.addSound("splash", "logo2.wav");

      GameLayer splashLayer = new SplashScreen();
      this.pushGameLayer(splashLayer);
  }

  public void finishSetup()
  {
      
      //Final sounds to keep
      this.addSound("blueCloneDie", "99C_blue_clone_killed.wav");
      this.addSound("whiteCloneDie", "99C_white_clone_killed.wav");
      this.addSound("noAir", "99C_white_clone_no_air.wav");
      this.addSound("enterRover", "99C_rover_enter.wav");
      this.addSound("exitRover", "99C_rover_exit.wav");
      this.addSound("buggyDrive", "99C_rover_drive_loop.wav");

      this.addSound("airDeplete", "99C_air_deplete.wav");
      this.addSound("alarm", "99C_alarm_light_loop.wav");
      this.addSound("movingBlock1", "99C_big_block_grinding_loop.wav");
      this.addSound("movingBlockDestroy", "big_block_destroy_object.wav");
      this.addSound("countdown", "99C_blue_clone_death_countdown.wav");
      this.addSound("tutorial_voice", "VS_AUA_scientist_talking.wav");
      this.addSound("tutorial_in", "VS_AUA_scientist_message_in.wav");
      this.addSound("tutorial_out", "VS_AUA_scientist_message_out.wav");

      this.addSound("switchToggle", "VS_AUA_switch_toggle.wav");
      this.addSound("heartbeat", "99C_white_clone_low_air_loop.wav");
      this.addSound("sawDeath", "VS_AUA_power_cube_land_on_ground.wav");
      this.addSound("movingBlockDeath", "VS_AUA_power_cube_land_on_ground.wav");

      this.addSound("openDoor", "99C_enter_door.wav");
      this.addSound("bombFuse", "99C_bomb_fuse.wav");
      this.addSound("platformOn", "VS_AUA_platform_appear.wav");

      this.addSound("land", "VS_AUA_player_land.wav");
      this.addSound("jump", "VS_AUA_player_jump.wav");
      this.addSound("explode", "explode.wav");
      this.addSound("laser", "VS_AUA_laser_gun_shoot.wav");

      this.addSound("gotSound", "VS_AUA_player_item_pickup.wav");
      this.addSound("footsteps", "99C_clone_walk_1.wav");

      this.addSound("toggle", "VS_AUA_menu_toggle_2.0.wav");
      this.addSound("click", "VS_AUA_menu_select_2.0.wav");
      this.addSound("menuOpen", "VS_AUA_menu_open.wav");

      this.addSound("splat", "splat.wav");
      this.addSound("grow", "99C_grow.wav");

      this.addSound("seeker1", "roar.wav");
      this.addSound("intro1", "99C_level_start_sting.ogg");

      this.addSound("superGreat", "99C_super_great_display.wav");
      this.addSound("winSting", "99C_win_sting.wav");
      this.addSound("vaporize", "VS_AUA_final_boss_player_vaporize.wav");
      this.addSound("hitBlock", "VS_AUA_power_cube_touch.wav");

      loadGameDefaults();
  }

  public void loadGameDefaults()
  {
      this.setGlobal("TotalClones", "0");
      this.setGlobal("NumLives", "99");
      this.setGlobal("WonGame", "NO");
      this.setGlobal("Stage1State", "Unlocked");
      this.setGlobal("Stage2State", "Locked");

      for (int stage = 1; stage < 3; stage++)
      {
          for (int lv = 0; lv < 12; lv++)
          {
              this.setGlobal("Stage" + stage + "Level" + lv + "State", "Locked");
              this.setGlobal("Stage" + stage + "Level" + lv + "Clones", "0");
          }
      }

      this.setGlobal("Stage1Level0State", "Unlocked");
      this.setGlobal("Stage2Level0State", "Unlocked");

      this.setGlobal("LevelCompleted", "-1");
      this.setGlobal("LevelCompletedClones", "-1");

  }

  public void resize(int width, int height) {
    m_viewport.update(width, height, true);
  }

}