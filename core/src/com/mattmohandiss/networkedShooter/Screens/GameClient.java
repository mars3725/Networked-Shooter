package com.mattmohandiss.networkedShooter.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mattmohandiss.networkedShooter.Assets;

public class GameClient extends Game {
	public final int WindowWidth = 200;
	public final int WindowHeight = 200;
	public OrthographicCamera camera;

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		//Settings.load()
		Assets.load();

		camera = new OrthographicCamera(WindowWidth, WindowHeight);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
		//viewport = new FillViewport(camera.viewportWidth, camera.viewportHeight, camera);
		setScreen(new ClientSetupScreen(this));
	}

	@Override
	public void resize(int width, int height) {
		//viewport.update(width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}
}
