package com.mattmohandiss.war;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.IntMap;
import com.mattmohandiss.war.Enums.CollisionBits;
import com.mattmohandiss.war.Enums.MessageType;
import com.mattmohandiss.war.Systems.StateMachineSystem;
import com.mattmohandiss.war.Systems.SteeringSystem;
import com.mattmohandiss.war.networking.Client;
import com.mattmohandiss.war.networking.Message;
import com.mattmohandiss.war.networking.Server;
import org.java_websocket.framing.CloseFrame;

/**
 * Created by Matthew on 9/25/16.
 */
public class GameWorld {
	public Engine engine = new Engine();
	public World world = new World(new Vector2(0, 0), true);
	public IntMap<Entity> players = new IntMap<>();
	public EntityCreator entityCreator = new EntityCreator(this);
	private Server server;
	private Client client;

	public GameWorld(Server server) {
		this();
		this.server = server;
	}

	public GameWorld(Client client) {
		this();
		this.client = client;
	}

	private GameWorld() {
		engine.addSystem(new StateMachineSystem());
		engine.addSystem(new SteeringSystem());

		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				if (!solveContact(contact.getFixtureA(), contact.getFixtureB())) {
					solveContact(contact.getFixtureB(), contact.getFixtureA());
				}
			}

			@Override
			public void endContact(Contact contact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}

			private boolean solveContact(Fixture firstFixture, Fixture secondFixture) {
				short firstBodyMask = firstFixture.getFilterData().categoryBits;
				short secondBodyMask = firstFixture.getFilterData().categoryBits;

				if (firstBodyMask == CollisionBits.ally && secondBodyMask == CollisionBits.enemyBullet) {
					if (server != null) {
						int key = server.clients.findKey(entityforBody(firstFixture.getBody()), true, -1);
						if (key != -1) {
							server.clients.get(key).close(CloseFrame.NORMAL);
							server.sendToAllExcept(server.clients.get(key), new Message(MessageType.removePlayer, key));
						}
					}
					return true;
				} else if (firstBodyMask == CollisionBits.enemy && secondBodyMask == CollisionBits.friendlyBullet) {
					return true;
				} else if (firstBodyMask == CollisionBits.friendlyBullet && secondBodyMask == CollisionBits.wall) {
					return true;
				}
				return false;
			}
		});
	}

	public void create() {
		Vector2[] vertices = new Vector2[4];
		vertices[0] = new Vector2(-100, -100);
		vertices[1] = new Vector2(100, -100);
		vertices[2] = new Vector2(100, 100);
		vertices[3] = new Vector2(-100, 100);
		createPolygon(vertices, new Vector2(10, 10));

		vertices = new Vector2[4];
		vertices[0] = new Vector2(0, 0);
		vertices[1] = new Vector2(10, 5);
		vertices[2] = new Vector2(10, 11);
		vertices[3] = new Vector2(0, 7);
		createPolygon(vertices, new Vector2(10, 10));

		vertices[0] = new Vector2(0, 0);
		vertices[1] = new Vector2(5, 0);
		vertices[2] = new Vector2(5, 5);
		vertices[3] = new Vector2(0, 5);
		createPolygon(vertices, new Vector2(-15, -15));
	}

	private void createPolygon(Vector2[] vertices, Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		Body body = world.createBody(bodyDef);
		ChainShape loop = new ChainShape();
		loop.createLoop(vertices);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = loop;
		fixtureDef.filter.categoryBits = CollisionBits.wall;
		fixtureDef.filter.maskBits = CollisionBits.ally | CollisionBits.enemy | CollisionBits.friendlyBullet;
		body.createFixture(fixtureDef);
		loop.dispose();
	}

	public void addPlayer(int ID) {
		Entity player = entityCreator.createCharacter();
		players.put(ID, player);
		engine.addEntity(player);
	}

	public void removePlayer(int ID) {
		world.destroyBody(Mappers.physics.get(players.get(ID)).body);
		engine.removeEntity(players.get(ID));
		players.remove(ID);
	}

	public void fireBullet(int ID, Vector3 coordinates) {
		if (client != null && client.game.playerID == ID) {
			engine.addEntity(entityCreator.createBullet(coordinates, ID, true));
		} else {
			engine.addEntity(entityCreator.createBullet(coordinates, ID, false));
		}
	}

	public void update(float deltaTime) {
		if (client != null) {
			engine.update(deltaTime);
		}
		world.step(1 / 60f, 6, 2);
	}

	private Entity entityforBody(Body body) {
		final Entity[] entity = new Entity[1];
		players.forEach((entry) -> {
			if (body.equals(Mappers.physics.get(entry.value).body)) {
				entity[0] = entry.value;
			}
		});
		return entity[0];
	}
}
