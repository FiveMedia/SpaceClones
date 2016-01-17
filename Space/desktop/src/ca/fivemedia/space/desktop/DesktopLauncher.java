package ca.fivemedia.space.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ca.fivemedia.space.SpaceClones;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.width=1280;
    config.height=720;

        //config.width= LwjglApplicationConfiguration.getDesktopDisplayMode().width;
        //config.height= LwjglApplicationConfiguration.getDesktopDisplayMode().height;

    // fullscreen
    //config.fullscreen = true;
    // vSync
    config.vSyncEnabled = true;

		new LwjglApplication(new SpaceClones(), config);
	}
}
