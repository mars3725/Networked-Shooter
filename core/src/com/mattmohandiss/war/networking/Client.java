package com.mattmohandiss.war.networking;

/**
 * Created by Matthew on 9/25/16.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.mattmohandiss.war.Controller;
import com.mattmohandiss.war.Enums.GameState;
import com.mattmohandiss.war.Enums.MessageType;
import com.mattmohandiss.war.Enums.PlayerState;
import com.mattmohandiss.war.GameScreen;
import com.mattmohandiss.war.Mappers;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

public class Client extends WebSocketClient {
	public GameScreen game;

	public Client(URI serverURI) {
		super(serverURI, new Draft_17());
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("Client connection opened");
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("closed with exit code " + code + " with timestamp " + TimeUtils.nanoTime() + " additional info: " + reason);
		Gdx.app.exit();
	}

	@Override
	public void onMessage(String s) {
		System.out.println("IDK how this happened");
	}

	@Override
	public void onMessage(ByteBuffer bytes) {
		//System.out.println("received message: " + message);
		Message actualMessage = processMessage(bytes);
		if (actualMessage != null) {
			switch (actualMessage.type) {
				case clientJoin:
					game.playerID = actualMessage.id;
					game.localWorld.addPlayer(game.playerID, false);
					Gdx.input.setInputProcessor(new Controller(game));
					Mappers.networking.get(game.getPlayer()).game = game;
					game.gameState = GameState.inProgress;
					send(new Message(MessageType.addPlayer, game.playerID));
					break;
				case addPlayer:
					game.localWorld.addPlayer(actualMessage.id, true);
					break;
				case position:
					Mappers.physics.get(game.localWorld.players.get(actualMessage.id)).body.setLinearVelocity(0, 0);
					Mappers.physics.get(game.localWorld.players.get(actualMessage.id)).body.setTransform(actualMessage.contents[0], actualMessage.contents[1], 0);
					break;
				case removePlayer:
					game.localWorld.removePlayer(actualMessage.id);
					break;
				case velocity:
					Mappers.physics.get(game.localWorld.players.get(actualMessage.id)).body.setLinearVelocity(actualMessage.contents[0], actualMessage.contents[1]);
					break;
				case fireBullet:
					game.localWorld.fireBullet(actualMessage.id, new Vector3(actualMessage.contents[0], actualMessage.contents[1], 0));
					break;
				case changeState:
					Mappers.stateMachine.get(game.localWorld.players.get(actualMessage.id)).stateMachine.changeState(PlayerState.values()[actualMessage.contents[0]]);
					break;
				default:
					System.out.print("Client received an unexpected message from Server");
			}
		}
	}

	@Override
	public void onError(Exception ex) {
		System.err.println("an error occurred:" + ex);
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
