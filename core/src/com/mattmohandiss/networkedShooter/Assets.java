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
	public static Label.LabelStyle labelStyle;

	public static void load() {
		BitmapFont font = new BitmapFont(Gdx.files.internal("Fonts/visitor.fnt"));
		VisUI.load();
		labelStyle = new Label.LabelStyle(font, Color.LIGHT_GRAY);
	}
}
