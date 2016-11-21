package com.mattmohandiss.networkedShooter.networking;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.mattmohandiss.networkedShooter.Enums.MessageType;
import com.mattmohandiss.networkedShooter.Enums.PlayerState;
import com.mattmohandiss.networkedShooter.Mappers;
import com.mattmohandiss.networkedShooter.Screens.GameServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Matthew on 9/25/16.
 */
public class Server extends WebSocketServer {
	public GameServer gameServer;
	public IntMap<WebSocket> clients = new IntMap<>();
	private int uniqueClientCount = 1;
	private Timer timer = new Timer();

	public Server(InetSocketAddress address) {
		super(address);
	}

	private void broadcastPositions() {
		gameServer.globalWorld.getPlayers().forEach((player) -> {
			if (Mappers.stateMachine.get(player).stateMachine.isInState(PlayerState.Idle)) {
				sendToAllExcept(null, new Message(MessageType.position, Mappers.id.get(player).entityID, new int[]{(int) (Mappers.physics.get(gameServer.globalWorld.getPlayer(Mappers.id.get(player).entityID)).body.getPosition().x * 100), (int) (Mappers.physics.get(gameServer.globalWorld.getPlayer(Mappers.id.get(player).entityID)).body.getPosition().y * 100)}));
			}
		});
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				broadcastPositions();
			}
		}, 250);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		gameServer.console.log("new connection to " + conn.getRemoteSocketAddress());

		gameServer.globalWorld.getPlayers().forEach((player) -> {
			sendToClient(conn, new Message(MessageType.addPlayer, Mappers.id.get(player).entityID));
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
			gameServer.globalWorld.remove(gameServer.globalWorld.getPlayer(key));
			sendToAllExcept(conn, new Message(MessageType.removePlayer, key));
		}
	}

	@Override
	public void onMessage(WebSocket webSocket, String s) {
		gameServer.console.log("IDK how this happened");
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer bytes) {
		Message actualMessage = processMessage(bytes);
		//gameServer.console.log("Message of type: " + actualMessage.type + " from address: " + conn.getRemoteSocketAddress());
		if (actualMessage != null) {
			switch (actualMessage.type) {
				case addPlayer:
					gameServer.globalWorld.addPlayer(actualMessage.id, false);
					sendToAllExcept(conn, actualMessage);
					gameServer.globalWorld.getPlayers().forEach((player) -> {
						sendToClient(conn, new Message(MessageType.position, Mappers.id.get(player).entityID, new int[]{(int) (Mappers.physics.get(gameServer.globalWorld.getPlayer(Mappers.id.get(player).entityID)).body.getPosition().x * 100), (int) (Mappers.physics.get(gameServer.globalWorld.getPlayer(Mappers.id.get(player).entityID)).body.getPosition().y * 100)}));
					});
					break;
				case position:
					Mappers.physics.get(gameServer.globalWorld.getPlayer(actualMessage.id)).body.setLinearVelocity(0, 0);
					if (!gameServer.globalWorld.world.isLocked()) {
						Mappers.physics.get(gameServer.globalWorld.getPlayer(actualMessage.id)).body.setTransform(actualMessage.contents[0] / 100, actualMessage.contents[1] / 100, 0);
					}
					break;
				case removePlayer:
					sendToAllExcept(conn, actualMessage);
					break;
				case velocity:
					Mappers.physics.get(gameServer.globalWorld.getPlayer(actualMessage.id)).body.setLinearVelocity(actualMessage.contents[0], actualMessage.contents[1]);
					sendToAllExcept(conn, new Message(MessageType.position, actualMessage.id, new int[]{(int) (Mappers.physics.get(gameServer.globalWorld.getPlayer(actualMessage.id)).body.getPosition().x * 100), (int) (Mappers.physics.get(gameServer.globalWorld.getPlayer(actualMessage.id)).body.getPosition().y * 100)}));
					sendToAllExcept(conn, actualMessage);
					break;
				case fireBullet:
					gameServer.globalWorld.fireBullet(actualMessage.id, new Vector3(actualMessage.contents[0], actualMessage.contents[1], 0));
					sendToAllExcept(conn, actualMessage);
					break;
				case changeState:
					Mappers.stateMachine.get(gameServer.globalWorld.getPlayer(actualMessage.id)).stateMachine.changeState(PlayerState.values()[actualMessage.contents[0]]);
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
		clients.values().forEach((value) -> {
			if (value != ignoredClient) {
				sendToClient(value, message);
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
