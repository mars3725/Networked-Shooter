package com.mattmohandiss.networkedShooter;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Matthew on 7/27/16.
 */
public class SteerableEntity implements Steerable<Vector2> {
	private final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());
	private Body body;
	private Entity entity;
	private float boundingRadius = 0;
	private boolean tagged;
	private float maxLinearSpeed = 14;
	private float maxLinearAcceleration = 1000;
	private float maxAngularSpeed = 10;
	private float maxAngularAcceleration = 10;

	SteerableEntity(Entity entity) {
		this.entity = entity;
		body = Mappers.physics.get(entity).body;
	}

	@Override
	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity() {
		return body.getAngularVelocity();
	}

	@Override
	public float getBoundingRadius() {
		return boundingRadius;
	}

	@Override
	public boolean isTagged() {
		return tagged;
	}

	@Override
	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}

	@Override
	public float getZeroLinearSpeedThreshold() {
		return 0.001f;
	}

	@Override
	public void setZeroLinearSpeedThreshold(float value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getMaxLinearSpeed() {
		return maxLinearSpeed;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed;
	}

	@Override
	public float getMaxLinearAcceleration() {
		return maxLinearAcceleration;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;
	}

	@Override
	public float getMaxAngularSpeed() {
		return maxAngularSpeed;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}

	@Override
	public float getMaxAngularAcceleration() {
		return maxAngularAcceleration;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration;
	}

	@Override
	public Vector2 getPosition() {
		return body.getPosition();
	}

	@Override
	public float getOrientation() {
		return body.getAngle();
	}

	@Override
	public void setOrientation(float orientation) {
		body.setTransform(getPosition(), orientation);
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		return (float) Math.atan2(-vector.x, vector.y);
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		outVector.x = -(float) Math.sin(angle);
		outVector.y = (float) Math.cos(angle);
		return outVector;
	}

	@Override
	public Location<Vector2> newLocation() {
		return new Box2dLocation();
	}

	public void update(float delta) {
		if (Mappers.rubberbanding.get(entity) != null) {
			Mappers.rubberbanding.get(entity).arriveBehavior.calculateSteering(steeringOutput);
			applyRubberbanding(steeringOutput, delta);
		}

		if (Mappers.aiSteering.get(entity) != null) {
			Mappers.aiSteering.get(entity).steeringBehavior.calculateSteering(steeringOutput);
			applyAI(steeringOutput, delta);
		}
	}

	private void applyRubberbanding(SteeringAcceleration<Vector2> steering, float deltaTime) {
		if (!steering.linear.isZero()) {
			if (Mappers.rubberbanding.get(entity).idealPosition.getPosition().dst(Mappers.physics.get(entity).body.getPosition()) < 20) {
				body.applyForceToCenter(steering.linear, true);
			} else {
				Mappers.physics.get(entity).body.setTransform(Mappers.rubberbanding.get(entity).idealPosition.getPosition(), 0);
			}
		}
	}

	private void applyAI(SteeringAcceleration<Vector2> steering, float deltaTime) {

	}
}
