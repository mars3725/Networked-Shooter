package com.mattmohandiss.war.Enums;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.Timer;
import com.mattmohandiss.war.Mappers;
import com.mattmohandiss.war.networking.Message;

/**
 * Created by Matthew on 9/19/16.
 */
public enum PlayerState implements State<Entity> {
	Moving {
		@Override
		public void enter(Entity entity) {
			//Mappers.networking.get(entity).game.client.send(new Message(MessageType.changeState, Mappers.networking.get(entity).game.playerID, new int[]{((PlayerState) Mappers.stateMachine.get(entity).stateMachine.getCurrentState()).ordinal()}));
			Mappers.networking.get(entity).timer.scheduleTask(new Timer.Task() {
				@Override
				public void run() {
					if (Mappers.stateMachine.get(entity).stateMachine.getCurrentState() == Moving) {
						Mappers.networking.get(entity).game.client.send(new Message(MessageType.position, Mappers.networking.get(entity).game.playerID, new int[]{((int) Mappers.physics.get(entity).body.getPosition().x), (int) Mappers.physics.get(entity).body.getPosition().y}));
						Mappers.networking.get(entity).game.client.send(new Message(MessageType.velocity, Mappers.networking.get(entity).game.playerID, new int[]{((int) Mappers.physics.get(entity).body.getLinearVelocity().x), ((int) Mappers.physics.get(entity).body.getLinearVelocity().y)}));
					} else {
						Mappers.networking.get(entity).timer.stop();
					}
				}
			}, 0, .25f);
		}

		@Override
		public void update(Entity entity) {
			Mappers.movement.get(entity).direction.set(0, 0);
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				Mappers.movement.get(entity).direction.x = -10;
			} else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				Mappers.movement.get(entity).direction.x = 10;
			}

			if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
				Mappers.movement.get(entity).direction.y = 10;
			} else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
				Mappers.movement.get(entity).direction.y = -10;
			}

			Mappers.physics.get(entity).body.setLinearVelocity(Mappers.movement.get(entity).direction);

			if (Mappers.movement.get(entity).direction.isZero()) {
				Mappers.stateMachine.get(entity).stateMachine.changeState(Idle);
			}
		}

		@Override
		public void exit(Entity entity) {
			Mappers.physics.get(entity).body.setLinearVelocity(0, 0);
		}
	},

	Idle {
		@Override
		public void enter(Entity entity) {
			//here's more text
			//Mappers.networking.get(entity).game.client.send(new Message(MessageType.changeState, Mappers.networking.get(entity).game.playerID, new int[]{((PlayerState) Mappers.stateMachine.get(entity).stateMachine.getCurrentState()).ordinal()}));
			Mappers.networking.get(entity).timer.scheduleTask(new Timer.Task() {
				@Override
				public void run() {
					if (Mappers.stateMachine.get(entity).stateMachine.getCurrentState() == Idle) {
						Mappers.networking.get(entity).game.client.send(new Message(MessageType.position, Mappers.networking.get(entity).game.playerID, new int[]{((int) Mappers.physics.get(entity).body.getPosition().x), (int) Mappers.physics.get(entity).body.getPosition().y}));
					} else {
						Mappers.networking.get(entity).timer.stop();
					}
				}
			}, 0, .25f);
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
