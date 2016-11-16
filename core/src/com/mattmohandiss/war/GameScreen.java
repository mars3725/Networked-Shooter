package com.mattmohandiss.war;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.mattmohandiss.war.Enums.GameState;
import com.mattmohandiss.war.Launchers.GameClient;
import com.mattmohandiss.war.networking.Client;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by Matthew on 9/12/16.
 */
public class GameScreen extends ScreenAdapter {
	public GameClient gameClient;
	public HUD hud;
	public GameWorld localWorld;
	public Client client;
	public Integer playerID = -1;
	public GameState gameState = GameState.loading;
	private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

	public GameScreen(GameClient gameClient) {
		this.gameClient = gameClient;

		try {
			Client client;
			client = new Client(new URI("ws://localhost:8855"));
			this.client = client;
			client.game = this;
			localWorld = new GameWorld(client);
			localWorld.create();
			hud = new HUD(this);
			client.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(float delta) {
		if (gameState == GameState.inProgress) {
			gameClient.viewport.apply();
			Vector2 position = Mappers.physics.get(getPlayer()).body.getPosition();
			gameClient.camera.position.set(position.x, position.y, 0);
			gameClient.camera.update();

			debugRenderer.render(localWorld.world, gameClient.camera.combined);

			hud.stage.getViewport().apply();
			hud.update();
			hud.stage.draw();

			localWorld.update(delta);
		} else if (gameState == GameState.finished) {
			Gdx.app.exit();
		}
	}

	public Entity getPlayer() {
		return localWorld.players.get(playerID);
	}
}
