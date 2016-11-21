package com.mattmohandiss.networkedShooter;

import com.mattmohandiss.networkedShooter.Screens.GameServer;
import com.strongjoshua.console.CommandExecutor;

import java.io.IOException;

/**
 * Created by Matthew on 10/17/16.
 */
public class Commander extends CommandExecutor {
	GameServer gameServer;

	public Commander(GameServer gameServer) {
		super();
		this.gameServer = gameServer;
	}

	public void test() {
		console.log("The console is working correctly");
	}

	public void players() {
		console.log(gameServer.globalWorld.getPlayers().size() + " players currently connected");
	}

	public void userCount() {
		console.log(String.valueOf(gameServer.server.clients.size));
	}

	public void clear() {
		console.clear();
	}

	public void stop() {

		try {
			gameServer.server.stop();
			exitApp();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void IP() {
		console.log(gameServer.server.getAddress().toString());
	}
}
