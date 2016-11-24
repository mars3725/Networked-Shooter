package com.mattmohandiss.networkedShooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mattmohandiss.networkedShooter.Screens.GameScreen;

/**
 * Created by Matthew on 9/18/16.
 */
public class HUD {
	public Stage stage;
	private Viewport viewport;
	private GameScreen game;

	private Label label;
	private Label label2;
	private Label label3;
	private Label label4;
	private Table table;

	public HUD(GameScreen game) {
		this.game = game;
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera());
		stage = new Stage(viewport);

		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);

		label = new Label(null, Assets.hudLabelStyle);
		label.setFontScale(0.75f);
		table.top().add(label).pad(0, 25, 0, 25).expandX().center().uniform();

		label2 = new Label(null, Assets.hudLabelStyle);
		label2.setFontScale(0.75f);
		table.top().add(label2).pad(0, 25, 0, 25).expandX().center().uniform();

		label3 = new Label(null, Assets.hudLabelStyle);
		label3.setFontScale(0.75f);
		table.top().add(label3).pad(0, 25, 0, 25).expandX().center().uniform();

		label4 = new Label(null, Assets.hudLabelStyle);
		label4.setFontScale(0.75f);
		table.top().add(label4).pad(0, 25, 0, 25).expandX().center().uniform();
	}

	public void update() {
		label.setText("(" + ((int) Mappers.physics.get(game.getPlayer()).body.getPosition().x) + "," + ((int) Mappers.physics.get(game.getPlayer()).body.getPosition().y) + ")");
		label2.setText(Mappers.movement.get(game.getPlayer()).direction.toString());
		label3.setText(Mappers.stateMachine.get(game.getPlayer()).stateMachine.getCurrentState().toString());
		label4.setText(String.valueOf(Gdx.graphics.getFramesPerSecond()));
	}

	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
