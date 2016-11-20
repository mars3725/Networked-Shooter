package com.mattmohandiss.war.Enums;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/**
 * Created by Matthew on 9/23/16.
 */
public enum EnemyState implements State<Entity> {
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
