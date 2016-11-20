package com.mattmohandiss.networkedShooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.mattmohandiss.networkedShooter.Launchers.GameClient;
import com.mattmohandiss.networkedShooter.networking.Client;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Matthew on 11/20/16.
 */
public class ClientSetupScreen extends ScreenAdapter {
	private final TextField textField;
	private final TextButton button;
	private final Label errorLabel;
	public GameClient gameClient;
	private Stage stage;
	private Timer timer = new Timer();
	private InputMultiplexer multiplexer = new InputMultiplexer();

	public ClientSetupScreen(GameClient gameClient) {
		this.gameClient = gameClient;
		VisUI.load();
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);

		textField = new TextField("127.0.0.1:8855", VisUI.getSkin());
		textField.setMaxLength(39);
		textField.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				textField.setText("");
			}
		});
		table.add(textField).center();

		errorLabel = new Label("invalid address", VisUI.getSkin());
		errorLabel.setVisible(false);

		button = new TextButton("Continue", VisUI.getSkin());
		button.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				try {
					Client client;
					client = new Client(new URI("ws://" + textField.getText()));
					gameClient.setScreen(new GameScreen(gameClient, client));
				} catch (URISyntaxException e) {
					timer.scheduleTask(new Timer.Task() {
						@Override
						public void run() {
							if (errorLabel.isVisible()) {
								errorLabel.setVisible(false);
							} else {
								errorLabel.setVisible(true);
							}
						}
					}, 0, 2, 1);
				}
			}
		});
		table.row();
		table.add(button).center().padTop(25);

		table.row();
		table.add(errorLabel).center().padTop(25);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
