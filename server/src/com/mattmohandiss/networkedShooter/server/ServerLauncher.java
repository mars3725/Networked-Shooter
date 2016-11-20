package com.mattmohandiss.networkedShooter.server;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mattmohandiss.networkedShooter.Launchers.GameServer;

public class ServerLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 360;
		config.title = "War Server";

		new LwjglApplication(new GameServer(), config);
	}
}
