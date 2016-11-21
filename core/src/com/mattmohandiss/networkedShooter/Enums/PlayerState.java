package com.mattmohandiss.networkedShooter.Enums;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.mattmohandiss.networkedShooter.Mappers;

/**
 * Created by Matthew on 9/19/16.
 */
public enum PlayerState implements State<Entity> {
	Moving {
		@Override
		public void enter(Entity entity) {

		}

		@Override
		public void update(Entity entity) {

		}

		@Override
		public void exit(Entity entity) {

		}
	},

	Idle {
		@Override
		public void enter(Entity entity) {

		}

		@Override
		public void update(Entity entity) {
			Mappers.physics.get(entity).body.setLinearVelocity(0, 0);
		}

		@Override
		public void exit(Entity entity) {

		}
	};

	@Override
	public boolean onMessage(Entity entity, Telegram telegram) {
		return false;
	}
}
