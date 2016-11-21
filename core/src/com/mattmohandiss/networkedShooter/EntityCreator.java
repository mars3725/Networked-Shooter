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
import com.mattmohandiss.networkedShooter.Enums.ControllerState;
import com.mattmohandiss.networkedShooter.Enums.NPCState;
import com.mattmohandiss.networkedShooter.Enums.PlayerState;

/**
 * Created by Matthew on 9/25/16.
 */
public class EntityCreator {
	GameWorld world;

	public EntityCreator(GameWorld world) {
		this.world = world;
	}

	public Entity createPlayer(boolean controllable) {
		Entity player = new Entity();
		PhysicsComponent physicsComponent = new PhysicsComponent();
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.fixedRotation = true;
		physicsComponent.body = world.world.createBody(bodyDef);
		CircleShape circle = new CircleShape();
		circle.setRadius(1);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.filter.categoryBits = CollisionBits.player;
		fixtureDef.filter.maskBits = CollisionBits.wall | CollisionBits.bullet | CollisionBits.player;
		physicsComponent.body.createFixture(fixtureDef);
		player.add(physicsComponent);

		player.add(new IDComponent());

		StateMachineComponent stateMachineComponent = new StateMachineComponent();
		player.add(stateMachineComponent);

		if (controllable) {
			stateMachineComponent.stateMachine = new DefaultStateMachine<>(player, ControllerState.Idle);
			player.add(new MovementComponent());
			player.add(new NetworkingComponent());
		} else {
			stateMachineComponent.stateMachine = new DefaultStateMachine<>(player, PlayerState.Idle);
		}

		return player;
	}

	public Entity createNPC(Entity target) {
		Entity enemy = createPlayer(false);

		StateMachineComponent stateMachineComponent = new StateMachineComponent();
		stateMachineComponent.stateMachine = new DefaultStateMachine<>(enemy, NPCState.Idle);
		enemy.add(stateMachineComponent);

		SteeringComponent steeringComponent = new SteeringComponent();
		steeringComponent.steerable = new SteerableEntity(enemy);
		steeringComponent.steeringBehavior = new PrioritySteering<>(steeringComponent.steerable);
		steeringComponent.target = new SteerableEntity(target);
		steeringComponent.collisionAvoidanceGroup = new BlendedSteering<>(steeringComponent.steerable);
		steeringComponent.formationMovementGroup = new BlendedSteering<>(steeringComponent.steerable);
		steeringComponent.individualMovementGroup = new BlendedSteering<>(steeringComponent.steerable);
		steeringComponent.individualMovementGroup.add(new Pursue<>(steeringComponent.steerable, steeringComponent.target
		), 1);
		steeringComponent.steeringBehavior.add(steeringComponent.collisionAvoidanceGroup);
		steeringComponent.steeringBehavior.add(steeringComponent.formationMovementGroup);
		steeringComponent.steeringBehavior.add(steeringComponent.individualMovementGroup);
		enemy.add(steeringComponent);

		return enemy;
	}

	public Entity createBullet(Vector3 coordinates, int playerID) {
		Vector2 playerPos = Mappers.physics.get(world.getPlayer(playerID)).body.getPosition();

		Entity bullet = new Entity();
		PhysicsComponent physicsComponent = new PhysicsComponent();
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		physicsComponent.body = world.world.createBody(bodyDef);
		CircleShape circle = new CircleShape();
		circle.setRadius(0.25f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.filter.categoryBits = CollisionBits.bullet;
		fixtureDef.filter.maskBits = CollisionBits.player | CollisionBits.wall | CollisionBits.bullet;
		fixtureDef.isSensor = true;
		physicsComponent.body.createFixture(fixtureDef);
		physicsComponent.body.setBullet(true);
		physicsComponent.body.setTransform(playerPos, 0);
		bullet.add(physicsComponent);

		IDComponent idComponent = new IDComponent();
		idComponent.entityID = playerID;
		bullet.add(idComponent);

		coordinates.sub(playerPos.x, playerPos.y, 0);
		coordinates.nor().scl(6000);
		physicsComponent.body.applyForceToCenter(coordinates.x, coordinates.y, true);

		return bullet;
	}
}
