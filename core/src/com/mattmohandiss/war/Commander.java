package com.mattmohandiss.war;

import com.mattmohandiss.war.Launchers.GameServer;
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
		gameServer.globalWorld.players.forEach((entry) -> {
			console.log(entry.value.toString() + " for key " + entry.key);
		});
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
