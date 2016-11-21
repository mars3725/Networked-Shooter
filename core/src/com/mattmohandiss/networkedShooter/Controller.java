package com.mattmohandiss.networkedShooter;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.mattmohandiss.networkedShooter.Enums.ControllerState;
import com.mattmohandiss.networkedShooter.Enums.GameState;
import com.mattmohandiss.networkedShooter.Enums.MessageType;
import com.mattmohandiss.networkedShooter.Screens.GameScreen;
import com.mattmohandiss.networkedShooter.networking.Message;

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
		if (keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT || keycode == Input.Keys.UP || keycode == Input.Keys.DOWN) {
			Mappers.stateMachine.get(game.getPlayer()).stateMachine.changeState(ControllerState.Moving);
		} else if (keycode == Input.Keys.ESCAPE) {
			game.client.send(new Message(MessageType.removePlayer, game.playerID));
			game.gameState = GameState.finished;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
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
