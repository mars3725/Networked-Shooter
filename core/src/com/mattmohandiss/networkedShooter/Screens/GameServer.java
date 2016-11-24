package com.mattmohandiss.networkedShooter.Screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.VisUI;
import com.mattmohandiss.networkedShooter.Commander;
import com.mattmohandiss.networkedShooter.GameWorld;
import com.mattmohandiss.networkedShooter.Systems.StateMachineSystem;
import com.mattmohandiss.networkedShooter.networking.Server;
import com.strongjoshua.console.GUIConsole;

import java.net.InetSocketAddress;

/**
 * Created by Matthew on 10/12/16.
 */
public class GameServer extends ApplicationAdapter {
	public Engine engine = new Engine();
	public Server server;
	public GameWorld globalWorld;
	public GUIConsole console;

	@Override
	public void create() {
		VisUI.load();
		console = new GUIConsole(VisUI.getSkin());
		console.setCommandExecutor(new Commander(this));
		console.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		console.setSizePercent(100, 100);
		console.setVisible(true);

		engine.addSystem(new StateMachineSystem());

		Server server = new Server(new InetSocketAddress("localhost", 8855));
		this.server = server;
		globalWorld = new GameWorld(server);
		globalWorld.create();
		server.gameServer = this;
		new Thread(server::run).start();
		console.log("Server initialized at " + server.getAddress());
		server.broadcastPositions();
	}

	@Override
	public void render() {
		globalWorld.update(Gdx.graphics.getDeltaTime());
		try {
			console.draw();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		console.refresh();
	}
}
