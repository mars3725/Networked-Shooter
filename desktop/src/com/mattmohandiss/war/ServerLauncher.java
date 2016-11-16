package com.mattmohandiss.war;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mattmohandiss.war.Launchers.GameServer;

public class ServerLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 360;
		config.title = "War Server";

		new LwjglApplication(new GameServer(), config);
	}
}
