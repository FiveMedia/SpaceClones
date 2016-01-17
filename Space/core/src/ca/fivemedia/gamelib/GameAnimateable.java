package ca.fivemedia.gamelib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

public interface GameAnimateable {
    public void run(GameDrawable target);
    public void stop();
    public boolean step(float deltaTime);
    public boolean isRunning();
    public boolean ignoreStop();
    public void setIgnoreStop(boolean ignore);
}