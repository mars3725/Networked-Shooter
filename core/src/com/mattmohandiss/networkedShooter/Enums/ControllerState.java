package com.mattmohandiss.networkedShooter.Enums;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.mattmohandiss.networkedShooter.Mappers;
import com.mattmohandiss.networkedShooter.Screens.ClientSetupScreen;
import com.mattmohandiss.networkedShooter.networking.Message;

import java.util.TimerTask;

/**
 * Created by Matthew on 9/19/16.
 */
public enum ControllerState implements State<Entity> {
	Moving {
		@Override
		public void enter(Entity entity) {
			Mappers.networking.get(entity).game.client.send(new Message(MessageType.changeState, Mappers.networking.get(entity).game.playerID, new int[]{((ControllerState) Mappers.stateMachine.get(entity).stateMachine.getCurrentState()).ordinal()}));

			Mappers.networking.get(entity).timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (Mappers.stateMachine.get(entity).stateMachine.getCurrentState() == Moving) {
						Mappers.networking.get(entity).game.client.send(new Message(MessageType.velocity, Mappers.networking.get(entity).game.playerID, new int[]{((int) Mappers.physics.get(entity).body.getLinearVelocity().x), ((int) Mappers.physics.get(entity).body.getLinearVelocity().y)}));
					} else {
						cancel();
					}
				}
			}, 0, 250);
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
			Mappers.networking.get(entity).game.client.send(new Message(MessageType.velocity, Mappers.networking.get(entity).game.playerID, new int[]{0, 0}));
			Mappers.physics.get(entity).body.setLinearVelocity(0, 0);
		}
	},

	Idle {
		@Override
		public void enter(Entity entity) {
			Mappers.networking.get(entity).game.client.send(new Message(MessageType.changeState, Mappers.networking.get(entity).game.playerID, new int[]{((ControllerState) Mappers.stateMachine.get(entity).stateMachine.getCurrentState()).ordinal()}));

			Mappers.networking.get(entity).timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (Mappers.stateMachine.get(entity).stateMachine.getCurrentState() == Idle) {
						Mappers.networking.get(entity).game.client.send(new Message(MessageType.position, Mappers.networking.get(entity).game.playerID, new int[]{(int) (Mappers.physics.get(entity).body.getPosition().x * 100), (int) (Mappers.physics.get(entity).body.getPosition().y * 100)}));
					} else {
						cancel();
					}
				}
			}, 0, 250);
		}

		@Override
		public void update(Entity entity) {
			Mappers.physics.get(entity).body.setLinearVelocity(0, 0);
		}

		@Override
		public void exit(Entity entity) {

		}
	},

	Dead {
		@Override
		public void enter(Entity entity) {
			Mappers.networking.get(entity).game.client.close();
			Mappers.networking.get(entity).game.gameClient.setScreen(new ClientSetupScreen(Mappers.networking.get(entity).game.gameClient));
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
