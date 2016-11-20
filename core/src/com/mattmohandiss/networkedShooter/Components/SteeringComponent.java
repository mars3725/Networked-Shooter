package com.mattmohandiss.networkedShooter.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.math.Vector2;
import com.mattmohandiss.networkedShooter.SteerableEntity;

/**
 * Created by Matthew on 9/23/16.
 */
public class SteeringComponent implements Component {
	public SteerableEntity steerable;
	public PrioritySteering<Vector2> steeringBehavior;
	public Steerable<Vector2> target;
	public BlendedSteering<Vector2> collisionAvoidanceGroup;
	public BlendedSteering<Vector2> formationMovementGroup;
	public BlendedSteering<Vector2> individualMovementGroup;
}
