package com.mattmohandiss.networkedShooter;

import com.badlogic.ashley.core.ComponentMapper;
import com.mattmohandiss.networkedShooter.Components.*;

/**
 * Created by Matthew on 9/5/16.
 */
public class Mappers {
	public static ComponentMapper<PhysicsComponent> physics = ComponentMapper.getFor(PhysicsComponent.class);
	public static ComponentMapper<StateMachineComponent> stateMachine = ComponentMapper.getFor(StateMachineComponent.class);
	public static ComponentMapper<MovementComponent> movement = ComponentMapper.getFor(MovementComponent.class);
	public static ComponentMapper<AISteeringComponent> aiSteering = ComponentMapper.getFor(AISteeringComponent.class);
	public static ComponentMapper<RubberbandingComponent> rubberbanding = ComponentMapper.getFor(RubberbandingComponent.class);
	public static ComponentMapper<NetworkingComponent> networking = ComponentMapper.getFor(NetworkingComponent.class);
	public static ComponentMapper<IDComponent> id = ComponentMapper.getFor(IDComponent.class);
}
