package com.mattmohandiss.networkedShooter.client;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mattmohandiss.networkedShooter.Screens.GameClient;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 360;
		config.title = "Networked-Shooter Client";

		new LwjglApplication(new GameClient(), config);
	}
}
