package com.mattmohandiss.networkedShooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.VisUI;

/**
 * Created by Matthew on 9/19/16.
 */
public class Assets {
	public static Label.LabelStyle titleLabelStyle;
	public static Label.LabelStyle hudLabelStyle;

	public static void load() {
		VisUI.load();
		BitmapFont font = new BitmapFont(Gdx.files.internal("Fonts/visitor.fnt"));
		titleLabelStyle = new Label.LabelStyle(font, Color.WHITE);
		hudLabelStyle = new Label.LabelStyle(font, Color.WHITE);
	}
}
