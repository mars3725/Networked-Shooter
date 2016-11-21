package com.mattmohandiss.networkedShooter;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mattmohandiss.networkedShooter.Components.IDComponent;
import com.mattmohandiss.networkedShooter.Components.PhysicsComponent;
import com.mattmohandiss.networkedShooter.Components.StateMachineComponent;
import com.mattmohandiss.networkedShooter.Enums.CollisionBits;
import com.mattmohandiss.networkedShooter.Enums.MessageType;
import com.mattmohandiss.networkedShooter.Systems.StateMachineSystem;
import com.mattmohandiss.networkedShooter.networking.Client;
import com.mattmohandiss.networkedShooter.networking.Message;
import com.mattmohandiss.networkedShooter.networking.Server;
import org.java_websocket.framing.CloseFrame;

import java.util.Iterator;

/**
 * Created by Matthew on 9/25/16.
 */
public class GameWorld {
	public Engine engine = new Engine();
	public World world = new World(new Vector2(0, 0), true);
	public EntityCreator entityCreator = new EntityCreator(this);
	private Server server;
	private Client client;
	private Array<Entity> entitiesForDeletion = new Array<>();

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
				short secondBodyMask = secondFixture.getFilterData().categoryBits;

				if (firstBodyMask == CollisionBits.player && secondBodyMask == CollisionBits.bullet) {
					if (server != null && Mappers.id.get(getEntity(firstFixture.getBody())).entityID == Mappers.id.get(getEntity(secondFixture.getBody())).entityID) {
						int key = server.clients.findKey(getEntity(firstFixture.getBody()), true, -1);
						if (key != -1) {
							server.clients.get(key).close(CloseFrame.NORMAL);
							server.sendToAllExcept(server.clients.get(key), new Message(MessageType.removePlayer, key));
						}
					}
					return true;
				} else if (firstBodyMask == CollisionBits.wall && secondBodyMask == CollisionBits.bullet) {
					remove(getEntity(firstFixture.getBody()));
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
		fixtureDef.filter.maskBits = CollisionBits.player | CollisionBits.bullet;
		body.createFixture(fixtureDef);
		loop.dispose();
	}

	public void addPlayer(int ID, boolean controllable) {
		Entity player = entityCreator.createPlayer(controllable);
		Mappers.id.get(player).entityID = ID;
		engine.addEntity(player);
	}

	public void fireBullet(int ID, Vector3 coordinates) {
		Entity bullet = entityCreator.createBullet(coordinates, ID);
		Mappers.id.get(bullet).entityID = ID;
		engine.addEntity(bullet);
	}

	public void update(float deltaTime) {
		engine.update(deltaTime);
		world.step(1 / 60f, 6, 2);

		entitiesForDeletion.forEach((entity) -> {
			if (entity != null) {
				world.destroyBody(Mappers.physics.get(entity).body);
				engine.removeEntity(entity);
			}
		});
		entitiesForDeletion.clear();
	}

	public ImmutableArray<Entity> getPlayers() {
		return engine.getEntitiesFor(Family.all(PhysicsComponent.class, IDComponent.class, StateMachineComponent.class).get());
	}

	private Entity getEntity(Body body) {
		Iterator<Entity> entities = engine.getEntities().iterator();
		while (entities.hasNext()) {
			Entity entity = entities.next();
			if (body.equals(Mappers.physics.get(entity).body)) {
				return entity;
			}
		}
		return null;
	}

	public Entity getPlayer(int ID) {
		ImmutableArray<Entity> players = getPlayers();
		for (int i = 0; i < players.size(); i++) {
			if (Mappers.id.get(players.get(i)).entityID == ID) {
				return players.get(i);
			}
		}
		return null;
	}

	public void remove(Entity entity) {
		entitiesForDeletion.add(entity);
	}
}
