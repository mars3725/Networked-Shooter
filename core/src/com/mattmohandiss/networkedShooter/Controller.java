package com.mattmohandiss.networkedShooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mattmohandiss.networkedShooter.Enums.ControllerState;
import com.mattmohandiss.networkedShooter.Enums.MessageType;
import com.mattmohandiss.networkedShooter.Screens.GameScreen;
import com.mattmohandiss.networkedShooter.networking.Message;

import static com.mattmohandiss.networkedShooter.Enums.ControllerState.Idle;

/**
 * Created by Matthew on 9/18/16.
 */
public class Controller implements InputProcessor {
	GameScreen game;

	public Controller(GameScreen gameScreen) {
		this.game = gameScreen;
	}

	@Override
	public boolean keyDown(int keycode) {
		updateControlDirection();

		if (Mappers.movement.get(game.getPlayer()).changed) {
			if (Mappers.stateMachine.get(game.getPlayer()).stateMachine.isInState(Idle)) {
				Mappers.stateMachine.get(game.getPlayer()).stateMachine.changeState(ControllerState.Moving);
				Mappers.networking.get(game.getPlayer()).game.client.send(new Message(MessageType.changeState, Mappers.networking.get(game.getPlayer()).game.playerID, new int[]{ControllerState.Moving.ordinal(), ((int) Mappers.movement.get(game.getPlayer()).direction.x), ((int) Mappers.movement.get(game.getPlayer()).direction.y)}));
			} else {
				Mappers.networking.get(game.getPlayer()).game.client.send(new Message(MessageType.velocity, Mappers.networking.get(game.getPlayer()).game.playerID, new int[]{((int) Mappers.movement.get(game.getPlayer()).direction.x), ((int) Mappers.movement.get(game.getPlayer()).direction.y)}));
			}
		}

		if (keycode == Input.Keys.ESCAPE) {
			game.client.send(new Message(MessageType.removePlayer, game.playerID));
			Mappers.stateMachine.get(game.getPlayer()).stateMachine.changeState(ControllerState.Dead);
		}

		return false;
	}

	private void updateControlDirection() {
		Vector2 direction = new Vector2(0, 0);
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			direction.x = -10;
		} else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			direction.x = 10;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			direction.y = 10;
		} else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			direction.y = -10;
		}

		Mappers.movement.get(game.getPlayer()).changed = !Mappers.movement.get(game.getPlayer()).direction.equals(direction);
		Mappers.movement.get(game.getPlayer()).direction.set(direction);
	}

	@Override
	public boolean keyUp(int keycode) {
		updateControlDirection();

		if (Mappers.movement.get(game.getPlayer()).direction.isZero()) {
			Mappers.stateMachine.get(game.getPlayer()).stateMachine.changeState(ControllerState.Idle);
			Mappers.networking.get(game.getPlayer()).game.client.send(new Message(MessageType.changeState, game.playerID, new int[]{ControllerState.Idle.ordinal()}));
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 adjustedCoords = game.viewport.getCamera().unproject(new Vector3(screenX, screenY, 0),
				game.viewport.getScreenX(), game.viewport.getScreenY(),
				game.viewport.getScreenWidth(), game.viewport.getScreenHeight());
		adjustedCoords.set(((int) adjustedCoords.x), ((int) adjustedCoords.y), 0);

		game.localWorld.fireBullet(game.playerID, adjustedCoords.cpy());
		game.client.send(new Message(MessageType.fireBullet, game.playerID, new int[]{((int) adjustedCoords.x), ((int) adjustedCoords.y)}));
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
