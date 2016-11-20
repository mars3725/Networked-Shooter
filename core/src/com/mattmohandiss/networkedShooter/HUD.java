package com.mattmohandiss.networkedShooter;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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

	public HUD(GameScreen game) {
		this.game = game;
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera());
		stage = new Stage(viewport, new SpriteBatch());
		Engine eng = new Engine();

		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);

		label = new Label(null, Assets.labelStyle);
		table.top().add(label).expandX().uniform().center();

		label2 = new Label(null, Assets.labelStyle);
		table.top().add(label2).expandX().uniform().center();

		label3 = new Label(null, Assets.labelStyle);
		table.top().add(label3).expandX().uniform().center();

		label4 = new Label(null, Assets.labelStyle);
		table.top().add(label4).expandX().uniform().center();
	}

	public void update() {
		label.setText("(" + ((int) Mappers.physics.get(game.getPlayer()).body.getPosition().x) + "," + ((int) Mappers.physics.get(game.getPlayer()).body.getPosition().y) + ")");
		label2.setText(Mappers.physics.get(game.getPlayer()).body.getLinearVelocity().toString());
		label3.setText(Mappers.stateMachine.get(game.getPlayer()).stateMachine.getCurrentState().toString());
		label4.setText(String.valueOf(Gdx.graphics.getFramesPerSecond()));
	}
}
