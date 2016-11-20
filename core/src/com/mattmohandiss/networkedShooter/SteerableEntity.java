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
	final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());
	Body body;
	Entity entity;
	float boundingRadius = 0;
	boolean tagged;
	float maxLinearSpeed = 10;
	float maxLinearAcceleration = 10;
	float maxAngularSpeed = 10;
	float maxAngularAcceleration = 10;

	public SteerableEntity(Entity entity) {
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

		Mappers.steering.get(entity).steeringBehavior.calculateSteering(steeringOutput);
		applySteering(steeringOutput, delta);
	}

	protected void applySteering(SteeringAcceleration<Vector2> steering, float deltaTime) {
		body.setLinearVelocity(steering.linear);
		body.setAngularVelocity(steering.angular);
	}
}
