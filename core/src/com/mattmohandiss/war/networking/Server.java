package com.mattmohandiss.war.networking;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.mattmohandiss.war.Enums.MessageType;
import com.mattmohandiss.war.Enums.PlayerState;
import com.mattmohandiss.war.Launchers.GameServer;
import com.mattmohandiss.war.Mappers;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by Matthew on 9/25/16.
 */
public class Server extends WebSocketServer {
	public GameServer gameServer;
	public IntMap<WebSocket> clients = new IntMap<>();
	private int uniqueClientCount = 0;

	public Server(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		gameServer.console.log("new connection to " + conn.getRemoteSocketAddress());

		gameServer.globalWorld.players.forEach((entry) -> {
			sendToClient(conn, new Message(MessageType.addPlayer, entry.key));
		});

		clients.put(uniqueClientCount, conn);
		sendToClient(conn, new Message(MessageType.clientJoin, uniqueClientCount));
		uniqueClientCount++;
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		gameServer.console.log("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " with timestamp " + TimeUtils.nanoTime());
		int key = clients.findKey(conn, true, -1);
		if (key != -1) {
			clients.remove(key);
			gameServer.globalWorld.removePlayer(key);
			sendToAllExcept(conn, new Message(MessageType.removePlayer, key));
		}
	}

	@Override
	public void onMessage(WebSocket webSocket, String s) {
		gameServer.console.log("IDK how this happened");
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer bytes) {
		//gameServer.console.log("received message (" + message + ") from " + conn.getRemoteSocketAddress()); // + " with timestamp " + TimeUtils.nanoTime()

		Message actualMessage = processMessage(bytes);
		gameServer.console.log("Message of type: " + actualMessage.type + " from address: " + conn.getRemoteSocketAddress());
		if (actualMessage != null) {
			switch (actualMessage.type) {
				case addPlayer:
					gameServer.globalWorld.addPlayer(actualMessage.id, true);
					sendToAllExcept(conn, actualMessage);
					//need to wait for physics world update
					gameServer.globalWorld.players.forEach((entry) -> {
						sendToClient(conn, new Message(MessageType.position, entry.key, new int[]{((int) Mappers.physics.get(gameServer.globalWorld.players.get(entry.key)).body.getPosition().x), ((int) Mappers.physics.get(gameServer.globalWorld.players.get(entry.key)).body.getPosition().y)}));
					});
					break;
				case position:
					Mappers.physics.get(gameServer.globalWorld.players.get(actualMessage.id)).body.setLinearVelocity(0, 0);
					Mappers.physics.get(gameServer.globalWorld.players.get(actualMessage.id)).body.setTransform(actualMessage.contents[0], actualMessage.contents[1], 0);
					sendToAllExcept(conn, actualMessage);
					break;
				case removePlayer:
					conn.close(CloseFrame.NORMAL);
					sendToAllExcept(conn, actualMessage);
					break;
				case velocity:
					Mappers.physics.get(gameServer.globalWorld.players.get(actualMessage.id)).body.setLinearVelocity(actualMessage.contents[0], actualMessage.contents[1]);
					sendToAllExcept(conn, actualMessage);
					break;
				case fireBullet:
					gameServer.globalWorld.fireBullet(actualMessage.id, new Vector3(actualMessage.contents[0], actualMessage.contents[1], 0));
					sendToAllExcept(conn, actualMessage);
					break;
				case changeState:
					Mappers.stateMachine.get(gameServer.globalWorld.players.get(actualMessage.id)).stateMachine.changeState(PlayerState.values()[actualMessage.contents[0]]);
					sendToAllExcept(conn, actualMessage);
					break;
				default:
					gameServer.console.log("Server received an unexpected message from client");
			}
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		gameServer.console.log("an error occurred on " + conn.getRemoteSocketAddress());
		ex.printStackTrace();
	}

	public void sendToAllExcept(WebSocket ignoredClient, Message message) {
		clients.forEach((entry) -> {
			if (entry.value != ignoredClient) {
				sendToClient(entry.value, message);
			}
		});
	}

	public void sendToClient(WebSocket client, Message message) {
		try {
			client.send(Serializer.serialize(message));
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
