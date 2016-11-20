package com.mattmohandiss.networkedShooter;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mattmohandiss.networkedShooter.Components.*;
import com.mattmohandiss.networkedShooter.Enums.CollisionBits;
import com.mattmohandiss.networkedShooter.Enums.EnemyState;
import com.mattmohandiss.networkedShooter.Enums.PlayerState;

/**
 * Created by Matthew on 9/25/16.
 */
public class EntityCreator {
	GameWorld world;

	public EntityCreator(GameWorld world) {
		this.world = world;
	}

	public Entity createCharacter() {
		Entity player = playerBase();

		StateMachineComponent stateMachineComponent = new StateMachineComponent();
		stateMachineComponent.stateMachine = new DefaultStateMachine<>(player, PlayerState.Idle);
		player.add(stateMachineComponent);

		MovementComponent movementComponent = new MovementComponent();
//		movementComponent.controller = new Controller();
		player.add(movementComponent);

		NetworkingComponent networkingComponent = new NetworkingComponent();
		player.add(networkingComponent);

		return player;
	}

	public Entity createEnemy() {
		Entity enemy = playerBase();

		StateMachineComponent stateMachineComponent = new StateMachineComponent();
		stateMachineComponent.stateMachine = new DefaultStateMachine<>(enemy, EnemyState.Idle);
		enemy.add(stateMachineComponent);

		SteeringComponent steeringComponent = new SteeringComponent();
		steeringComponent.steerable = new SteerableEntity(enemy);
		steeringComponent.steeringBehavior = new PrioritySteering<>(steeringComponent.steerable);
//		steeringComponent.target = new SteerableEntity(client.ally);
		steeringComponent.collisionAvoidanceGroup = new BlendedSteering<>(steeringComponent.steerable);
		steeringComponent.formationMovementGroup = new BlendedSteering<>(steeringComponent.steerable);
		steeringComponent.individualMovementGroup = new BlendedSteering<>(steeringComponent.steerable);
		steeringComponent.individualMovementGroup.add(new Pursue<>(steeringComponent.steerable, steeringComponent.target
		), 1);
		steeringComponent.steeringBehavior.add(steeringComponent.collisionAvoidanceGroup);
		steeringComponent.steeringBehavior.add(steeringComponent.formationMovementGroup);
		steeringComponent.steeringBehavior.add(steeringComponent.individualMovementGroup);
		//enemy.add(steeringComponent);

		return enemy;
	}

	private Entity playerBase() {
		Entity entity = new Entity();
		PhysicsComponent physicsComponent = new PhysicsComponent();
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.fixedRotation = true;
		physicsComponent.body = world.world.createBody(bodyDef);
		CircleShape circle = new CircleShape();
		circle.setRadius(1);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.filter.categoryBits = CollisionBits.enemy;
		fixtureDef.filter.maskBits = CollisionBits.wall | CollisionBits.enemy | CollisionBits.ally | CollisionBits.friendlyBullet;
		physicsComponent.body.createFixture(fixtureDef);
		entity.add(physicsComponent);

		return entity;
	}

	public Entity createBullet(Vector3 coordinates, int playerID, boolean friendlyBullet) {
		Vector2 playerPos = Mappers.physics.get(world.players.get(playerID)).body.getPosition();

		Entity bullet = new Entity();
		PhysicsComponent physicsComponent = new PhysicsComponent();
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		physicsComponent.body = world.world.createBody(bodyDef);
		CircleShape circle = new CircleShape();
		circle.setRadius(0.25f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		if (friendlyBullet) {
			fixtureDef.filter.categoryBits = CollisionBits.friendlyBullet;
			fixtureDef.filter.maskBits = CollisionBits.enemy | CollisionBits.wall;
		} else {
			fixtureDef.filter.categoryBits = CollisionBits.enemyBullet;
			fixtureDef.filter.maskBits = CollisionBits.ally | CollisionBits.wall;
		}
		physicsComponent.body.createFixture(fixtureDef);
		physicsComponent.body.setBullet(true);
		physicsComponent.body.setTransform(playerPos, 0);
		bullet.add(physicsComponent);

		coordinates.sub(playerPos.x, playerPos.y, 0);
		coordinates.nor().scl(5000);
		physicsComponent.body.applyForceToCenter(coordinates.x, coordinates.y, true);

		return bullet;
	}
}
