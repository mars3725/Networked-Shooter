package com.mattmohandiss.networkedShooter.Screens;

import com.badlogic.gdx.Gdx;
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
import com.mattmohandiss.networkedShooter.Assets;
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
	private final Label titleLabel;
	public GameClient gameClient;
	private Stage stage = new Stage(new ScreenViewport());
	private Timer timer = new Timer();
	private Table table = new Table();

	public ClientSetupScreen(GameClient gameClient) {
		this.gameClient = gameClient;
		Gdx.input.setInputProcessor(stage);

		table.setFillParent(true);

		textField = new TextField("127.0.0.1:8855", VisUI.getSkin());
		textField.setMaxLength(39);
		textField.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				textField.setText("");
			}
		});

		errorLabel = new Label("invalid address", VisUI.getSkin());
		errorLabel.setVisible(false);

		titleLabel = new Label("Networked-Shooter Client", Assets.titleLabelStyle);

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

		table.add(titleLabel).center();
		table.row();
		table.add(textField).center().padTop(50);
		table.row();
		table.add(button).center().padTop(25);
		table.row();
		table.add(errorLabel).center().padTop(25);
		stage.addActor(table);
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
