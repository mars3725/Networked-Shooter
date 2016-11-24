package com.mattmohandiss.networkedShooter.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mattmohandiss.networkedShooter.Components.RubberbandingComponent;
import com.mattmohandiss.networkedShooter.Enums.PlayerState;
import com.mattmohandiss.networkedShooter.Mappers;

/**
 * Created by Matthew on 11/21/16.
 */
public class RubberbandingSystem extends IteratingSystem {

	public RubberbandingSystem() {
		super(Family.all(RubberbandingComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if (Mappers.stateMachine.get(entity).stateMachine.isInState(PlayerState.Idle) || (Mappers.rubberbanding.get(entity).idealPosition.getPosition().dst(Mappers.physics.get(entity).body.getPosition()) > 5)) {
			Mappers.rubberbanding.get(entity).steerable.update(deltaTime);
		}
	}
}
