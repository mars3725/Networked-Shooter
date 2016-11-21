package com.mattmohandiss.networkedShooter.Screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mattmohandiss.networkedShooter.GameWorld;
import com.mattmohandiss.networkedShooter.HUD;
import com.mattmohandiss.networkedShooter.Mappers;
import com.mattmohandiss.networkedShooter.networking.Client;


/**
 * Created by Matthew on 9/12/16.
 */
public class GameScreen extends ScreenAdapter {
	public GameClient gameClient;
	public HUD hud;
	public GameWorld localWorld;
	public Client client;
	public Integer playerID;
	public Viewport viewport;
	private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

	public GameScreen(GameClient gameClient, Client client) {
		this.gameClient = gameClient;
		this.client = client;
		viewport = new FillViewport(gameClient.WindowWidth, gameClient.WindowHeight);
		client.game = this;
		localWorld = new GameWorld(client);
		localWorld.create();
		hud = new HUD(this);
		client.connect();
	}

	@Override
	public void render(float delta) {
		if (playerID != null) {
			viewport.apply();
			Vector2 position = Mappers.physics.get(getPlayer()).body.getPosition();
			viewport.getCamera().position.set(position.x, position.y, 0);
			viewport.getCamera().update();

			debugRenderer.render(localWorld.world, viewport.getCamera().combined);

			hud.stage.getViewport().apply();
			hud.update();
			hud.stage.draw();

			localWorld.update(delta);
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	public Entity getPlayer() {
		return localWorld.getPlayer(playerID);
	}
}
