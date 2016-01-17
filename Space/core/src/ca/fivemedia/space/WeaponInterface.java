package ca.fivemedia.space;
import ca.fivemedia.gamelib.*;
import com.badlogic.gdx.math.Rectangle;

public interface WeaponInterface {
  public void attack();
  public boolean isActive();
  public void handleCollision(GameLayer layer, BaseSprite sprite);
  public Rectangle getBoundingRectangle();
  public boolean didCollide(GameLayer layer, BaseSprite sprite);
  public int getPauseTicks();
  public int getAttackTicks();
  public boolean stopMovingWhenAttacking();
}