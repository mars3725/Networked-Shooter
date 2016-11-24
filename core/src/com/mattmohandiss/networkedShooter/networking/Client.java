package com.mattmohandiss.networkedShooter.networking;

/**
 * Created by Matthew on 9/25/16.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mattmohandiss.networkedShooter.Controller;
import com.mattmohandiss.networkedShooter.Enums.ControllerState;
import com.mattmohandiss.networkedShooter.Enums.MessageType;
import com.mattmohandiss.networkedShooter.Enums.PlayerState;
import com.mattmohandiss.networkedShooter.Mappers;
import com.mattmohandiss.networkedShooter.Screens.ClientSetupScreen;
import com.mattmohandiss.networkedShooter.Screens.GameScreen;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client extends WebSocketClient {
	public GameScreen game;

	public Client(URI serverURI) {
		super(serverURI, new Draft_17());
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				game.gameClient.setScreen(new ClientSetupScreen(game.gameClient));
			}
		});
	}

	@Override
	public void onMessage(String s) {
	}

	public void sendPosition() {
		Mappers.networking.get(game.getPlayer()).game.client.send(new Message(MessageType.position, game.playerID, new int[]{(int) (Mappers.physics.get(game.getPlayer()).body.getPosition().x * 100), (int) (Mappers.physics.get(game.getPlayer()).body.getPosition().y * 100)}));

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.schedule(this::sendPosition, 100, TimeUnit.MILLISECONDS);
	}

	@Override
	public void onMessage(ByteBuffer bytes) {
		//System.out.println("received message: " + message);
		Message actualMessage = processMessage(bytes);
		if (actualMessage != null) {
			switch (actualMessage.type) {
				case clientJoin:
					game.localWorld.addPlayer(actualMessage.id, new Vector2(actualMessage.contents[0] / 100, actualMessage.contents[1] / 100), true);
					game.playerID = actualMessage.id;
					Gdx.input.setInputProcessor(new Controller(game));
					Mappers.networking.get(game.getPlayer()).game = game;
					send(new Message(MessageType.addPlayer, game.playerID, actualMessage.contents));
					sendPosition();
					break;
				case addPlayer:
					game.localWorld.addPlayer(actualMessage.id, new Vector2(actualMessage.contents[0] / 100, actualMessage.contents[1] / 100), false);
					break;
				case position:
					Gdx.app.postRunnable(() -> {
						Mappers.rubberbanding.get(game.localWorld.getPlayer(actualMessage.id)).idealPosition.setPosition(new Vector2(actualMessage.contents[0] / 100, actualMessage.contents[1] / 100));
					});
					break;
				case removePlayer:
					if (actualMessage.id == game.playerID) {
						Mappers.stateMachine.get(game.getPlayer()).stateMachine.changeState(ControllerState.Dead);
					} else {
						game.localWorld.remove(game.localWorld.getPlayer(actualMessage.id));
					}
					break;
				case velocity:
					Mappers.movement.get(game.localWorld.getPlayer(actualMessage.id)).direction.set(new Vector2(actualMessage.contents[0], actualMessage.contents[1]));
					break;
				case fireBullet:
					game.localWorld.fireBullet(actualMessage.id, new Vector3(actualMessage.contents[0], actualMessage.contents[1], 0));
					break;
				case changeState:
					Mappers.stateMachine.get(game.localWorld.getPlayer(actualMessage.id)).stateMachine.changeState(PlayerState.values()[actualMessage.contents[0]]);

					if (actualMessage.contents[0] == PlayerState.Moving.ordinal()) {
						Mappers.movement.get(game.localWorld.getPlayer(actualMessage.id)).direction.set(new Vector2(actualMessage.contents[1], actualMessage.contents[2]));
					}
					break;
				default:
					System.out.print("Client received an unexpected message from Server");
			}
		}
	}

	@Override
	public void onError(Exception ex) {
		System.out.println("an error occurred:" + ex);
		ex.printStackTrace();
	}

	public void send(Message message) {
		try {
			send(Serializer.serialize(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Message processMessage(ByteBuffer message) {
		try {
			return Serializer.deserialize(message.array());
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
