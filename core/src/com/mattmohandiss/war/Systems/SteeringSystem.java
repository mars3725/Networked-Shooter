package com.mattmohandiss.war.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mattmohandiss.war.Components.SteeringComponent;
import com.mattmohandiss.war.Mappers;

/**
 * Created by Matthew on 7/22/16.
 */
public class SteeringSystem extends IteratingSystem {

	public SteeringSystem() {
		super(Family.all(SteeringComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Mappers.steering.get(entity).steerable.update(deltaTime);
	}
}
